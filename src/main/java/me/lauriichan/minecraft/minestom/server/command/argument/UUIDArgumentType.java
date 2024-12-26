package me.lauriichan.minecraft.minestom.server.command.argument;

import java.util.UUID;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
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
    protected UUID map(final Actor<?> actor, final UUID primitive, final IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<UUID> createArgument(final IMinestomModule module, final String id, final IArgumentMap map) {
        return net.minestom.server.command.builder.arguments.ArgumentType.UUID(id);
    }

}
