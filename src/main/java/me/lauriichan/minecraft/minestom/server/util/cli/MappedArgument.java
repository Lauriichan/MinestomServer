package me.lauriichan.minecraft.minestom.server.util.cli;

import java.util.function.Function;

final class MappedArgument<P, V> implements IDelegateArgument<P, V> {

    private final IArgument<P> delegate;
    private final Function<P, V> mappingFunc;

    public MappedArgument(IArgument<P> delegate, Function<P, V> mappingFunc) {
        this.delegate = delegate;
        this.mappingFunc = mappingFunc;
    }

    @Override
    public IArgument<P> delegate() {
        return delegate;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public String valueName() {
        return delegate.valueName();
    }

    @Override
    public String description() {
        return delegate.description();
    }

    @Override
    public V defaultValue() {
        return map(delegate.value());
    }

    @Override
    public V value() {
        return map(delegate.value());
    }

    private V map(P value) {
        if (value == null) {
            return null;
        }
        return mappingFunc.apply(value);
    }

}
