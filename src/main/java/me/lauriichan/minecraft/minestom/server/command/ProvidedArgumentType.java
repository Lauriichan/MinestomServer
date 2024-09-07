package me.lauriichan.minecraft.minestom.server.command;

import java.util.Objects;

import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;

public abstract non-sealed class ProvidedArgumentType<T> implements IArgumentType<T> {

    private final Class<T> type;

    public ProvidedArgumentType(Class<T> type) {
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public Class<T> type() {
        return type;
    }

    public abstract T provide(Actor<?> actor, IArgumentMap map);

}
