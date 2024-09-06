package me.lauriichan.minecraft.minestom.server.module;

import java.nio.file.Path;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.extension.IConditionMap;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.extension.IExtensionPool;
import me.lauriichan.minecraft.minestom.server.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.util.instance.ISharedInstances;
import me.lauriichan.minecraft.minestom.server.util.instance.SimpleInstanceInvoker;
import net.minestom.server.command.CommandSender;

public sealed interface IMinestomModule permits ExternModule, SystemModule, MinestomModule {

    IDataSource resource(final String path);

    <E extends IExtension> IExtensionPool<E> extension(final Class<E> type, final boolean instantiate);

    <E extends IExtension> IExtensionPool<E> extension(final Class<? extends IExtension> extensionType, final Class<E> type,
        final boolean instantiate);
    
    boolean dependsOn(IMinestomModule module);
    
    default void provideArguments(Actor<?> actor, ObjectArrayList<Object> list) {}
    
    default Class<?> getClassByName(String name) {
        try {
            return Class.forName(name, true, classLoader());
        } catch(ClassNotFoundException nfe) {
        }
        return null;
    }
    
    default <T extends CommandSender> Actor<T> actor(T sender){
        return new Actor<>(sender, this);
    }
    
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
    
    ClassLoader classLoader();

}
