package me.lauriichan.minecraft.minestom.server.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.minecraft.minestom.server.game.phased.PhasedType;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;

public final class GameProvider<G extends Game<G>> {

    private final GameManager manager;

    private final String id;

    private final Class<G> gameType;

    private final ObjectList<Class<? extends Phase<?>>> phases;
    private final ObjectList<PhasedType<? extends Task<G>>> tasks;
    private final ObjectList<IPhasedListener<G>> listeners;

    private final Object2ObjectMap<String, GameState<G>> states = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());

    @SuppressWarnings("unchecked")
    GameProvider(final GameManager manager, final String id, final Class<G> gameType, final ObjectList<Class<? extends Phase<?>>> phaseList,
        final ObjectList<Class<? extends Task<?>>> taskList, final ObjectList<IPhasedListener<?>> listenerList) {
        this.manager = manager;
        this.id = id;
        this.gameType = gameType;
        this.phases = phaseList;
        if (taskList.isEmpty()) {
            this.tasks = ObjectLists.emptyList();
        } else {
            ObjectArrayList<PhasedType<? extends Task<G>>> tasks = new ObjectArrayList<>(taskList.size());
            for (Class<? extends Task<?>> taskType : taskList) {
                tasks.add(new PhasedType<>((Class<? extends Task<G>>) taskType));
            }
            this.tasks = ObjectLists.unmodifiable(tasks);
        }
        if (listenerList.isEmpty()) {
            this.listeners = ObjectLists.emptyList();
        } else {
            this.listeners = ObjectLists
                .unmodifiable(listenerList.stream().map(listener -> (IPhasedListener<G>) listener).collect(ObjectArrayList.toList()));
        }
    }

    public GameManager manager() {
        return manager;
    }

    public String id() {
        return id;
    }

    public Class<G> gameType() {
        return gameType;
    }

    public ObjectList<Class<? extends Phase<?>>> phases() {
        return phases;
    }

    public ObjectList<PhasedType<? extends Task<G>>> tasks() {
        return tasks;
    }

    public ObjectList<IPhasedListener<G>> listeners() {
        return listeners;
    }

    public boolean hasState(String name) {
        return states.containsKey(name);
    }

    public GameState<G> newState(IMinestomModule creator, String name) {
        GameState<G> state = states.get(name);
        if (state != null) {
            throw new IllegalArgumentException("There is already a game instance with the name '" + name + "'");
        }
        state = new GameState<>(creator, name, this);
        states.put(name, state);
        return state;
    }

    public GameState<G> getState(String name) {
        return states.get(name);
    }

    public ObjectCollection<GameState<G>> states() {
        return ObjectCollections.unmodifiable(states.values());
    }

    void remove(GameState<G> state) {
        states.remove(state.name(), state);
    }

}
