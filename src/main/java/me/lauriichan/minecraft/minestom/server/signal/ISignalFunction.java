package me.lauriichan.minecraft.minestom.server.signal;

@FunctionalInterface
public interface ISignalFunction<S extends ISignal> {
    
    void onSignal(SignalContext<S> context) throws Throwable;
    
}
