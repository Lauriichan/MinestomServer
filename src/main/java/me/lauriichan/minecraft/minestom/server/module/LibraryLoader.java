package me.lauriichan.minecraft.minestom.server.module;

import java.net.URL;
import java.net.URLClassLoader;

public final class LibraryLoader extends URLClassLoader {

    private static final URL[] EMPTY = new URL[0];

    LibraryLoader(ClassLoader parent) {
        this(EMPTY, parent);
    }

    LibraryLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addToClasspath(URL url) {
        addURL(url);
    }

}
