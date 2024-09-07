package me.lauriichan.minecraft.minestom.server.command.argument;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
import me.lauriichan.minecraft.minestom.server.command.Arguments;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.util.argument.ArgumentStack;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum.Format;

@Extension
@SuppressWarnings("rawtypes")
public final class EnumArgumentType extends ArgumentType<Enum, Enum> {

    public EnumArgumentType() {
        super(Enum.class, false);
    }

    @Override
    protected Enum map(Actor<?> actor, Enum primitive, IArgumentMap map) {
        return primitive;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Argument<Enum> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        ArgumentStack stack = new ArgumentStack(1);
        Class<? extends Enum> clazz = map.getClassOrStack("enum", Enum.class, stack);
        stack.throwIfPresent();
        return (Argument<Enum>) Arguments.Enum(id, clazz).setFormat(Format.LOWER_CASED);
    }

}
