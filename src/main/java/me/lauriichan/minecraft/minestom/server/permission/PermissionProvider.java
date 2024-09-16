package me.lauriichan.minecraft.minestom.server.permission;

import java.util.Objects;

import me.lauriichan.laylib.reflection.StackTracker;
import me.lauriichan.minecraft.minestom.server.MinestomServer;
import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;

public abstract class PermissionProvider {

    private final IMinestomModule module;
    private final String id;

    public PermissionProvider(IMinestomModule module, String id) {
        this.module = Objects.requireNonNull(module);
        this.id = Objects.requireNonNull(id).toLowerCase();
        if (id.isBlank()) {
            throw new IllegalArgumentException("Id can't be blank");
        }
    }

    public final IMinestomModule module() {
        return module;
    }

    public final String id() {
        return id;
    }

    public final void activate() {
        StackTracker.getCallerClass().filter(clz -> clz == MinestomServer.class)
            .orElseThrow(() -> new UnsupportedOperationException("This can only be called by the MinestomServer class"));
        onActivate();
    }

    protected abstract void onActivate();

    public abstract IPermissionAccess access(Actor<?> actor);

}
