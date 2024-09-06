package me.lauriichan.minecraft.minestom.server.config;

import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;

public interface IConfigHandler {

    void load(Configuration configuration, IDataSource source, boolean onlyRaw) throws Exception;

    void save(Configuration configuration, IDataSource source) throws Exception;

}
