package me.lauriichan.minecraft.minestom.server.signal;

import me.lauriichan.minecraft.minestom.server.util.attribute.Attributable;

public final class SignalContext<S extends ISignal> extends Attributable {

    private final S signal;
    private final Class<S> signalType;
    private final boolean cancelable;

    private volatile boolean cancelled = false;

    @SuppressWarnings("unchecked")
    public SignalContext(final S signal) {
        this.signal = signal;
        this.signalType = (Class<S>) signal.getClass();
        this.cancelable = ICancelable.class.isAssignableFrom(signalType);
    }

    public S signal() {
        return signal;
    }

    public Class<S> signalType() {
        return signalType;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        if (!cancelable) {
            return;
        }
        this.cancelled = cancelled;
    }

}
