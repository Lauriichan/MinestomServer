package me.lauriichan.minecraft.minestom.util;

import java.io.File;

import me.lauriichan.minecraft.minestom.resource.ISourceProvider;
import me.lauriichan.minecraft.minestom.resource.ResourceManager;
import me.lauriichan.minecraft.minestom.resource.source.FileDataSource;
import me.lauriichan.minecraft.minestom.resource.source.PathDataSource;

public final class DefaultResourceProviders {
    
    public static final ISourceProvider FILE_SYSTEM = (module, path) -> new FileDataSource(new File(path));
    public static final ISourceProvider JAR = (module, path) -> new PathDataSource(module.jarRoot().resolve(path));
    public static final ISourceProvider DATA = (module, path) -> new PathDataSource(module.dataRoot().resolve(path));
    
    private DefaultResourceProviders() {
        throw new UnsupportedOperationException();
    }
    
    public static void setDefaults(ResourceManager resourceManager) {
        resourceManager.setDefault("jar");
        resourceManager.register("fs", FILE_SYSTEM);
        resourceManager.register("jar", JAR);
        resourceManager.register("data", DATA);
    }

}
