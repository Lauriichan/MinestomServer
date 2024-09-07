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
public final class DoubleArgumentType extends ArgumentType<Double, Double> {

    public DoubleArgumentType() {
        super(Double.class, false);
    }

    @Override
    protected Double map(Actor<?> actor, Double primitive, IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<Double> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        ArgumentNumber<Double> argument = Arguments.Double(id);
        map.get("min", Number.class).ifPresent(num -> argument.min(num.doubleValue()));
        map.get("max", Number.class).ifPresent(num -> argument.max(num.doubleValue()));
        return argument;
    }

}
