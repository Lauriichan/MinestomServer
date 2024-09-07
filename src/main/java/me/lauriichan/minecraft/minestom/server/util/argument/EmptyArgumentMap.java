package me.lauriichan.minecraft.minestom.server.util.argument;

import java.util.Optional;

final class EmptyArgumentMap implements IArgumentMap {

    public static final EmptyArgumentMap INSTANCE = new EmptyArgumentMap();

    private EmptyArgumentMap() {}

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public boolean has(String key, Class<?> type) {
        return false;
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.empty();
    }

    @Override
    public <E> Optional<E> get(String key, Class<E> type) {
        return Optional.empty();
    }

    @Override
    public Optional<Class<?>> getClass(String key) {
        return Optional.empty();
    }

    @Override
    public <E> Optional<Class<? extends E>> getClass(String key, Class<E> abstraction) {
        return Optional.empty();
    }

    @Override
    public IArgumentMap set(String key, Object value) {
        return this;
    }

    @Override
    public IArgumentMap remove(String key) {
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
