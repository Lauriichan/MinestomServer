package me.lauriichan.minecraft.minestom.server.command;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public sealed interface IArgumentType<T> extends IExtension permits ArgumentType, ProvidedArgumentType {
    
    Class<T> type();

}
