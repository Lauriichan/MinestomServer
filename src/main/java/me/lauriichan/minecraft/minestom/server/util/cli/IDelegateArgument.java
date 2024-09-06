package me.lauriichan.minecraft.minestom.server.util.cli;

public interface IDelegateArgument<K, V> extends IArgument<V> {

    IArgument<K> delegate();
    
}
