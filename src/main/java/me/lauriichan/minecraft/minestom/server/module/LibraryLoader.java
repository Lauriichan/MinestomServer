package me.lauriichan.minecraft.minestom.server.module;

import java.net.URL;
import java.net.URLClassLoader;

public final class LibraryLoader extends URLClassLoader {

    private static final URL[] EMPTY = {};

    LibraryLoader(final ClassLoader parent) {
        this(EMPTY, parent);
    }

    LibraryLoader(final URL[] urls, final ClassLoader parent) {
        super(urls, parent);
    }

    public void addToClasspath(final URL url) {
        addURL(url);
    }

}
