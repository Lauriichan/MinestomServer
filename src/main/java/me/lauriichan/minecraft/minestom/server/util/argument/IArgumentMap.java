package me.lauriichan.minecraft.minestom.server.util.argument;

import java.util.Optional;

public interface IArgumentMap {

    static IArgumentMap empty() {
        return EmptyArgumentMap.INSTANCE;
    }

    static IArgumentMap newMap() {
        return new ArgumentMap();
    }

    boolean has(String key);

    boolean has(String key, Class<?> type);

    Optional<Object> get(String key);

    <E> Optional<E> get(String key, Class<E> type);

    Optional<Class<?>> getClass(String key);

    <E> Optional<Class<? extends E>> getClass(String key, Class<E> abstraction);

    default <E> Class<? extends E> getClassOrStack(String key, Class<E> abstraction, ArgumentStack stack) {
        Optional<Class<? extends E>> option = getClass(key, abstraction);
        if (option.isPresent()) {
            return option.get();
        }
        stack.push(key, abstraction.getClass());
        return null;
    }

    default <E> E getOrStack(String key, Class<E> type, ArgumentStack stack) {
        Optional<E> option = get(key, type);
        if (option.isPresent()) {
            return option.get();
        }
        stack.push(key, type);
        return null;
    }

    IArgumentMap set(String key, Object value);

    IArgumentMap remove(String key);

    IArgumentMap clear();

    IArgumentMap clone();

    boolean isEmpty();

    int size();

}