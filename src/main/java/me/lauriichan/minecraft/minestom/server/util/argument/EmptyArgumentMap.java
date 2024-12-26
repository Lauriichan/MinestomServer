package me.lauriichan.minecraft.minestom.server.util.argument;

import java.util.Optional;

final class EmptyArgumentMap implements IArgumentMap {

    public static final EmptyArgumentMap INSTANCE = new EmptyArgumentMap();

    private EmptyArgumentMap() {}

    @Override
    public boolean has(final String key) {
        return false;
    }

    @Override
    public boolean has(final String key, final Class<?> type) {
        return false;
    }

    @Override
    public Optional<Object> get(final String key) {
        return Optional.empty();
    }

    @Override
    public <E> Optional<E> get(final String key, final Class<E> type) {
        return Optional.empty();
    }

    @Override
    public Optional<Class<?>> getClass(final String key) {
        return Optional.empty();
    }

    @Override
    public <E> Optional<Class<? extends E>> getClass(final String key, final Class<E> abstraction) {
        return Optional.empty();
    }

    @Override
    public IArgumentMap set(final String key, final Object value) {
        return this;
    }

    @Override
    public IArgumentMap remove(final String key) {
        return this;
    }

    @Override
    public IArgumentMap clear() {
        return this;
    }

    @Override
    public IArgumentMap clone() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

}
