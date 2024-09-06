package me.lauriichan.minecraft.minestom.server.command;

import java.util.Objects;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.arguments.Argument;

@ExtensionPoint
public abstract class ArgumentType<P, C> implements IExtension {

    private final Class<C> type;

    public ArgumentType(Class<C> type) {
        this.type = Objects.requireNonNull(type);
    }

    public final Class<C> type() {
        return type;
    }

    public abstract C map(Actor<?> actor, P primitive);

    public abstract Argument<P> create(String id, IArgumentMap map);

}
