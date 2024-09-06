package me.lauriichan.minecraft.minestom.server;

import me.lauriichan.minecraft.minestom.server.command.MinestomCommandManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.io.IOManager;
import me.lauriichan.minecraft.minestom.server.module.IModuleManager;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;
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
    
    private final MinestomCommandManager commandManager;

    private final IOManager ioManager;

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
        this.ioManager = new IOManager(systemModule);
        this.configMigrator = new ConfigMigrator(systemModule);
        this.configManager = new ConfigManager(systemModule);
        configManager.reload();
        minecraft = MinecraftServer.init();
        commandManager = new MinestomCommandManager(systemModule);
        systemModule.start();
        commandManager.registerCommands();
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
    
    public MinestomCommandManager commandManager() {
        return commandManager;
    }

    public IOManager ioManager() {
        return ioManager;
    }

    public ConfigMigrator configMigrator() {
        return configMigrator;
    }

    public ConfigManager configManager() {
        return configManager;
    }

}
