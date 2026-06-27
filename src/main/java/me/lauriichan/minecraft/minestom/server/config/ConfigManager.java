package me.lauriichan.minecraft.minestom.server.config;

import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class ConfigManager {

    private final Object2ObjectArrayMap<Class<? extends ISingleConfigExtension>, ConfigWrapper<?>> configs = new Object2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<Class<? extends IMultiConfigExtension>, MultiConfigWrapper<?, ?, ?, ?>> multiConfigs = new Object2ObjectArrayMap<>();

    public ConfigManager(final SystemModule systemModule) {
        systemModule.extension(ISingleConfigExtension.class, true).callInstances((module, extension) -> {
            configs.put(extension.getClass(), ConfigWrapper.single(module, extension));
        });
        systemModule.extension(IMultiConfigExtension.class, true).callInstances((module, extension) -> {
            multiConfigs.put(extension.getClass(), new MultiConfigWrapper<>(module, extension));
        });
    }

    public int amount() {
        return configs.size();
    }

    public Object2ObjectMap<IConfigWrapper<?>, int[]> reload() {
        return reload(false, false);
    }

    public Object2ObjectMap<IConfigWrapper<?>, int[]> reload(final boolean force, final boolean wipeAfterLoad) {
        final ObjectList<IConfigWrapper<?>> wrappers = wrappers();
        final Object2ObjectArrayMap<IConfigWrapper<?>, int[]> results = new Object2ObjectArrayMap<>(wrappers.size());
        wrappers.forEach(wrapper -> results.put(wrapper, wrapper.reload(force, wipeAfterLoad)));
        return Object2ObjectMaps.unmodifiable(results);
    }

    public Object2ObjectMap<IConfigWrapper<?>, int[]> save() {
        return save(false);
    }

    public Object2ObjectMap<IConfigWrapper<?>, int[]> save(final boolean force) {
        final ObjectList<IConfigWrapper<?>> wrappers = wrappers();
        final Object2ObjectArrayMap<IConfigWrapper<?>, int[]> results = new Object2ObjectArrayMap<>(wrappers.size());
        wrappers.forEach(wrapper -> results.put(wrapper, wrapper.save(force)));
        return Object2ObjectMaps.unmodifiable(results);
    }

    public ObjectList<IConfigWrapper<?>> wrappers() {
        return Stream.concat(configs.values().stream(), multiConfigs.values().stream().flatMap(config -> config.wrappers().stream()))
            .collect(ObjectArrayList.toList());
    }

    public <E extends ISingleConfigExtension> ConfigWrapper<E> wrapper(final Class<E> type) {
        final ConfigWrapper<?> extension = configs.get(type);
        if (extension == null) {
            return null;
        }
        return (ConfigWrapper<E>) extension;
    }

    public <E extends ISingleConfigExtension> E config(final Class<E> type) {
        final ConfigWrapper<?> wrapper = configs.get(type);
        if (wrapper == null) {
            return null;
        }
        return type.cast(wrapper.config());
    }

    public boolean has(final Class<? extends ISingleConfigExtension> type) {
        return configs.containsKey(type);
    }

    public ObjectCollection<MultiConfigWrapper<?, ?, ?, ?>> multiWrappers() {
        return multiConfigs.values();
    }

    public <T, C extends IConfigExtension, E extends IMultiConfigExtension<?, T, C>> MultiConfigWrapper<?, T, C, E> multiWrapper(
        final Class<E> type) {
        final MultiConfigWrapper<?, ?, ?, ?> multiWrapper = multiConfigs.get(type);
        if (multiWrapper == null) {
            return null;
        }
        return (MultiConfigWrapper<?, T, C, E>) multiWrapper;
    }

    public <T, C extends IConfigExtension, E extends IMultiConfigExtension<?, T, C>> ConfigWrapper<C> multiWrapper(final Class<E> type,
        final T element) {
        final MultiConfigWrapper<?, T, C, E> multiWrapper = multiWrapper(type);
        if (multiWrapper == null) {
            return null;
        }
        return multiWrapper.wrapper(element);
    }

    public <T, C extends IConfigExtension, E extends IMultiConfigExtension<?, T, C>> ConfigWrapper<C> multiWrapperOrCreate(
        final Class<E> type, final T element) {
        final MultiConfigWrapper<?, T, C, E> multiWrapper = multiWrapper(type);
        if (multiWrapper == null) {
            return null;
        }
        return multiWrapper.wrapperOrCreate(element);
    }

    public <T, C extends IConfigExtension, E extends IMultiConfigExtension<?, T, C>> C multiConfig(final Class<E> type, final T element) {
        final ConfigWrapper<C> wrapper = multiWrapper(type, element);
        if (wrapper == null) {
            return null;
        }
        return wrapper.config();
    }

    public <T, C extends IConfigExtension, E extends IMultiConfigExtension<?, T, C>> C multiConfigOrCreate(final Class<E> type,
        final T element) {
        return multiWrapperOrCreate(type, element).config();
    }

    public boolean hasMulti(final Class<? extends IMultiConfigExtension<?, ?, ?>> type) {
        return multiConfigs.containsKey(type);
    }

}
