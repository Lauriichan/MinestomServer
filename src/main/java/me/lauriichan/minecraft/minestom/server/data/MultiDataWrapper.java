package me.lauriichan.minecraft.minestom.server.data;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;

public final class MultiDataWrapper<K, E, T, D extends IFileDataExtension<T>, M extends IMultiDataExtension<K, E, T, D>> {

    private final Object2ObjectArrayMap<K, DataWrapper<T, D>> data = new Object2ObjectArrayMap<>();

    private final IMinestomModule module;
    private final M extension;

    public MultiDataWrapper(final IMinestomModule module, final M extension) {
        this.module = module;
        this.extension = extension;
    }

    public DataWrapper<T, D> wrapper(final E element) {
        return data.get(extension.getDataKey(Objects.requireNonNull(element)));
    }

    public DataWrapper<T, D> wrapperOrCreate(final E element) {
        final K key = extension.getDataKey(Objects.requireNonNull(element));
        DataWrapper<T, D> wrapper = data.get(key);
        if (wrapper == null) {
            wrapper = new DataWrapper<>(module, extension.create(element), extension.path(element));
            wrapper.reload();
            data.put(key, wrapper);
        }
        return wrapper;
    }

    public D config(final E element) {
        final DataWrapper<T, D> wrapper = wrapper(element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.data();
    }

    public D configOrCreate(final E element) {
        return wrapperOrCreate(element).data();
    }

    public ObjectCollection<DataWrapper<T, D>> wrappers() {
        return data.values();
    }

}
