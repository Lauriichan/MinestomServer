package me.lauriichan.minecraft.minestom.util.cli;

public interface IDelegateArgument<K, V> extends IArgument<V> {

    IArgument<K> delegate();
    
}
