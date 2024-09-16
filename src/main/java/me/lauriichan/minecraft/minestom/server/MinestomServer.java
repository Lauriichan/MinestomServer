package me.lauriichan.minecraft.minestom.server;

import me.lauriichan.minecraft.minestom.server.command.MinestomCommandManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.io.IOManager;
import me.lauriichan.minecraft.minestom.server.module.IModuleManager;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;
import me.lauriichan.minecraft.minestom.server.permission.PermissionProvider;
import me.lauriichan.minecraft.minestom.server.signal.SignalManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;

public final class MinestomServer {

    private static MinestomServer INSTANCE;

    public static MinestomServer get() {
        return INSTANCE;
    }

    public static void stopServer() {
        System.exit(0);
    }

    private final MinecraftServer minecraft;

    private final SignalManager signalManager = new SignalManager();

    private final SystemModule systemModule;
    private final IModuleManager moduleManager;

    private final MinestomCommandManager commandManager;

    private final IOManager ioManager;

    private final ConfigMigrator configMigrator;
    private final ConfigManager configManager;

    private final PermissionProvider permissionProvider;

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
        this.minecraft = MinecraftServer.init();
        systemModule.registerSignalHandlers();
        this.permissionProvider = systemModule.setupPermissionProvider();
        this.commandManager = new MinestomCommandManager(systemModule);
        systemModule.start();
        permissionProvider.activate();
        commandManager.registerCommands();
        if (MinestomArguments.MC_AUTH.value()) {
            MojangAuth.init();
        }
        minecraft.start(MinestomArguments.MC_HOST.value(), MinestomArguments.MC_PORT.value().intValue());
        systemModule.callServerReady();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            systemModule.callServerShutdown();
            configManager.save();
            MinecraftServer.stopCleanly();
        }, "Server Shutdown"));
    }

    public MinecraftServer minecraft() {
        return minecraft;
    }

    public SignalManager signalManager() {
        return signalManager;
    }

    public SystemModule systemModule() {
        return systemModule;
    }

    public IModuleManager moduleManager() {
        return moduleManager;
    }

    public PermissionProvider permissionProvider() {
        return permissionProvider;
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

    public void stop() {
        stopServer();
    }

}
