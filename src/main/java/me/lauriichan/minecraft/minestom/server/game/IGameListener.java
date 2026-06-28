package me.lauriichan.minecraft.minestom.server.game;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IGameListener<G extends Game<G>> extends IExtension {

    default PhasedEventContainer<G> newContainer(GameState<G> gameState) {
        throw new UnsupportedOperationException();
    }

}
