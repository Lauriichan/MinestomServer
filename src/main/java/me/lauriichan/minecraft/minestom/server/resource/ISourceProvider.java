package me.lauriichan.minecraft.minestom.server.resource;

import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;

public interface ISourceProvider {

    /**
     * Provides a data source related to the path
     * 
     * @param  module the resource owner
     * @param  path   the path
     * 
     * @return        the data source
     */
    IDataSource provide(IMinestomModule module, String path);

}
