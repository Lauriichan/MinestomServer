package me.lauriichan.minecraft.minestom;

import me.lauriichan.minecraft.minestom.config.ConfigManager;
import me.lauriichan.minecraft.minestom.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.module.IModuleManager;
import me.lauriichan.minecraft.minestom.module.SystemModule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;

public final class MinestomServer {

    private static MinestomServer INSTANCE;

    public static MinestomServer get() {
        return INSTANCE;
    }

    private final MinecraftServer minecraft;

    private final SystemModule systemModule;
    private final IModuleManager moduleManager;

    private final ConfigMigrator configMigrator;
    private final ConfigManager configManager;

    MinestomServer() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
        INSTANCE = this;
        this.systemModule = new SystemModule(this);
        this.moduleManager = systemModule.moduleManager();
        systemModule.load();
        this.configMigrator = new ConfigMigrator(systemModule);
        this.configManager = new ConfigManager(systemModule);
        minecraft = MinecraftServer.init();
        systemModule.start();
        if (MinestomArguments.MC_AUTH.value()) {
            MojangAuth.init();
        }
        minecraft.start(MinestomArguments.MC_HOST.value(), MinestomArguments.MC_PORT.value().intValue());
    }

    public MinecraftServer minecraft() {
        return minecraft;
    }

    public SystemModule systemModule() {
        return systemModule;
    }

    public IModuleManager moduleManager() {
        return moduleManager;
    }

    public ConfigMigrator configMigrator() {
        return configMigrator;
    }

    public ConfigManager configManager() {
        return configManager;
    }

}
