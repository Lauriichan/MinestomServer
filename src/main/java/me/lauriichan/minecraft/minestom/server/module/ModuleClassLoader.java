package me.lauriichan.minecraft.minestom.server.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.laylib.reflection.AccessFailedException;
import me.lauriichan.laylib.reflection.JavaAccess;
import me.lauriichan.minecraft.minestom.server.MinestomServer;

public final class ModuleClassLoader extends URLClassLoader {

    public static class InvalidModuleException extends ModuleException {

        private static final long serialVersionUID = 6976951234523893403L;

        public InvalidModuleException(String message) {
            super(message);
        }

        public InvalidModuleException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    static interface ModuleCreator<M extends MinestomModule> {

        M newInstance(ExternModule<M> externModule) throws InvalidModuleException;

    }

    private static final ObjectList<String> DISALLOWED_PACKAGES = ObjectLists.unmodifiable(ObjectArrayList.of(new String[] {
        "me.lauriichan.minecraft.minestom.",
        "net.kyori.adventure.",
    }));

    private final Object2ObjectMap<String, Class<?>> classes = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    private ClassLoader libraryLoader;

    private final Class<? extends MinestomModule> moduleClass;
    private final ExternModule<?> module;

    private final JarFile jarFile;
    private final Manifest manifest;
    private final URL url;

    ModuleClassLoader(final MinestomServer server, final File file, final Path jarRoot, final ClassLoader parent,
        final ModuleDescription description) throws IOException, InvalidModuleException {
        super(new URL[] {
            file.toURI().toURL()
        }, parent);

        this.jarFile = new JarFile(file);
        this.manifest = jarFile.getManifest();
        this.url = file.toURI().toURL();

        this.libraryLoader = server.systemModule().libraryLoader().createLoader(server.moduleManager(), description);

        Class<?> jarClass;
        try {
            jarClass = Class.forName(description.main(), true, this);
        } catch (ClassNotFoundException exp) {
            throw new InvalidModuleException("Couldn't find main class '" + description.main() + "'", exp);
        }

        try {
            this.moduleClass = jarClass.asSubclass(MinestomModule.class);
        } catch (ClassCastException exp) {
            throw new InvalidModuleException("Main class '" + description.main() + "' is required to extend MinestomModule", exp);
        }

        Constructor<? extends MinestomModule> moduleConstructor;
        try {
            moduleConstructor = moduleClass.getDeclaredConstructor(ExternModule.class);
        } catch (NoSuchMethodException exp) {
            throw new InvalidModuleException(
                "Main class '" + description.main() + "' is required to have a public constructor with ExternModule as argument", exp);
        }

        this.module = new ExternModule<>(server, description, file, jarRoot, this, moduleClass, mod -> {
            try {
                return JavaAccess.PLATFORM.invoke(moduleConstructor, mod);
            } catch (AccessFailedException exp) {
                throw new InvalidModuleException("Couldn't initialize main class", exp.getCause());
            }
        });
    }

    ExternModule<?> module() {
        return module;
    }

    @Override
    public URL getResource(String name) {
        return findResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true, true);
    }

    Class<?> loadClass0(String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);
            if (checkGlobal || result.getClassLoader() == this) {
                return result;
            }
        } catch (ClassNotFoundException exp) {
        }
        if (checkLibraries && libraryLoader != null) {
            try {
                return libraryLoader.loadClass(name);
            } catch (ClassNotFoundException exp) {
            }
        }
        if (checkGlobal) {
            return ((MinestomModuleManager) module.moduleManager()).loadClassByName(name, resolve, this);
        }
        throw new ClassNotFoundException(name);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (DISALLOWED_PACKAGES.stream().anyMatch(name::startsWith)) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);
        if (result != null) {
            return result;
        }
        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = jarFile.getJarEntry(path);
        if (entry != null) {
            byte[] bytes;
            try (InputStream in = jarFile.getInputStream(entry)) {
                bytes = in.readAllBytes();
            } catch (IOException exp) {
                throw new ClassNotFoundException(name, exp);
            }

            int lastDot = name.lastIndexOf('.');
            if (lastDot != -1) {
                String pkgName = name.substring(0, lastDot);
                if (getPackage(pkgName) == null) {
                    try {
                        if (manifest != null) {
                            definePackage(pkgName, manifest, url);
                        } else {
                            definePackage(pkgName, null, null, null, null, null, null, null);
                        }
                    } catch (IllegalArgumentException ex) {
                        if (getPackage(pkgName) == null) {
                            throw new IllegalStateException("Cannot find package " + pkgName);
                        }
                    }
                }
            }
            CodeSigner[] signers = entry.getCodeSigners();
            CodeSource source = new CodeSource(url, signers);
            result = defineClass(name, bytes, 0, bytes.length, source);
        } else {
            result = super.findClass(name);
        }
        classes.put(name, result);
        return result;
    }

}
