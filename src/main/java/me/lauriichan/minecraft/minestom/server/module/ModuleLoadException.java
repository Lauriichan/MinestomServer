package me.lauriichan.minecraft.minestom.server.module;

public final class ModuleLoadException extends Exception {

    private static final long serialVersionUID = 6976954816721893403L;

    public ModuleLoadException(final String message) {
        super(message);
    }

    public ModuleLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
