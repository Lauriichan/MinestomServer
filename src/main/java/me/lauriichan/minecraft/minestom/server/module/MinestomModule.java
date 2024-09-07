package me.lauriichan.minecraft.minestom.server.module;

import java.nio.file.Path;
import java.util.Objects;

import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.server.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.util.instance.ISharedInstances;
import me.lauriichan.minecraft.minestom.server.util.instance.SimpleInstanceInvoker;

public abstract non-sealed class MinestomModule implements IMinestomModule {

    private final ExternModule<?> delegate;

    public MinestomModule(ExternModule<?> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }
    
    /*
     * Lifecycle
     */

    /**
     * Executed before default extension processing
     */
    protected void onModuleLoad() {}
    
    /**
     * Executed pretty much immediately after {@link MinestomModule#onModuleLoad}
     */
    protected void onModuleConditionSetup(IConditionMap map) {}

    /**
     * Executed after default extension processing with server initialised
     */
    protected void onModuleStart() {}

    /**
     * Executed after all modules have been started with server initialised
     */
    protected void onModuleReady() {}

    /**
     * Executed after the minecraft server connection is established
     */
    protected void onServerReady() {}

    /**
     * Executed on server shutdown
     */
    protected void onServerShutdown() {}
    
    /*
     * Getter
     */

    public final ExternModule<?> delegate() {
        return delegate;
    }

    @Override
    public final IDataSource resource(final String path) {
        return delegate.resource(path);
    }

    @Override
    public final <E extends IExtension> IExtensionPool<E> extension(final Class<E> type, final boolean instantiate) {
        return delegate.extension(type, instantiate);
    }

    @Override
    public final <E extends IExtension> IExtensionPool<E> extension(final Class<? extends IExtension> extensionType, final Class<E> type,
        final boolean instantiate) {
        return delegate.extension(extensionType, type, instantiate);
    }
    
    @Override
    public final boolean dependsOn(IMinestomModule module) {
        return delegate.dependsOn(module);
    }

    @Override
    public final MinestomServer server() {
        return delegate.server();
    }

    @Override
    public final IModuleDescription description() {
        return delegate.description();
    }

    @Override
    public final ISimpleLogger logger() {
        return delegate.logger();
    }

    @Override
    public final Path jarRoot() {
        return delegate.jarRoot();
    }

    @Override
    public final Path dataRoot() {
        return delegate.dataRoot();
    }

    @Override
    public final IModuleManager moduleManager() {
        return delegate.moduleManager();
    }

    @Override
    public final ResourceManager resourceManager() {
        return delegate.resourceManager();
    }

    @Override
    public final SimpleInstanceInvoker invoker() {
        return delegate.invoker();
    }

    @Override
    public final ISharedInstances<IExtension> sharedExtensions() {
        return delegate.sharedExtensions();
    }

    @Override
    public final MessageManager messageManager() {
        return delegate.messageManager();
    }

    @Override
    public final ModuleActorMap actorMap() {
        return delegate.actorMap();
    }
    
    @Override
    public final IConditionMap conditionMap() {
        return delegate.conditionMap();
    }

    @Override
    public final ConfigMigrator configMigrator() {
        return delegate.configMigrator();
    }

    @Override
    public final ConfigManager configManager() {
        return delegate.configManager();
    }

    @Override
    public final ClassLoader classLoader() {
        return delegate.classLoader();
    }
    
}
