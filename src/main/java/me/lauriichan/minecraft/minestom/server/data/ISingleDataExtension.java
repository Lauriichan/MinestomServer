package me.lauriichan.minecraft.minestom.server.data;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;

@ExtensionPoint
public interface ISingleDataExtension<T> extends IFileDataExtension<T> {
    
    String path();

}
