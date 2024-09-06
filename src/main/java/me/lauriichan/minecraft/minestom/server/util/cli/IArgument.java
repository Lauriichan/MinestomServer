package me.lauriichan.minecraft.minestom.server.util.cli;

import java.util.function.Function;

public interface IArgument<V> {

    static IArgument<String> string(String name, String[] description, String defaultValue) {
        return string(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<String> string(String name, String description, String defaultValue) {
        return string(name, null, description, defaultValue);
    }

    static IArgument<String> string(String name, String valueName, String[] description, String defaultValue) {
        return string(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<String> string(String name, String valueName, String description, String defaultValue) {
        return new StringArgument(name, valueName, description, defaultValue);
    }

    static IArgument<Number> number(String name, String[] description, Number defaultValue) {
        return number(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<Number> number(String name, String description, Number defaultValue) {
        return number(name, null, description, defaultValue);
    }

    static IArgument<Number> number(String name, String valueName, String[] description, Number defaultValue) {
        return number(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<Number> number(String name, String valueName, String description, Number defaultValue) {
        return new NumberArgument(name, valueName, description, defaultValue);
    }

    static IArgument<Boolean> bool(String name, String[] description, Boolean defaultValue) {
        return bool(name, null, String.join("\n", description), defaultValue);
    }

    static IArgument<Boolean> bool(String name, String description, Boolean defaultValue) {
        return bool(name, null, description, defaultValue);
    }

    static IArgument<Boolean> bool(String name, String valueName, String[] description, Boolean defaultValue) {
        return bool(name, valueName, String.join("\n", description), defaultValue);
    }

    static IArgument<Boolean> bool(String name, String valueName, String description, Boolean defaultValue) {
        return new BooleanArgument(name, valueName, description, defaultValue);
    }

    String name();

    String valueName();

    String description();

    V value();

    V defaultValue();
    
    default <T> IArgument<T> map(Function<V, T> mappingFunc) {
        return new MappedArgument<>(this, mappingFunc);
    }

}
