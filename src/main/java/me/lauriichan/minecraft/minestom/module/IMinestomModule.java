package me.lauriichan.minecraft.minestom.module;

import java.nio.file.Path;

import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.MinestomServer;
import me.lauriichan.minecraft.minestom.config.ConfigManager;
import me.lauriichan.minecraft.minestom.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.extension.IExtension;
import me.lauriichan.minecraft.minestom.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.util.instance.ISharedInstances;
import me.lauriichan.minecraft.minestom.util.instance.SimpleInstanceInvoker;

public sealed interface IMinestomModule permits ExternModule, SystemModule, MinestomModule {

    IDataSource resource(final String path);

    <E extends IExtension> IExtensionPool<E> extension(final Class<E> type, final boolean instantiate);

    <E extends IExtension> IExtensionPool<E> extension(final Class<? extends IExtension> extensionType, final Class<E> type,
        final boolean instantiate);
    
    boolean dependsOn(IMinestomModule module);
    
    /*
     * Getter
     */
    
    MinestomServer server();
    
    IModuleDescription description();

    ISimpleLogger logger();

    Path jarRoot();

    Path dataRoot();

    ResourceManager resourceManager();
    
    IModuleManager moduleManager();

    SimpleInstanceInvoker invoker();

    ISharedInstances<IExtension> sharedExtensions();
    
    MessageManager messageManager();

    IConditionMap conditionMap();

    ConfigMigrator configMigrator();

    ConfigManager configManager();

}
