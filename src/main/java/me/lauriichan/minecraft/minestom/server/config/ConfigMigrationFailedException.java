package me.lauriichan.minecraft.minestom.server.config;

public final class ConfigMigrationFailedException extends Exception {

    private static final long serialVersionUID = -8543752837139689144L;

    public ConfigMigrationFailedException(final String message) {
        super(message);
    }

    public ConfigMigrationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
