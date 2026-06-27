package me.lauriichan.minecraft.minestom.server.game;

import java.util.concurrent.atomic.AtomicBoolean;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.minecraft.minestom.server.game.phased.IPhased;
import me.lauriichan.minecraft.minestom.server.game.phased.PhasedObjRef;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;

public final class PhasedEventReceiver<G extends Game<G>, E extends Event> implements EventListener<E>, IPhased<String> {

    @FunctionalInterface
    public static interface IEventFunc<G extends Game<G>, E extends Event> {
        Result callFunc(GameState<G> state, E event) throws Throwable;
    }

    @FunctionalInterface
    public static interface IEventFunc0<G extends Game<G>, E extends Event> extends IEventFunc<G, E> {
        void call(E event) throws Throwable;

        @Override
        default Result callFunc(GameState<G> state, E event) throws Throwable {
            call(event);
            return Result.SUCCESS;
        }
    }

    @FunctionalInterface
    public static interface IEventFunc1<G extends Game<G>, E extends Event> extends IEventFunc<G, E> {
        Result call(E event) throws Throwable;

        @Override
        default Result callFunc(GameState<G> state, E event) throws Throwable {
            return call(event);
        }
    }

    @FunctionalInterface
    public static interface IEventFunc2<G extends Game<G>, E extends Event> extends IEventFunc<G, E> {
        void call(GameState<G> state, E event) throws Throwable;

        @Override
        default Result callFunc(GameState<G> state, E event) throws Throwable {
            call(state, event);
            return Result.SUCCESS;
        }
    }

    @FunctionalInterface
    public static interface IEventFunc3<G extends Game<G>, E extends Event> extends IEventFunc<G, E> {
        void call(E event, GameState<G> state) throws Throwable;

        @Override
        default Result callFunc(GameState<G> state, E event) throws Throwable {
            call(event, state);
            return Result.SUCCESS;
        }
    }

    @FunctionalInterface
    public static interface IEventFunc4<G extends Game<G>, E extends Event> extends IEventFunc<G, E> {
        Result call(E event, GameState<G> state) throws Throwable;

        @Override
        default Result callFunc(GameState<G> state, E event) throws Throwable {
            return call(event, state);
        }
    }

    final AtomicBoolean active = new AtomicBoolean(false);

    private final String name;
    private final Class<E> eventType;
    private final PhasedObjRef<IEventFunc<G, E>> phasedFunction;

    private PhasedEventContainer<G> container;

    public PhasedEventReceiver(String name, Class<E> eventType, PhasedObjRef<IEventFunc<G, E>> phasedFunction) {
        this.name = name;
        this.eventType = eventType;
        this.phasedFunction = phasedFunction;
    }

    void setup(PhasedEventContainer<G> container) {
        if (this.container != null) {
            throw new UnsupportedOperationException("Game state can only be initialized once");
        }
        this.container = container;
    }

    @Override
    public String get() {
        return name;
    }

    @Override
    public boolean isBlacklist() {
        return phasedFunction.isBlacklist();
    }

    @Override
    public boolean shouldBeActive(Class<? extends Phase<?>> newPhase) {
        return phasedFunction.shouldBeActive(newPhase);
    }

    @Override
    public ObjectList<Class<? extends Phase<?>>> phases() {
        return phasedFunction.phases();
    }

    @Override
    public Class<E> eventType() {
        return eventType;
    }

    @Override
    public Result run(E event) {
        GameState<G> state = container.gameState();
        try {
            return phasedFunction.get().callFunc(state, event);
        } catch (Throwable e) {
            state.logger().error("Failed to execute event '{0}#{1}'", e, container.name(), name);
            return Result.EXCEPTION;
        }
    }

}
