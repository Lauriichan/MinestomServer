package me.lauriichan.minecraft.minestom.server.io.data;

import me.lauriichan.minecraft.minestom.server.io.IIOHandler;

public interface IDataHandler<B, V> extends IIOHandler<B, V> {
    
    static record Result<V>(V value, boolean dirty) {}

    static <T> Result<T> result(T value) {
        return new Result<>(value, false);
    }

    static <T> Result<T> result(T value, boolean dirty) {
        return new Result<>(value, dirty);
    }

    void serialize(B buffer, V value);

    Result<V> deserialize(B buffer);

}
