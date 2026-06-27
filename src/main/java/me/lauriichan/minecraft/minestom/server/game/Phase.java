package me.lauriichan.minecraft.minestom.server.game;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public abstract class Phase<G extends Game<G>> implements IExtension {
    
    protected void onBegin(GameState<G> state) {}
    
    protected void onTick(GameState<G> state, long delta) {}
    
    protected void onEnd(GameState<G> state) {}
    
    protected void onPhaseChange(GameState<G> state) {}
    
    protected abstract boolean nextPhase();

}
