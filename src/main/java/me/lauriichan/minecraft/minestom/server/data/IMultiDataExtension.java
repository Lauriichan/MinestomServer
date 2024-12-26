package me.lauriichan.minecraft.minestom.server.data;

import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IMultiDataExtension<K, E, T, D extends IFileDataExtension<T>> extends IExtension {

    Class<D> type();

    K getDataKey(E element);

    String path(E element);

    D create(E element);

    default void onLoad(final ISimpleLogger logger) {}

    default void onSave(final ISimpleLogger logger) {}

}
