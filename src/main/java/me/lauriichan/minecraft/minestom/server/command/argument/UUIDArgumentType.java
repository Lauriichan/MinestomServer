package me.lauriichan.minecraft.minestom.server.command.argument;

import java.util.UUID;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
import me.lauriichan.minecraft.minestom.server.command.Arguments;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.arguments.Argument;

@Extension
public final class UUIDArgumentType extends ArgumentType<UUID, UUID> {

    public UUIDArgumentType() {
        super(UUID.class, false);
    }

    @Override
    protected UUID map(Actor<?> actor, UUID primitive, IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<UUID> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        return Arguments.UUID(id);
    }

}
