package me.lauriichan.minecraft.minestom.server.command;

import me.lauriichan.laylib.reflection.StackTracker;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;

public final class MinestomCommandManager {

    private final SystemModule module;
    
    public MinestomCommandManager(SystemModule module) {
        if (module.server().commandManager() != null) {
            throw new UnsupportedOperationException("Only one instance allowed");
        }
        this.module = module;
        module.extension(ArgumentType.class, true).callInstances((mod, ext) -> {
            // TODO: Implement
        });;
    }

    public void registerCommands() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        module.extension(ICommandExtension.class, true).callInstances((mod, ext) -> {
            // TODO: Implement
        });;
    }

}
