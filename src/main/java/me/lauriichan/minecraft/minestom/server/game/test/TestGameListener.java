package me.lauriichan.minecraft.minestom.server.game.test;

import me.lauriichan.minecraft.minestom.server.game.IPhasedListener;

public class TestGameListener implements IPhasedListener<TestGame> {

    @Override
    public Class<TestGame> gameType() {
        return TestGame.class;
    }

}
