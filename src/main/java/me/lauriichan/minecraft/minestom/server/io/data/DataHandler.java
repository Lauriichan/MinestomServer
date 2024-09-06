package me.lauriichan.minecraft.minestom.server.io.data;

public abstract class DataHandler<B, V> implements IDataHandler<B, V> {

    protected final Class<B> bufferType;
    protected final Class<V> valueType;

    public DataHandler(final Class<B> bufferType, final Class<V> valueType) {
        this.bufferType = bufferType;
        this.valueType = valueType;
    }

    @Override
    public final Class<B> bufferType() {
        return bufferType;
    }

    @Override
    public final Class<V> valueType() {
        return valueType;
    }

    public abstract void serialize(B buffer, V value);

    public abstract Result<V> deserialize(B buffer);

}
