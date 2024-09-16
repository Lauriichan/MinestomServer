package me.lauriichan.minecraft.minestom.server.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.source.AnnotationMessageSource;
import me.lauriichan.laylib.localization.source.EnumMessageSource;
import me.lauriichan.laylib.localization.source.IMessageDefinition;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.StackTracker;
import me.lauriichan.minecraft.minestom.server.MinestomArguments;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.server.module.ModuleDescription.ModuleDescriptionException;
import me.lauriichan.minecraft.minestom.server.permission.PermissionProvider;
import me.lauriichan.minecraft.minestom.server.permission.RegisterPermissionProviderSignal;
import me.lauriichan.minecraft.minestom.server.permission.SelectPermissionProviderSignal;
import me.lauriichan.minecraft.minestom.server.resource.DefaultResourceProviders;
import me.lauriichan.minecraft.minestom.server.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.signal.ISignalHandlerExtension;
import me.lauriichan.minecraft.minestom.server.signal.SignalManager;
import me.lauriichan.minecraft.minestom.server.translation.ITranslationExtension;
import me.lauriichan.minecraft.minestom.server.translation.provider.SimpleMessageProviderFactory;
import me.lauriichan.minecraft.minestom.server.util.instance.SharedInstances;
import me.lauriichan.minecraft.minestom.server.util.instance.SimpleInstanceInvoker;
import me.lauriichan.minecraft.minestom.server.util.logger.Slf4jSimpleLogger;

public final class SystemModule implements IMinestomModule {

    public static final String ID = "system";

    private static final class SystemModuleDescription implements IModuleDescription {
        private static final Dependency SYSTEM = new Dependency(ID, "*", Version.ANY, Version.ANY, true);

        private final Model model;
        private final Version version;

        private SystemModuleDescription(IDataSource source) throws IOException, ModuleDescriptionException {
            try (BufferedReader reader = source.openReader()) {
                this.model = ModuleDescription.MAVEN_READER.read(reader);
            } catch (IllegalStateException | IOException | XmlPullParserException e) {
                throw new ModuleDescriptionException("Failed to parse pom.xml", e);
            }
            this.version = ModuleDescription.parseVersion(model.getVersion());
        }

        @Override
        public Model mavenModel() {
            return model;
        }

        @Override
        public String id() {
            return ID;
        }

        @Override
        public Version version() {
            return version;
        }

        @Override
        public ObjectList<Dependency> dependencies() {
            return ObjectLists.emptyList();
        }

        @Override
        public Optional<Dependency> dependency(String id) {
            return Optional.empty();
        }

        @Override
        public Dependency systemDependency() {
            return SYSTEM;
        }
    }

    private final ISimpleLogger logger;

    private final MinestomServer server;
    private final MinestomModuleManager moduleManager;

    private final Path jarRoot;
    private final Path dataRoot;

    private final ResourceManager resourceManager;
    private final MessageManager messageManager;

    private final ModuleActorMap actorMap;

    private final SystemModuleDescription description;
    private final MavenLibraryLoader libraryLoader;

    private final SimpleInstanceInvoker invoker = new SimpleInstanceInvoker();
    private final SharedInstances<IExtension> sharedExtensions = new SharedInstances<>(invoker);

    public SystemModule(MinestomServer server) {
        if (server.systemModule() != null) {
            throw new UnsupportedOperationException("Only one instance allowed per server");
        }
        this.logger = new Slf4jSimpleLogger(LoggerFactory.getLogger("System"));
        this.server = server;
        this.moduleManager = new MinestomModuleManager(logger, server);
        this.jarRoot = moduleManager.createJarRoot(MinestomServer.class);
        this.dataRoot = Paths.get(MinestomArguments.SYSTEM_DATA_DIR.value());
        this.resourceManager = new ResourceManager(this);
        DefaultResourceProviders.setDefaults(resourceManager);
        this.messageManager = new MessageManager();
        this.actorMap = new ModuleActorMap(this);
        try {
            IDataSource source = resource("jar://META-INF/maven/me.lauriichan.minecraft/minestom/pom.xml");
            if (!source.exists()) {
                source = resource("fs://pom.xml");
            }
            this.description = new SystemModuleDescription(source);
        } catch (ModuleDescriptionException | IOException e) {
            throw new IllegalStateException("Failed to read system version", e);
        }
        this.libraryLoader = new MavenLibraryLoader(logger, description.mavenModel());
    }

    public void load() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        moduleManager.loadModules();
    }

    public void registerSignalHandlers() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        SignalManager signalManager = server.signalManager();
        extension(ISignalHandlerExtension.class, true).callInstances(signalManager::register);
    }

    public PermissionProvider setupPermissionProvider() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        SignalManager signalManager = server.signalManager();
        RegisterPermissionProviderSignal registerSignal = new RegisterPermissionProviderSignal();
        signalManager.call(registerSignal);
        ObjectList<PermissionProvider> providers = registerSignal.providers();
        SelectPermissionProviderSignal selectSignal = new SelectPermissionProviderSignal(providers);
        signalManager.call(selectSignal);
        String id = selectSignal.selected();
        if (id == null) {
            return null;
        }
        return providers.stream().filter(prov -> prov.id().equals(id)).findAny().orElse(null);
    }

    public void start() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        registerTranslations();
        moduleManager.postStartModules();
    }

    @SuppressWarnings({
        "unchecked",
        "rawtypes"
    })
    private void registerTranslations() {
        final IExtensionPool<ITranslationExtension> pool = extension(ITranslationExtension.class, false);
        final SimpleMessageProviderFactory factory = new SimpleMessageProviderFactory();
        pool.callClasses((module, extension) -> {
            if (extension.isEnum()) {
                if (!IMessageDefinition.class.isAssignableFrom(extension)) {
                    return;
                }
                module.messageManager().register(new EnumMessageSource((Class) extension, factory));
                return;
            }
            module.messageManager().register(new AnnotationMessageSource(extension, factory));
        });
    }

    public void callServerReady() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        moduleManager.serverReadyModules();
    }

    public void callServerShutdown() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        moduleManager.serverShutdownModules();
    }

    @Override
    public IDataSource resource(String path) {
        return resourceManager.resolve(path);
    }

    @Override
    public <E extends IExtension> IExtensionPool<E> extension(Class<E> type, boolean instantiate) {
        return new ExtensionPoolImpl<>(this, type, instantiate);
    }

    @Override
    public <E extends IExtension> IExtensionPool<E> extension(Class<? extends IExtension> extensionType, Class<E> type,
        boolean instantiate) {
        return new ExtensionPoolImpl<>(this, extensionType, type, instantiate);
    }

    @Override
    public boolean dependsOn(IMinestomModule module) {
        return false;
    }

    /*
     * Getter
     */

    MavenLibraryLoader libraryLoader() {
        return libraryLoader;
    }

    @Override
    public ClassLoader classLoader() {
        return MinestomServer.class.getClassLoader();
    }

    @Override
    public MinestomServer server() {
        return server;
    }

    @Override
    public SignalManager signalManager() {
        return server.signalManager();
    }

    @Override
    public IModuleDescription description() {
        return description;
    }

    @Override
    public ISimpleLogger logger() {
        return logger;
    }

    @Override
    public Path jarRoot() {
        return jarRoot;
    }

    @Override
    public Path dataRoot() {
        return dataRoot;
    }

    @Override
    public IModuleManager moduleManager() {
        return moduleManager;
    }

    @Override
    public ResourceManager resourceManager() {
        return resourceManager;
    }

    @Override
    public SimpleInstanceInvoker invoker() {
        return invoker;
    }

    @Override
    public SharedInstances<IExtension> sharedExtensions() {
        return sharedExtensions;
    }

    @Override
    public MessageManager messageManager() {
        return messageManager;
    }

    @Override
    public ModuleActorMap actorMap() {
        return actorMap;
    }

    @Override
    public IConditionMap conditionMap() {
        return IConditionMap.empty();
    }

    @Override
    public ConfigMigrator configMigrator() {
        return server.configMigrator();
    }

    @Override
    public ConfigManager configManager() {
        return server.configManager();
    }

}
