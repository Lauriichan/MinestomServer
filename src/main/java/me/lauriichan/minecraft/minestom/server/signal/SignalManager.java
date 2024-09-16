package me.lauriichan.minecraft.minestom.server.signal;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.minecraft.minestom.server.module.ExternModule;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;

public final class SignalManager {

    private final ObjectArrayList<SignalContainer> containers = new ObjectArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public final ObjectList<SignalContainer> getContainers() {
        lock.readLock().lock();
        try {
            return ObjectLists.unmodifiable(new ObjectArrayList<>(containers));
        } finally {
            lock.readLock().unlock();
        }
    }

    public final ObjectList<SignalContainer> getContainersOf(IMinestomModule module) {
        Objects.requireNonNull(module);
        if (module instanceof ExternModule<?> extern) {
            module = extern.moduleInstance();
        }
        IMinestomModule fModule = module;
        ObjectArrayList<SignalContainer> list;
        lock.readLock().lock();
        try {
            list = containers.stream().filter(container -> container.module() == fModule).collect(ObjectArrayList.toList());
        } finally {
            lock.readLock().unlock();
        }
        if (list.isEmpty()){
            return ObjectLists.emptyList();
        }
        return ObjectLists.unmodifiable(list);
    }

    public final SignalContainer register(IMinestomModule module, ISignalHandler handler) {
        Objects.requireNonNull(module);
        if (module instanceof ExternModule<?> extern) {
            module = extern.moduleInstance();
        }
        lock.readLock().lock();
        try {
            for (int index = 0; index < containers.size(); index++) {
                final SignalContainer container = containers.get(index);
                if (Objects.equals(container.handler(), handler)) {
                    if (container.module() == module){
                        throw new IllegalStateException("Can't register a already registered handler using a different module.");
                    }
                    return container;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        SignalContainer container = handler.newContainer(module);
        if (container.module() != module) {
            throw new IllegalStateException("Can't use different module then the one registering the container.");
        }
        lock.writeLock().lock();
        try {
            containers.add(container);
        } finally {
            lock.writeLock().unlock();
        }
        return container;
    }

    public final boolean unregister(final SignalContainer container) {
        lock.readLock().lock();
        try {
            if (!containers.contains(container)) {
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            return containers.remove(container);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public final void unregisterAll(final IMinestomModule module){
        for (SignalContainer container : getContainersOf(module)) {
            unregister(container);
        }
    }

    public final <S extends ISignal> boolean call(S signal) {
        SignalContext<S> context = new SignalContext<>(signal);
        call(context);
        return context.isCancelled();
    }

    public final <S extends ISignal> boolean call(S signal, Consumer<SignalContext<S>> contextSetup) {
        SignalContext<S> context = new SignalContext<>(signal);
        if (contextSetup != null) {
            contextSetup.accept(context);
        }
        call(context);
        return context.isCancelled();
    }

    public final <S extends ISignal> void call(SignalContext<S> context) {
        SignalContainer[] containers;
        lock.readLock().lock();
        try {
            if (this.containers.isEmpty()) {
                return;
            }
            containers = this.containers.toArray(new SignalContainer[this.containers.size()]);
        } finally {
            lock.readLock().unlock();
        }
        for (SignalContainer current : containers) {
            if (context.isCancelled() && !current.allowsCancelled()) {
                continue;
            }
            current.handleSignal(this, context);
        }
    }

}
