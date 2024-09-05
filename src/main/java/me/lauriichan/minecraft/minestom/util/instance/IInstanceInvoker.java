package me.lauriichan.minecraft.minestom.util.instance;

import me.lauriichan.minecraft.minestom.util.ReflectionUtil;

public interface IInstanceInvoker {
    
    public static final IInstanceInvoker DEFAULT = ReflectionUtil::createInstance;
    
    <T> T invoke(final Class<T> clazz, final Object... arguments) throws Throwable;

}
