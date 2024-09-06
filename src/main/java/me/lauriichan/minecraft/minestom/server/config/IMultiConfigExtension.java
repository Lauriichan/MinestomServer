package me.lauriichan.minecraft.minestom.server.config;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IMultiConfigExtension<K, T, C extends IConfigExtension> extends IExtension {
    
    K getConfigKey(T element);

    String path(T element);
    
    C create(T element);
    
}
