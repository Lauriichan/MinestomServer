package me.lauriichan.minecraft.minestom.server.permission;

import java.util.Iterator;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.server.signal.ISignal;

public final class SelectPermissionProviderSignal implements ISignal, Iterable<PermissionProvider> {

    private final ObjectList<PermissionProvider> providers;

    private volatile String id;

    public SelectPermissionProviderSignal(final ObjectList<PermissionProvider> providers) {
        this.providers = providers;
        this.id = providers.isEmpty() ? null : providers.get(0).id();
    }

    public void select(String id) {
        this.id = id;
    }

    public String selected() {
        return id;
    }

    public PermissionProvider get(int index) {
        return providers.get(index);
    }

    public int amount() {
        return providers.size();
    }

    public boolean hasProviders() {
        return !providers.isEmpty();
    }

    @Override
    public Iterator<PermissionProvider> iterator() {
        return providers.iterator();
    }

}
