package me.lauriichan.minecraft.minestom.server.io.data;

import java.lang.reflect.Array;

public interface IArrayDataHandler<B, V> extends IDataHandler<B, V> {

    @SuppressWarnings("unchecked")
    default Result<V[]> deserializeArray(final B buffer) {
        final int amount = readArrayLength(buffer);
        final V[] array = (V[]) Array.newInstance(valueType(), amount);
        boolean dirty = false;
        for (int index = 0; index < amount; index++) {
            Result<V> result = deserialize(buffer);
            if (result.dirty()) {
                dirty = true;
            }
            array[index] = result.value();
        }
        return new Result<>(array, dirty);
    }

    default void serializeArray(final B buffer, final V[] array) {
        writeArrayLength(buffer, array.length);
        for (int index = 0; index < array.length; index++) {
            serialize(buffer, array[index]);
        }
    }

    int readArrayLength(final B buffer);

    void writeArrayLength(final B buffer, final int length);

}
