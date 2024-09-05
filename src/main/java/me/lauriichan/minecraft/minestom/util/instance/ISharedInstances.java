package me.lauriichan.minecraft.minestom.util.instance;

public interface ISharedInstances<E> {

    void remove(final ClassLoader loader);

    void remove(final Class<?> clazz);

    <T> T getCached(final Class<T> clazz);

    <T extends E> T get(final Class<T> clazz, final Object... arguments) throws Throwable;

}
