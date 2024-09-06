package me.lauriichan.minecraft.minestom.server.module;

public abstract class ModuleException extends Exception {

    private static final long serialVersionUID = 6976954816721893403L;

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
