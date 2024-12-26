package me.lauriichan.minecraft.minestom.server.command.argument;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;

@Extension
public final class FloatArgumentType extends ArgumentType<Float, Float> {

    public FloatArgumentType() {
        super(Float.class, true);
    }

    @Override
    protected Float map(final Actor<?> actor, final Float primitive, final IArgumentMap map) {
        return primitive;
    }

    @Override
    protected Argument<Float> createArgument(final IMinestomModule module, final String id, final IArgumentMap map) {
        final ArgumentNumber<Float> argument = net.minestom.server.command.builder.arguments.ArgumentType.Float(id);
        map.get("min", Number.class).ifPresent(num -> argument.min(num.floatValue()));
        map.get("max", Number.class).ifPresent(num -> argument.max(num.floatValue()));
        return argument;
    }

}
