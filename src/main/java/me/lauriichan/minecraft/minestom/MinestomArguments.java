package me.lauriichan.minecraft.minestom;

import me.lauriichan.minecraft.minestom.util.cli.ArgumentGroup;
import me.lauriichan.minecraft.minestom.util.cli.IArgument;
import me.lauriichan.minecraft.minestom.util.logger.slf4j.ILoggerAdapter.LogType;

public final class MinestomArguments {

    private static final ArgumentGroup ROOT_GROUP;

    private static final IArgument<Boolean> SHOW_HELP;
    public static final IArgument<LogType> LOGGER_LEVEL;
    public static final IArgument<String> LOG_DIR;
    public static final IArgument<Boolean> LOG_TO_CONSOLE;

    public static final IArgument<String> MC_HOST;
    public static final IArgument<Number> MC_PORT;
    public static final IArgument<Boolean> MC_AUTH;

    public static final IArgument<String> MODULE_DIR;
    public static final IArgument<String> MODULE_DATA_DIR;
    public static final IArgument<String> SYSTEM_DATA_DIR;
    
    static {
        ROOT_GROUP = ArgumentGroup.newRoot("Minestom Server", "Minestom based server software expanded to load modules.");
        
        ArgumentGroup group = ROOT_GROUP.newGroup("General", "");
        SHOW_HELP = group.argument(IArgument.bool("help", "Shows this help menu.", false));
        LOGGER_LEVEL = group.argument(IArgument.string("log-level", "LOG_LEVEL", new String[] {
            "Sets the maximum log level.",
            "Possible values:",
            "info, warn, error, debug, trace"
        }, "error").map(str -> {
            try {
                return LogType.valueOf(str.toUpperCase());
            } catch(IllegalArgumentException exp) {
                return LogType.ERROR;
            }
        }));
        LOG_DIR = group.argument(IArgument.string("log-dir", "PATH", "Sets the directory where the logs should be stored.", "logs"));
        LOG_TO_CONSOLE = group.argument(IArgument.bool("log-to-console", "Sets if the logger should also log to console.", false));
        
        group = ROOT_GROUP.newGroup("Minecraft Server", "");
        MC_HOST = group.argument(IArgument.string("host", "HOSTNAME", "Sets the server host name.", "0.0.0.0"));
        MC_PORT = group.argument(IArgument.number("port", "PORT", "Sets the server port.", 25565));
        MC_AUTH = group.argument(IArgument.bool("auth", "Sets the server to online mode.", true));
        
        group = ROOT_GROUP.newGroup("Module Settings", "");
        MODULE_DIR = group.argument(IArgument.string("module-dir", "PATH", "Sets the directory where the jar modules should be loaded from.", "modules"));
        MODULE_DATA_DIR = group.argument(IArgument.string("module-data-dir", "PATH", "Sets the directory where the jar modules' data should be saved.", "modules"));
        SYSTEM_DATA_DIR = group.argument(IArgument.string("system-data-dir", "PATH", "Sets the directory where system data should be saved.", "system"));
    }

    private MinestomArguments() {
        throw new UnsupportedOperationException();
    }

    public static boolean process(String[] args) {
        ROOT_GROUP.readCommandLine(args);
        if (SHOW_HELP.value()) {
            ROOT_GROUP.printHelp();
            return false;
        }
        return true;
    }

}
