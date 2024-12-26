package me.lauriichan.minecraft.minestom.server.io.serialization;

public final class SerializationException extends Exception {

    private static final long serialVersionUID = -8910952882954712940L;

    public SerializationException(final String message) {
        super(message);
    }

    public SerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
