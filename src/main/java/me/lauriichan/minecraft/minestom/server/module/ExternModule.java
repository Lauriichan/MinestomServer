package me.lauriichan.minecraft.minestom.server.module;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.LoggerFactory;

import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.server.module.ExtensionPoolImpl.ConditionMapImpl;
import me.lauriichan.minecraft.minestom.server.module.ModuleClassLoader.InvalidModuleException;
import me.lauriichan.minecraft.minestom.server.module.ModuleClassLoader.ModuleCreator;
import me.lauriichan.minecraft.minestom.server.resource.DefaultResourceProviders;
import me.lauriichan.minecraft.minestom.server.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.util.instance.ISharedInstances;
import me.lauriichan.minecraft.minestom.server.util.instance.SharedInstancesDelegated;
import me.lauriichan.minecraft.minestom.server.util.instance.SimpleInstanceInvoker;
import me.lauriichan.minecraft.minestom.server.util.logger.Slf4jSimpleLogger;

public final class ExternModule<M extends MinestomModule> implements IMinestomModule {

    private final MinestomServer server;
    private final ModuleDescription description;

    private final ISimpleLogger logger;

    private final File jarFile;

    private final Path jarRoot;
    private final Path dataRoot;

    private final ResourceManager resourceManager;
    private final MessageManager messageManager;

    private final SimpleInstanceInvoker invoker;
    private final SharedInstancesDelegated<IExtension> sharedExtensions;

    private final ConditionMapImpl conditionMap;

    private final ModuleClassLoader classLoader;
    private final Class<M> moduleClass;
    private final M moduleInstance;

    ExternModule(final MinestomServer server, final ModuleDescription description, final File jarFile, final Path jarRoot,
        final ModuleClassLoader classLoader, final Class<M> moduleClass, final ModuleCreator<M> instanceCreator)
        throws InvalidModuleException {
        this.description = description;
        this.server = server;
        this.logger = new Slf4jSimpleLogger(LoggerFactory.getLogger(description.name()));
        this.jarFile = jarFile;
        this.jarRoot = jarRoot;
        this.dataRoot = server.moduleManager().moduleDataRoot().resolve(description.id());
        this.resourceManager = new ResourceManager(this);
        DefaultResourceProviders.setDefaults(resourceManager);
        this.messageManager = new MessageManager();
        this.invoker = new SimpleInstanceInvoker(server.systemModule().invoker());
        this.sharedExtensions = new SharedInstancesDelegated<>(server.systemModule().sharedExtensions(), invoker);
        this.conditionMap = new ConditionMapImpl();
        this.classLoader = classLoader;
        this.moduleClass = moduleClass;
        this.moduleInstance = instanceCreator.newInstance(this);
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
        return description.dependency(module.description().id()).isPresent();
    }

    /*
     * Getter
     */
    
    @Override
    public ModuleClassLoader classLoader() {
        return classLoader;
    }

    public File moduleJarFile() {
        return jarFile;
    }

    public Class<M> moduleClass() {
        return moduleClass;
    }

    public M moduleInstance() {
        return moduleInstance;
    }

    @Override
    public MinestomServer server() {
        return server;
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
    public ResourceManager resourceManager() {
        return resourceManager;
    }

    @Override
    public IModuleManager moduleManager() {
        return server.moduleManager();
    }

    @Override
    public SimpleInstanceInvoker invoker() {
        return invoker;
    }

    @Override
    public ISharedInstances<IExtension> sharedExtensions() {
        return sharedExtensions;
    }

    @Override
    public MessageManager messageManager() {
        return messageManager;
    }

    @Override
    public IConditionMap conditionMap() {
        return conditionMap;
    }

    @Override
    public ConfigMigrator configMigrator() {
        return server.systemModule().configMigrator();
    }

    @Override
    public ConfigManager configManager() {
        return server.systemModule().configManager();
    }

}
