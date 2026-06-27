package me.lauriichan.minecraft.minestom.server.game.phased;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.minecraft.minestom.server.game.Phase;

public class PhasedObjRef<T> implements IPhased<T> {

    protected final T instance;

    protected final boolean blacklist;
    protected final ObjectList<Class<? extends Phase<?>>> phases;

    public PhasedObjRef(T element, boolean blacklist, @SuppressWarnings("unchecked") Class<? extends Phase<?>>... phases) {
        this.instance = element;
        if (phases == null || phases.length == 0) {
            this.blacklist = false;
            this.phases = ObjectLists.emptyList();
            return;
        }
        ObjectArrayList<Class<? extends Phase<?>>> knownPhases = new ObjectArrayList<>();
        for (Class<? extends Phase<?>> phase : phases) {
            if (phase == null || knownPhases.contains(phase)) {
                continue;
            }
            knownPhases.add(phase);
        }
        this.blacklist = !knownPhases.isEmpty() && blacklist;
        this.phases = knownPhases.isEmpty() ? ObjectLists.emptyList() : ObjectLists.unmodifiable(knownPhases);
    }

    @Override
    public final T get() {
        return instance;
    }

    @Override
    public final boolean isBlacklist() {
        return blacklist;
    }

    @Override
    public final ObjectList<Class<? extends Phase<?>>> phases() {
        return phases;
    }

    @Override
    public final boolean shouldBeActive(Class<? extends Phase<?>> phase) {
        if (phases.isEmpty()) {
            return true;
        }
        if (blacklist) {
            return !phases.contains(phase);
        }
        return phases.contains(phase);
    }

}
