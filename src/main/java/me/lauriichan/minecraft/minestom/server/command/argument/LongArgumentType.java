package me.lauriichan.minecraft.minestom.server.command.argument;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
import me.lauriichan.minecraft.minestom.server.command.Arguments;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;

@Extension
public final class LongArgumentType extends ArgumentType<Long, Long> {

    public LongArgumentType() {
        super(Long.class, false);
    }

    @Override
    protected Long map(Actor<?> actor, Long primitive, IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<Long> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        ArgumentNumber<Long> argument = Arguments.Long(id);
        map.get("min", Number.class).ifPresent(num -> argument.min(num.longValue()));
        map.get("max", Number.class).ifPresent(num -> argument.max(num.longValue()));
        return argument;
    }

}
