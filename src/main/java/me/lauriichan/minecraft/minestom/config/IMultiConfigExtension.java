package me.lauriichan.minecraft.minestom.config;

import me.lauriichan.minecraft.minestom.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.extension.IExtension;

@ExtensionPoint
public interface IMultiConfigExtension<K, T, C extends IConfigExtension> extends IExtension {
    
    K getConfigKey(T element);

    String path(T element);
    
    C create(T element);
    
}
