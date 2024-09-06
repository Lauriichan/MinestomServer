package me.lauriichan.minecraft.minestom.server.io;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IIOHandler<B, V> extends IExtension {
    
    Class<B> bufferType();
    
    Class<V> valueType();

}
