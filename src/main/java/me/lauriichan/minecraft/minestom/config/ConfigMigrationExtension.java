package me.lauriichan.minecraft.minestom.config;

import me.lauriichan.minecraft.minestom.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.extension.IExtension;

@ExtensionPoint
public abstract class ConfigMigrationExtension<C extends IConfigExtension> implements IExtension {
    
    private final Class<C> targetType;
    private final int minVersion, targetVersion;
    
    public ConfigMigrationExtension(Class<C> targetType, int minVersion, int targetVersion) {
        this.targetType = targetType;
        this.minVersion = minVersion;
        this.targetVersion = targetVersion;
    }
    
    public final Class<C> targetType() {
        return targetType;
    }
    
    public final int minVersion() {
        return minVersion;
    }
    
    public final int targetVersion() {
        return targetVersion;
    }
    
    public abstract String description();
    
    public abstract void migrate(Configuration configuration) throws Throwable;
    
    /*
     * Utils
     */
    
    public final void copy(Configuration config, String pathA, String pathB) {
        if (!config.contains(pathA)) {
            return;
        }
        config.set(pathB, config.get(pathA));
    }

}
