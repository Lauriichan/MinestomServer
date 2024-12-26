package me.lauriichan.minecraft.minestom.server.util.instance;

public final class SharedInstancesDelegated<E> implements ISharedInstances<E> {

    private final SharedInstances<E> delegate;
    private final IInstanceInvoker invoker;

    public SharedInstancesDelegated(final SharedInstances<E> delegate) {
        this(delegate, IInstanceInvoker.DEFAULT);
    }

    public SharedInstancesDelegated(final SharedInstances<E> delegate, final IInstanceInvoker invoker) {
        this.delegate = delegate;
        this.invoker = invoker;
    }

    @Override
    public void remove(final ClassLoader loader) {
        delegate.remove(loader);
    }

    @Override
    public void remove(final Class<?> clazz) {
        delegate.remove(clazz);
    }

    @Override
    public <T> T getCached(final Class<T> clazz) {
        return delegate.getCached(clazz);
    }

    @Override
    public final <T extends E> T get(final Class<T> clazz, final Object... arguments) throws Throwable {
        return delegate.get(invoker, clazz, arguments);
    }

}
