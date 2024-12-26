package me.lauriichan.minecraft.minestom.server.util.cli;

import java.util.function.Function;

public interface IArgument<V> {

    static IArgument<String> string(final String name, final String[] description, final String defaultValue) {
        return string(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<String> string(final String name, final String description, final String defaultValue) {
        return string(name, null, description, defaultValue);
    }

    static IArgument<String> string(final String name, final String valueName, final String[] description, final String defaultValue) {
        return string(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<String> string(final String name, final String valueName, final String description, final String defaultValue) {
        return new StringArgument(name, valueName, description, defaultValue);
    }

    static IArgument<Number> number(final String name, final String[] description, final Number defaultValue) {
        return number(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<Number> number(final String name, final String description, final Number defaultValue) {
        return number(name, null, description, defaultValue);
    }

    static IArgument<Number> number(final String name, final String valueName, final String[] description, final Number defaultValue) {
        return number(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<Number> number(final String name, final String valueName, final String description, final Number defaultValue) {
        return new NumberArgument(name, valueName, description, defaultValue);
    }

    static IArgument<Boolean> bool(final String name, final String[] description, final Boolean defaultValue) {
        return bool(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<Boolean> bool(final String name, final String description, final Boolean defaultValue) {
        return bool(name, null, description, defaultValue);
    }

    static IArgument<Boolean> bool(final String name, final String valueName, final String[] description, final Boolean defaultValue) {
        return bool(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<Boolean> bool(final String name, final String valueName, final String description, final Boolean defaultValue) {
        return new BooleanArgument(name, valueName, description, defaultValue);
    }

    String name();

    String valueName();

    String description();

    V value();

    V defaultValue();

    default <T> IArgument<T> map(final Function<V, T> mappingFunc) {
        return new MappedArgument<>(this, mappingFunc);
    }

}
