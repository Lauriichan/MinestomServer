package me.lauriichan.minecraft.minestom.module;

public final class ModuleLoadException extends Exception {

    private static final long serialVersionUID = 6976954816721893403L;

    public ModuleLoadException(String message) {
        super(message);
    }

    public ModuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
