package me.lauriichan.minecraft.minestom.server.util.argument;

import java.util.Objects;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.lauriichan.laylib.reflection.ClassUtil;

final class ArgumentMap implements IArgumentMap {

    private final Object2ObjectArrayMap<String, Object> map = new Object2ObjectArrayMap<>();

    @Override
    public boolean has(final String key) {
        return map.containsKey(key);
    }

    @Override
    public boolean has(final String key, final Class<?> type) {
        final Object object = map.get(key);
        return object != null && type.isInstance(object);
    }

    @Override
    public Optional<Object> get(final String key) {
        return Optional.ofNullable(map.get(key));
    }

    @Override
    public <E> Optional<E> get(final String key, final Class<E> type) {
        return get(key).filter(object -> type.isAssignableFrom(ClassUtil.toComplexType(object.getClass()))).map(type::cast);
    }

    @Override
    public Optional<Class<?>> getClass(final String key) {
        return Optional.ofNullable(map.get(key)).filter(Class.class::isInstance).map(val -> (Class<?>) val);
    }

    @Override
    public <E> Optional<Class<? extends E>> getClass(final String key, final Class<E> abstraction) {
        return getClass(key).filter(clazz -> abstraction.isAssignableFrom(ClassUtil.toComplexType(clazz)))
            .map(clazz -> clazz.asSubclass(abstraction));
    }

    @Override
    public ArgumentMap set(final String key, final Object value) {
        map.put(key, Objects.requireNonNull(value));
        return this;
    }

    @Override
    public ArgumentMap remove(final String key) {
        map.remove(key);
        return this;
    }

    @Override
    public ArgumentMap clear() {
        map.clear();
        return this;
    }

    @Override
    public ArgumentMap clone() {
        final ArgumentMap clone = new ArgumentMap();
        map.putAll(map);
        return clone;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public int size() {
        return map.size();
    }

}