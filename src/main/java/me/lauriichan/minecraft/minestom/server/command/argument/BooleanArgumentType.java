package me.lauriichan.minecraft.minestom.server.command.argument;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
import me.lauriichan.minecraft.minestom.server.command.Arguments;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.suggestion.Suggestion;

@Extension
public final class BooleanArgumentType extends ArgumentType<Boolean, Boolean> {

    public BooleanArgumentType() {
        super(Boolean.class, true);
    }

    @Override
    protected Boolean map(Actor<?> actor, Boolean primitive, IArgumentMap map) {
        return primitive;
    }
    
    @Override
    protected void suggest(Actor<?> actor, CommandContext context, Suggestion suggestion, IArgumentMap map) {
        suggestion.addEntry(entry("true"));
        suggestion.addEntry(entry("false"));
    }

    @Override
    protected Argument<Boolean> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        return Arguments.Boolean(id);
    }

}
