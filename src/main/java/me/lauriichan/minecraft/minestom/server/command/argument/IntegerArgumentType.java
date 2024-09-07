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
public final class IntegerArgumentType extends ArgumentType<Integer, Integer> {

    public IntegerArgumentType() {
        super(Integer.class, false);
    }

    @Override
    protected Integer map(Actor<?> actor, Integer primitive, IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<Integer> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        ArgumentNumber<Integer> argument = Arguments.Integer(id);
        map.get("min", Number.class).ifPresent(num -> argument.min(num.intValue()));
        map.get("max", Number.class).ifPresent(num -> argument.max(num.intValue()));
        return argument;
    }

}
