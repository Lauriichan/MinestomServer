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
public final class StringArgumentType extends ArgumentType<String, String> {

    public StringArgumentType() {
        super(String.class, true);
    }

    @Override
    public String map(Actor<?> actor, String primitive, IArgumentMap map) {
        return primitive;
    }

    @Override
    protected void suggest(Actor<?> actor, CommandContext context, Suggestion suggestion, IArgumentMap map) {
        String[] collection = map.get("collection", String[].class).orElse(null);
        if (collection == null || collection.length == 0) {
            return;
        }
        String[] collectionTooltips = map.get("collection-tooltips", String[].class).orElse(null);
        if (collectionTooltips == null) {
            for (String entry : collection) {
                suggestion.addEntry(entry(entry));
            }
            return;
        }
        for (int i = 0; i < collection.length; i++) {
            suggestion.addEntry(entry(collection[i], actor.getMessageAsComponent(collectionTooltips[i])));
        }
    }

    @Override
    protected Argument<String> createArgument(IMinestomModule module, String id, IArgumentMap map) {
        boolean forcedWord = map.get("word", Boolean.class).orElse(false);
        String[] collection = map.get("collection", String[].class).orElse(null);
        String[] collectionTooltips = map.get("collection-tooltip", String[].class).orElse(null);
        boolean isWord = true;
        if (collection != null && collection.length != 0) {
            if (collectionTooltips != null && collectionTooltips.length != collection.length) {
                throw new IllegalArgumentException(
                    "If collection tooltips are used then they need to have the same length as the collection array");
            }
            for (String entry : collection) {
                if (!entry.contains(" ")) {
                    continue;
                }
                if (forcedWord) {
                    throw new IllegalArgumentException("Can't have spaces in collection restriction if argument is a forced word");
                }
                isWord = false;
            }
        }
        return (isWord || forcedWord) ? Arguments.Word(id) : Arguments.String(id);
    }

}
