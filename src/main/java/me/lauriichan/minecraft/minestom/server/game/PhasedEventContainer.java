package me.lauriichan.minecraft.minestom.server.game;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public final class PhasedEventContainer<G extends Game<G>> {

    private final String listenerName;

    private final IGameListener<G> listener;

    private final GameState<G> gameState;
    private final EventNode<Event> node;

    private final PhasedEventReceiver<G, ?>[] receivers;
    private final ObjectList<PhasedEventReceiver<G, ?>> active;

    public PhasedEventContainer(GameState<G> gameState, IGameListener<G> listener, final PhasedEventReceiver<G, ?>[] receivers) {
        this.listenerName = listener.getClass().getSimpleName();
        this.listener = listener;
        this.gameState = gameState;
        this.node = EventNode.all(gameState.name() + "/" + listenerName);
        for (PhasedEventReceiver<G, ?> receiver : receivers) {
            receiver.setup(this);
        }
        this.receivers = receivers;
        this.active = ObjectLists.synchronize(new ObjectArrayList<>(receivers.length));
    }

    public boolean hasActive() {
        return !active.isEmpty();
    }

    public String name() {
        return listenerName;
    }

    void unregister() {
        if (active.isEmpty()) {
            return;
        }
        active.forEach(node::removeListener);
        active.clear();
    }

    void update(Class<? extends Phase<?>> newPhase) {
        for (PhasedEventReceiver<G, ?> receiver : receivers) {
            if (!receiver.shouldBeActive(newPhase)) {
                if (receiver.active.compareAndSet(true, false)) {
                    node.removeListener(receiver);
                    gameState.logger().debug("Deactivating {0}#{1}", listenerName, receiver.get());
                }
                continue;
            }
            if (receiver.active.compareAndSet(false, true)) {
                node.addListener(receiver);
                gameState.logger().debug("Activating {0}#{1}", listenerName, receiver.get());
            }
        }
    }

    public GameState<G> gameState() {
        return gameState;
    }

    public IGameListener<G> listener() {
        return listener;
    }

}
