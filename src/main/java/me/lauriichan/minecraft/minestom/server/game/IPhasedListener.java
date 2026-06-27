package me.lauriichan.minecraft.minestom.server.game;

import me.lauriichan.minecraft.minestom.server.extension.ExtensionPoint;
import me.lauriichan.minecraft.minestom.server.extension.IExtension;

@ExtensionPoint
public interface IPhasedListener<G extends Game<G>> extends IExtension {

    Class<G> gameType();

    default PhasedEventContainer<G> newContainer() {
        throw new UnsupportedOperationException();
    }

}
