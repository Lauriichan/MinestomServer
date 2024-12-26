package me.lauriichan.minecraft.minestom.server.util.instance;

import me.lauriichan.minecraft.minestom.server.util.ReflectionUtil;

public interface IInstanceInvoker {

    IInstanceInvoker DEFAULT = ReflectionUtil::createInstance;

    <T> T invoke(final Class<T> clazz, final Object... arguments) throws Throwable;

}
