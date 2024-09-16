package me.lauriichan.minecraft.minestom.server.permission;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.minecraft.minestom.server.signal.ISignal;

public final class RegisterPermissionProviderSignal implements ISignal {

    private final ObjectArrayList<PermissionProvider> providers = new ObjectArrayList<>();

    public ObjectList<PermissionProvider> providers() {
        return ObjectLists.unmodifiable(providers);
    }

    public void register(PermissionProvider provider) {
        if (providers.stream().anyMatch(prov -> prov.id().equals(provider.id()))) {
            throw new IllegalArgumentException("There is already a permission provider with id '" + provider.id() + "'");
        }
        providers.add(provider);
    }

}
