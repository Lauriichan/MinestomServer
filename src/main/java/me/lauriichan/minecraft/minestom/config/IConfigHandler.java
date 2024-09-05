package me.lauriichan.minecraft.minestom.config;

import me.lauriichan.minecraft.minestom.resource.source.IDataSource;

public interface IConfigHandler {

    void load(Configuration configuration, IDataSource source) throws Exception;

    void save(Configuration configuration, IDataSource source) throws Exception;

}
