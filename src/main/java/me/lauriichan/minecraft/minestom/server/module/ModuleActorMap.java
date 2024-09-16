package me.lauriichan.minecraft.minestom.server.module;

import java.util.Objects;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.lauriichan.minecraft.minestom.server.command.Actor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.entity.Player;

public final class ModuleActorMap {

    private final Object2ObjectMap<UUID, Actor<Player>> actorMap = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private volatile Actor<ConsoleSender> console;
    private volatile Actor<ServerSender> server;

    private final IMinestomModule module;

    public ModuleActorMap(IMinestomModule module) {
        this.module = module;
    }

    public void remove(UUID uuid) {
        actorMap.remove(uuid);
    }

    @SuppressWarnings("unchecked")
    public <T extends CommandSender> Actor<T> actor(T sender) {
        Objects.requireNonNull(sender);
        if (sender instanceof Player player) {
            Actor<Player> actor = actorMap.get(player.getUuid());
            if (actor == null) {
                actor = new Actor<>(player, module);
                actorMap.put(player.getUuid(), actor);
            }
            return (Actor<T>) actor;
        }
        if (sender instanceof ServerSender serverSender) {
            if (server != null) {
                return (Actor<T>) server;
            }
            return (Actor<T>) (server = new Actor<>(serverSender, module));
        }
        if (sender instanceof ConsoleSender consoleSender) {
            if (console != null) {
                return (Actor<T>) console;
            }
            return (Actor<T>) (console = new Actor<>(consoleSender, module));
        }
        throw new IllegalArgumentException("Unknown CommandSender type '" + sender.getClass().getName() + "'!");
    }

}
