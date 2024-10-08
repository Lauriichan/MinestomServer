package me.lauriichan.minecraft.minestom.server.module;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.maven.model.Model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.reflection.ClassUtil;
import me.lauriichan.minecraft.minestom.server.MinestomArguments;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.module.ExtensionPoolImpl.ConditionMapImpl;
import me.lauriichan.minecraft.minestom.server.module.ModuleClassLoader.InvalidModuleException;
import me.lauriichan.minecraft.minestom.server.translation.config.MultiTranslationConfig;
import me.lauriichan.minecraft.minestom.server.util.Triple;

final class MinestomModuleManager implements IModuleManager {

    private final ISimpleLogger logger;
    private final MinestomServer server;
    private final ObjectArrayList<IModuleDescription> knownModules = new ObjectArrayList<>();
    private final ObjectArrayList<ModuleClassLoader> loaders = new ObjectArrayList<>();

    private final Path moduleRoot;
    private final Path moduleDataRoot;

    MinestomModuleManager(ISimpleLogger logger, MinestomServer server) {
        if (server.moduleManager() != null) {
            throw new UnsupportedOperationException("Server already has module manager");
        }
        this.logger = logger;
        this.server = server;
        this.moduleRoot = Paths.get(MinestomArguments.MODULE_DIR.value());
        this.moduleDataRoot = Paths.get(MinestomArguments.MODULE_DATA_DIR.value());
    }

    @Override
    public MinestomServer server() {
        return server;
    }

    @Override
    public Path moduleRoot() {
        return moduleRoot;
    }

    @Override
    public Path moduleDataRoot() {
        return moduleDataRoot;
    }

    @Override
    public ObjectList<IMinestomModule> modules() {
        ObjectArrayList<IMinestomModule> modules = loaders.stream().map(loader -> loader.module().moduleInstance())
            .collect(ObjectArrayList.toList());
        modules.add(server.systemModule());
        return ObjectLists.unmodifiable(modules);
    }

    @Override
    public <M extends IMinestomModule> Optional<M> module(Class<M> moduleClass) {
        if (SystemModule.class == moduleClass) {
            return cast(Optional.of(server.systemModule()));
        }
        return cast(loaders.stream().filter(loader -> loader.module().moduleClass() == moduleClass).findFirst()
            .map(loader -> loader.module().moduleInstance()));
    }

    @Override
    public <M extends IMinestomModule> Optional<M> module(String id) {
        String fId = id.toLowerCase();
        if (SystemModule.ID.equals(fId)) {
            return cast(Optional.of(server.systemModule()));
        }
        return cast(loaders.stream().filter(loader -> loader.module().description().id().equals(fId)).findFirst()
            .map(loader -> loader.module().moduleInstance()));
    }
    
    @Override
    public Optional<IMinestomModule> findModule(ClassLoader classLoader) {
        if (classLoader == null) {
            return Optional.empty();
        }
        if (getClass().getClassLoader() == classLoader) {
            return Optional.of(server.systemModule());
        }
        if (classLoader instanceof ModuleClassLoader moduleLoader) {
            return Optional.of(moduleLoader.module().moduleInstance());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <M extends IMinestomModule> Optional<M> cast(Optional<IMinestomModule> optional) {
        return optional.map(module -> {
            try {
                return (M) module;
            } catch (ClassCastException ignore) {
                return null;
            }
        });
    }

    @Override
    public boolean isMavenArtifactKnown(String groupId, String artifactId) {
        return knownModules.stream().anyMatch(description -> {
            Model model = description.mavenModel();
            return model.getGroupId().equals(groupId) && model.getArtifactId().equals(artifactId);
        });
    }

    @Override
    public Class<?> getClassByName(String name) {
        Class<?> clazz = ClassUtil.findClass(name);
        if (clazz != null) {
            return clazz;
        }
        for (ModuleClassLoader loader : loaders) {
            if ((clazz = loader.module().getClassByName(name)) != null) {
                return clazz;
            }
        }
        return null;
    }

    private void preStartModules() {
        ObjectArrayList<MinestomModule> modules = loaders.stream().map(loader -> loader.module().moduleInstance())
            .collect(ObjectArrayList.toList());
        logger.info("Starting {0} modules...", modules.size());
        call(modules, "load", MinestomModule::onModuleLoad);
        call(modules, "condition map setup", module -> {
            ConditionMapImpl map = (ConditionMapImpl) module.conditionMap();
            module.onModuleConditionSetup(map);
            map.lock();
        });
    }

    void postStartModules() {
        ObjectArrayList<MinestomModule> modules = loaders.stream().map(loader -> loader.module().moduleInstance())
            .collect(ObjectArrayList.toList());
        processModule(server.systemModule());
        for (MinestomModule module : modules) {
            processModule(module);
        }
        call(modules, "process", this::processModule);
        call(modules, "start", MinestomModule::onModuleStart);
        call(modules, "ready", MinestomModule::onModuleReady);
        logger.info("Successfully started {0} modules.", loaders.size());
    }

    void serverReadyModules() {
        ObjectArrayList<MinestomModule> modules = loaders.stream().map(loader -> loader.module().moduleInstance())
            .collect(ObjectArrayList.toList());
        call(modules, "server-ready", MinestomModule::onServerReady);
        logger.info("Successfully ran server ready on {0} modules.", loaders.size());
    }

    void serverShutdownModules() {
        ObjectArrayList<MinestomModule> modules = loaders.stream().map(loader -> loader.module().moduleInstance())
            .collect(ObjectArrayList.toList());
        call(modules, "server-shutdown", MinestomModule::onServerShutdown);
        logger.info("Successfully ran server shutdown on {0} modules.", loaders.size());
    }

    private void processModule(IMinestomModule module) {
        ConfigManager manager = server.configManager();
        manager.multiConfigOrCreate(MultiTranslationConfig.class, module);
    }

    private void call(ObjectList<MinestomModule> modules, String phaseName, Consumer<MinestomModule> call) {
        logger.debug("Running phase '{0}' on {1} modules...", phaseName, modules.size());
        for (int index = 0; index < modules.size(); index++) {
            try {
                call.accept(modules.get(index));
            } catch (Throwable exp) {
                MinestomModule module = modules.remove(index--);
                module.logger().error("Failed to run '{0}'", phaseName, exp);
                closeModule(module);
            }
        }
    }

    private void closeModule(MinestomModule module) {
        ModuleClassLoader loader = module.delegate().classLoader();
        loaders.remove(loader);
        try {
            loader.close();
        } catch (IOException e) {
            module.logger().warning("Something went wrong when closing the class loader for module '{0}'");
        }
        try {
            module.jarRoot().getFileSystem().close();
        } catch (IOException e) {
            module.logger().warning("Something went wrong when closing the jar file system for module '{0}'");
        }
    }

    void loadModules() {
        if (!knownModules.isEmpty()) {
            return;
        }
        logger.info("Resolving modules...");
        File directory = moduleRoot.toFile();
        if (!directory.exists()) {
            logger.info("Module root doesn't exist, calling it done.");
            return;
        }
        ObjectArrayList<Triple<File, Path, IModuleDescription>> modulesToLoad = new ObjectArrayList<>();
        Triple<File, Path, IModuleDescription> system = Triple.of(null, null, server.systemModule().description());
        modulesToLoad.add(system);
        for (File file : directory.listFiles()) {
            if (!file.getName().endsWith(".jar")) {
                continue;
            }
            try {
                Path jarRoot = createJarRoot(file);
                modulesToLoad.add(new Triple<>(file, jarRoot, new ModuleDescription(jarRoot)));
            } catch (Exception e) {
                logger.error("Failed to resolve module '" + file.getName() + "'", e);
            }
        }
        modulesToLoad.sort((m1, m2) -> {
            int compare = m2.three().id().compareTo(m1.three().id());
            if (compare != 0) {
                return compare;
            }
            return m2.three().version().compareTo(m1.three().version());
        });
        DependencyGraph graph = new DependencyGraph(modulesToLoad);
        ObjectArrayList<Triple<File, Path, IModuleDescription>> tmp = modulesToLoad;
        modulesToLoad = graph.sorted();
        tmp.removeAll(modulesToLoad);
        tmp.stream().forEach(entry -> {
            try {
                entry.two().getFileSystem().close();
            } catch (IOException ignore) {
            }
        });
        graph.printReport(logger);
        if (modulesToLoad.isEmpty()) {
            logger.info("No modules to load anymore, calling it done.");
            return;
        }
        for (Triple<File, Path, IModuleDescription> entry : modulesToLoad) {
            knownModules.add(entry.three());
        }
        modulesToLoad.remove(system);
        for (Triple<File, Path, IModuleDescription> entry : modulesToLoad) {
            logger.info("Loading module '" + entry.three().name() + "'...");
            ModuleClassLoader loader;
            try {
                loader = new ModuleClassLoader(server, entry.one(), entry.two(), MinestomServer.class.getClassLoader(),
                    (ModuleDescription) entry.three());
            } catch (InvalidModuleException | IOException e) {
                logger.error("Failed module '" + entry.three().name() + "'", e);
                continue;
            }
            loaders.add(loader);
        }
        logger.info("Successfully loaded {0} out of {1} modules.", loaders.size(), modulesToLoad.size());
        preStartModules();
    }

    Class<?> loadClassByName(String name, boolean resolve, ModuleClassLoader caller) {
        for (ModuleClassLoader loader : loaders) {
            if (loader == caller) {
                continue;
            }
            try {
                return loader.loadClass0(name, resolve, false, caller.module().dependsOn(loader.module()));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return null;
    }

    Path createJarRoot(Class<?> clazz) {
        try {
            return createJarRoot(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to retrieve jar path", e);
        }
    }

    private Path createJarRoot(File file) {
        return createJarRoot(file.getAbsolutePath());
    }

    private Path createJarRoot(String jarFilePath) {
        URI uri = null;
        Path path = null;
        try {
            if (!jarFilePath.endsWith("/")) {
                uri = new URI(("jar:file:/" + jarFilePath.replace('\\', '/').replace(" ", "%20") + "!/").replace("//", "/"));
            } else {
                path = Paths.get(jarFilePath.substring(1));
            }
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Failed to build resource uri", e);
        }
        if (uri != null) {
            try {
                FileSystems.getFileSystem(uri).close();
            } catch (final Exception exp) {
                if (!(exp instanceof NullPointerException || exp instanceof FileSystemNotFoundException)) {
                    logger.warning("Something went wrong while closing the file system", exp);
                }
            }
        }
        if (path == null) {
            try {
                path = FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/");
            } catch (final IOException e) {
                throw new IllegalStateException("Unable to resolve jar root!", e);
            }
        }
        return path;
    }

}
