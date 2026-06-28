package me.lauriichan.minecraft.minestom.server.game;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public abstract class GameListener<G extends Game<G>> implements IExtension {

    protected PhasedEventContainer<G> newContainer(GameState<G> gameState) {
        throw new UnsupportedOperationException();
    }

}
