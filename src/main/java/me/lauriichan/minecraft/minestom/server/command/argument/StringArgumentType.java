package me.lauriichan.minecraft.minestom.server.command.argument;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import me.lauriichan.minecraft.minestom.server.command.ArgumentType;
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
    public String map(final Actor<?> actor, final String primitive, final IArgumentMap map) {
        return primitive;
    }

    @Override
    protected void suggest(final Actor<?> actor, final CommandContext context, final Suggestion suggestion, final IArgumentMap map) {
        final String[] collection = map.get("collection", String[].class).orElse(null);
        if (collection == null || collection.length == 0) {
            return;
        }
        final String[] collectionTooltips = map.get("collection-tooltips", String[].class).orElse(null);
        if (collectionTooltips == null) {
            for (final String entry : collection) {
                suggestion.addEntry(entry(entry));
            }
            return;
        }
        for (int i = 0; i < collection.length; i++) {
            suggestion.addEntry(entry(collection[i], actor.getMessageAsComponent(collectionTooltips[i])));
        }
    }

    @Override
    protected Argument<String> createArgument(final IMinestomModule module, final String id, final IArgumentMap map) {
        final boolean forcedWord = map.get("word", Boolean.class).orElse(false);
        final String[] collection = map.get("collection", String[].class).orElse(null);
        final String[] collectionTooltips = map.get("collection-tooltip", String[].class).orElse(null);
        boolean isWord = true;
        if (collection != null && collection.length != 0) {
            if (collectionTooltips != null && collectionTooltips.length != collection.length) {
                throw new IllegalArgumentException(
                    "If collection tooltips are used then they need to have the same length as the collection array");
            }
            for (final String entry : collection) {
                if (!entry.contains(" ")) {
                    continue;
                }
                if (forcedWord) {
                    throw new IllegalArgumentException("Can't have spaces in collection restriction if argument is a forced word");
                }
                isWord = false;
            }
        }
        return isWord || forcedWord ? net.minestom.server.command.builder.arguments.ArgumentType.Word(id)
            : net.minestom.server.command.builder.arguments.ArgumentType.String(id);
    }

}
