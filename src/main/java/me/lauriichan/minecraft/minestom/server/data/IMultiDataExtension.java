package me.lauriichan.minecraft.minestom.server.data;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IMultiDataExtension<K, E, T, D extends IFileDataExtension<T>> extends IExtension {

    K getDataKey(E element);

    String path(E element);

    D create(E element);

}
