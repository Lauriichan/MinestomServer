package me.lauriichan.minecraft.minestom.server.game;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;
import me.lauriichan.minecraft.minestom.server.tick.AbstractTickTimer;

@ExtensionPoint
public abstract class Game<G extends Game<G>> implements IExtension {
    
    protected void onStart(GameState<G> state, AbstractTickTimer timer) {}
    
    protected void onTick(GameState<G> state, long delta) {}
    
    protected void onTickPostPhase(GameState<G> state, long delta) {}
    
    protected void onTickPostTask(GameState<G> state, long delta) {}
    
    protected void onStop(GameState<G> state) {}
    
    protected boolean shouldRestart(GameState<G> state) {
        return true;
    }

}
