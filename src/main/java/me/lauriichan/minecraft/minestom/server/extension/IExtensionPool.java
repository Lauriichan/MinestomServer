package me.lauriichan.minecraft.minestom.server.extension;

import java.util.Objects;
import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap.Entry;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import it.unimi.dsi.fastutil.objects.ObjectList;

public interface IExtensionPool<T extends IExtension> {

    Class<T> type();

    int count();

    Object2ObjectMap<IMinestomModule, ObjectList<T>> extensions();

    boolean instantiated();

    Object2ObjectMap<IMinestomModule, ObjectList<Class<? extends T>>> extensionClasses();

    default void callInstances(final BiConsumer<IMinestomModule, T> call) {
        Objects.requireNonNull(call);
        for (final Entry<IMinestomModule, ObjectList<T>> entry : extensions().object2ObjectEntrySet()) {
            IMinestomModule module = entry.getKey();
            for (final T extension : entry.getValue()) {
                call.accept(module, extension);
            }
        }
    }

    default void callClasses(final BiConsumer<IMinestomModule, Class<? extends T>> call) {
        Objects.requireNonNull(call);
        for (final Entry<IMinestomModule, ObjectList<Class<? extends T>>> entry : extensionClasses().object2ObjectEntrySet()) {
            IMinestomModule module = entry.getKey();
            for (final Class<? extends T> extension : entry.getValue()) {
                call.accept(module, extension);
            }
        }
    }

}
