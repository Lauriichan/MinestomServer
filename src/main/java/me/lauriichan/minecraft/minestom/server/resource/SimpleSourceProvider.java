package me.lauriichan.minecraft.minestom.server.resource;

import java.nio.file.Path;

import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.resource.source.PathDataSource;

final class SimpleSourceProvider implements ISourceProvider {

    private final Path basePath;

    public SimpleSourceProvider(final Path basePath) {
        this.basePath = basePath;
    }

    public Path basePath() {
        return basePath;
    }

    @Override
    public IDataSource provide(IMinestomModule module, String path) {
        return new PathDataSource(basePath.resolve(path));
    }

}
