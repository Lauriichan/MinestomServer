package me.lauriichan.minecraft.minestom.server;

import java.util.Set;

import me.lauriichan.minecraft.minestom.server.command.MinestomCommandManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigManager;
import me.lauriichan.minecraft.minestom.server.config.ConfigMigrator;
import me.lauriichan.minecraft.minestom.server.data.DataManager;
import me.lauriichan.minecraft.minestom.server.data.DataMigrator;
import me.lauriichan.minecraft.minestom.server.io.IOManager;
import me.lauriichan.minecraft.minestom.server.module.IModuleManager;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;
import me.lauriichan.minecraft.minestom.server.permission.PermissionProvider;
import me.lauriichan.minecraft.minestom.server.signal.SignalManager;
import me.lauriichan.minecraft.minestom.server.translation.config.MultiTranslationConfig;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;

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

    private final DataMigrator dataMigrator;
    private final DataManager dataManager;

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
        this.dataMigrator = new DataMigrator(systemModule);
        this.dataManager = new DataManager(systemModule);
        configManager.reload();
        dataManager.reload();
        configManager.multiWrapper(MultiTranslationConfig.class).reload();
        String[] secrets = MinestomArguments.PROXY_SECRETS.value();
        this.minecraft = MinecraftServer.init(switch (MinestomArguments.MC_AUTH.value()) {
        case BUNGEECORD:
            if (secrets == null || secrets.length == 0) {
                yield new Auth.Bungee();
            }
            yield new Auth.Bungee(Set.of(secrets));
        default:
        case MOJANG:
            yield new Auth.Online();
        case OFFLINE:
            yield new Auth.Offline();
        case VELOCITY:
            if (secrets == null || secrets.length == 0) {
                throw new IllegalStateException("No proxy secret set");
            }
            yield new Auth.Velocity(secrets[0]);
        });
        systemModule.registerSignalHandlers();
        this.permissionProvider = systemModule.setupPermissionProvider();
        this.commandManager = new MinestomCommandManager(systemModule);
        systemModule.start();
        if (permissionProvider != null) {
            permissionProvider.activate();
        }
        commandManager.registerCommands();
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

    public DataMigrator dataMigrator() {
        return dataMigrator;
    }

    public DataManager dataManager() {
        return dataManager;
    }

    public void stop() {
        stopServer();
    }

}
