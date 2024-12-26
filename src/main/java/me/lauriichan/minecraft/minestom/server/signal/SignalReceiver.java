package me.lauriichan.minecraft.minestom.server.signal;

public final class SignalReceiver<S extends ISignal> {

    private final Class<S> signalType;
    private final ISignalFunction<S> receiver;

    private final boolean allowCancelled;

    public SignalReceiver(final Class<S> signalType, final ISignalFunction<S> receiver) {
        this(signalType, receiver, true);
    }

    public SignalReceiver(final Class<S> signalType, final ISignalFunction<S> receiver, final boolean allowCancelled) {
        this.signalType = signalType;
        this.receiver = receiver;
        this.allowCancelled = allowCancelled;
    }

    public boolean isSignalSuitable(final Class<? extends ISignal> signalType) {
        return this.signalType.isAssignableFrom(signalType);
    }

    public boolean allowsCancelled() {
        return allowCancelled;
    }

    void handle(final SignalManager manager, final SignalContainer container, final SignalContext<S> context) {
        try {
            receiver.onSignal(context);
        } catch (final Throwable e) {
            container.module().logger().error("Failed to run signal handler", e);
        }
    }

}
