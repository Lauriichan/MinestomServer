package me.lauriichan.minecraft.minestom.server.signal;

import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;

public interface ISignalHandler {
    
    default SignalContainer newContainer(IMinestomModule module) {
        throw new UnsupportedOperationException();
    }

}
