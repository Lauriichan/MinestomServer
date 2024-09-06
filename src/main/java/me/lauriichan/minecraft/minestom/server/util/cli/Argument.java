package me.lauriichan.minecraft.minestom.server.util.cli;

import java.util.Objects;

public abstract class Argument<V> implements IArgument<V> {

    private final String name;
    private final String valueName;
    private final String description;

    private final V defaultValue;
    private V value;

    public Argument(String name, String valueName, String description, V defaultValue) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid argument name: " + name);
        }
        if (valueName == null || valueName.isBlank()) {
            valueName = defaultName();
        }
        this.name = name;
        this.valueName = valueName;
        this.description = Objects.requireNonNull(description);
        this.defaultValue = defaultValue;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final String valueName() {
        return valueName;
    }

    @Override
    public final String description() {
        return description;
    }

    final void setValue(String unparsedValue) {
        this.value = parse(unparsedValue);
    }

    @Override
    public final V value() {
        return value == null ? defaultValue : value;
    }

    @Override
    public final V defaultValue() {
        return defaultValue;
    }
    
    protected abstract String defaultName();

    protected abstract V parse(String string);

}
