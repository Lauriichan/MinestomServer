package me.lauriichan.minecraft.minestom.server.data;

import me.lauriichan.minecraft.minestom.server.extension.IExtension;

public interface IDataExtension<T> extends IExtension {

    default String name() {
        return getClass().getSimpleName();
    }

    IDataHandler<T> handler();

}
