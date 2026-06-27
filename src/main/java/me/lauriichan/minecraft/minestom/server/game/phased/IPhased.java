package me.lauriichan.minecraft.minestom.server.game.phased;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.server.game.Phase;

public interface IPhased<T> {
    
    T get();
    
    boolean isBlacklist();
    
    ObjectList<Class<? extends Phase<?>>> phases();
    
    boolean shouldBeActive(Class<? extends Phase<?>> phase);

}
