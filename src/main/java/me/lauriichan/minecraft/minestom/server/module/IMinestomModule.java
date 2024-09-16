package me.lauriichan.minecraft.minestom.server.module;

import java.nio.file.Path;

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
import me.lauriichan.minecraft.minestom.server.signal.SignalManager;
import me.lauriichan.minecraft.minestom.server.util.instance.ISharedInstances;
import me.lauriichan.minecraft.minestom.server.util.instance.SimpleInstanceInvoker;

public sealed interface IMinestomModule permits ExternModule, SystemModule, MinestomModule {

    IDataSource resource(final String path);

    <E extends IExtension> IExtensionPool<E> extension(final Class<E> type, final boolean instantiate);

    <E extends IExtension> IExtensionPool<E> extension(final Class<? extends IExtension> extensionType, final Class<E> type,
        final boolean instantiate);
    
    boolean dependsOn(IMinestomModule module);
    
    default Class<?> getClassByName(String name) {
        try {
            return Class.forName(name, true, classLoader());
        } catch(ClassNotFoundException nfe) {
        }
        return null;
    }
    
    /*
     * Getter
     */
    
    MinestomServer server();
    
    SignalManager signalManager();
    
    IModuleDescription description();

    ISimpleLogger logger();

    Path jarRoot();

    Path dataRoot();

    ResourceManager resourceManager();
    
    IModuleManager moduleManager();

    SimpleInstanceInvoker invoker();

    ISharedInstances<IExtension> sharedExtensions();
    
    MessageManager messageManager();
    
    ModuleActorMap actorMap();

    IConditionMap conditionMap();

    ConfigMigrator configMigrator();

    ConfigManager configManager();
    
    ClassLoader classLoader();

}
