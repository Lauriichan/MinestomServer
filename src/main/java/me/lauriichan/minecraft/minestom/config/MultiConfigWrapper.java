package me.lauriichan.minecraft.minestom.config;

import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import me.lauriichan.minecraft.minestom.module.IMinestomModule;

public final class MultiConfigWrapper<K, T, C extends IConfigExtension, E extends IMultiConfigExtension<K, T, C>> {

    private final Object2ObjectArrayMap<K, ConfigWrapper<C>> configs = new Object2ObjectArrayMap<>();

    private final IMinestomModule module;
    private final E extension;

    public MultiConfigWrapper(IMinestomModule module, E extension) {
        this.module = module;
        this.extension = extension;
    }

    public ConfigWrapper<C> wrapper(T element) {
        return configs.get(extension.getConfigKey(Objects.requireNonNull(element)));
    }
    
    public ConfigWrapper<C> wrapperOrCreate(T element) {
        K key = extension.getConfigKey(Objects.requireNonNull(element));
        ConfigWrapper<C> wrapper = configs.get(key);
        if (wrapper == null) {
            wrapper = new ConfigWrapper<>(module, extension.create(element), extension.path(element));
            wrapper.reload(false);
            configs.put(key, wrapper);
        }
        return wrapper;
    }
    
    public C config(T element) {
        ConfigWrapper<C> wrapper = wrapper(element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.config();
    }
    
    public C configOrCreate(T element) {
        return wrapperOrCreate(element).config();
    }
    
    public ObjectCollection<ConfigWrapper<C>> wrappers() {
        return configs.values();
    }
    
}
