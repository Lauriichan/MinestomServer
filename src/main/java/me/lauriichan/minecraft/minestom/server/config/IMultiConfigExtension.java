package me.lauriichan.minecraft.minestom.server.config;

import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IMultiConfigExtension<K, T, C extends IConfigExtension> extends IExtension {

    Class<C> type();

    K getConfigKey(T element);

    String path(T element);

    C create(T element);

    default void onLoad(final ISimpleLogger logger) {}

    default void onSave(final ISimpleLogger logger) {}

}
