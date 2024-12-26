package me.lauriichan.minecraft.minestom.server.config;

import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

public interface IConfigExtension extends IExtension {
    
    default String name() {
        return getClass().getSimpleName();
    }

    IConfigHandler handler();

    default boolean isModified() {
        return false;
    }
    
    default void onPropergate(final ISimpleLogger logger, final Configuration configuration) throws Exception {}

    default void onLoad(final ISimpleLogger logger, final Configuration configuration) throws Exception {}

    default void onSave(final ISimpleLogger logger, final Configuration configuration) throws Exception {}

}
