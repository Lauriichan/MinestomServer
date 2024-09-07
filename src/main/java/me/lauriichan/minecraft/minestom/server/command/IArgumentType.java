package me.lauriichan.minecraft.minestom.server.command;

import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@Extension
public sealed interface IArgumentType<T> extends IExtension permits ArgumentType, ProvidedArgumentType {
    
    Class<T> type();

}
