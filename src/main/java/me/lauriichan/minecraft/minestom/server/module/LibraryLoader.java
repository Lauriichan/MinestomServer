package me.lauriichan.minecraft.minestom.server.module;

import java.net.URL;
import java.net.URLClassLoader;

public final class LibraryLoader extends URLClassLoader {

    private static final URL[] EMPTY = {};
    private final ModuleClassLoader moduleLoader;

    LibraryLoader(final ModuleClassLoader parent) {
        this(EMPTY, parent);
    }

    LibraryLoader(final URL[] urls, final ModuleClassLoader parent) {
        super(urls, parent.getParent());
        this.moduleLoader = parent;
    }

    public void addToClasspath(final URL url) {
        addURL(url);
    }

    final Class<?> loadInternal(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException exp) {
        }
        return moduleLoader.loadClass0(name, resolve, true, false);
    }

}
