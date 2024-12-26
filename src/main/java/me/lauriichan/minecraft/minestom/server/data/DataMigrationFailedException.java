package me.lauriichan.minecraft.minestom.server.data;

public final class DataMigrationFailedException extends Exception {

    private static final long serialVersionUID = -8543752837139689143L;

    public DataMigrationFailedException(final String message) {
        super(message);
    }

    public DataMigrationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
