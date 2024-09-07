package me.lauriichan.minecraft.minestom.server.command;

import java.util.Objects;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.translation.component.ComponentBuilder;
import me.lauriichan.minecraft.minestom.server.util.argument.IArgumentMap;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

@ExtensionPoint
public abstract non-sealed class ArgumentType<P, C> implements IExtension, IArgumentType<C> {

    private final Class<C> type;
    private final boolean withSuggestions;

    public ArgumentType(Class<C> type, boolean withSuggestions) {
        this.type = Objects.requireNonNull(type);
        this.withSuggestions = withSuggestions;
    }

    public final Class<C> type() {
        return type;
    }

    protected abstract C map(Actor<?> actor, P primitive, IArgumentMap map);

    protected void suggest(Actor<?> actor, CommandContext context, Suggestion suggestion, IArgumentMap map) {}

    protected abstract Argument<P> createArgument(IMinestomModule module, String id, IArgumentMap map);

    public final Argument<C> create(IMinestomModule module, String id, IArgumentMap map) {
        Argument<P> argument = createArgument(module, id, map);
        if (withSuggestions) {
            argument
                .setSuggestionCallback((sender, context, suggestion) -> suggest(module.actorMap().actor(sender), context, suggestion, map));
        }
        return argument.map((sender, value) -> map(module.actorMap().actor(sender), value, map));
    }

    /*
     * Suggestion helper
     */

    protected final SuggestionEntry entry(Object object) {
        return new SuggestionEntry(Objects.toString(object));
    }

    protected final SuggestionEntry entry(Object object, ComponentBuilder<?, ?> component) {
        return new SuggestionEntry(Objects.toString(object), component.buildComponent());
    }

    protected final SuggestionEntry entry(Object object, Component component) {
        return new SuggestionEntry(Objects.toString(object), component);
    }

    protected final SuggestionEntry entry(String string) {
        return new SuggestionEntry(string);
    }

    protected final SuggestionEntry entry(String string, ComponentBuilder<?, ?> component) {
        return new SuggestionEntry(string, component.buildComponent());
    }

    protected final SuggestionEntry entry(String string, Component component) {
        return new SuggestionEntry(string, component);
    }

}
