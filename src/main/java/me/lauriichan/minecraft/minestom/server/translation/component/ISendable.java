package me.lauriichan.minecraft.minestom.server.translation.component;

import java.util.function.BiConsumer;

import me.lauriichan.minecraft.minestom.server.command.Actor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;

public interface ISendable {

    static enum MessageType {

        CHAT(Audience::sendMessage),
        ACTION_BAR(Audience::sendActionBar),
        LIST_FOOTER(Audience::sendPlayerListFooter),
        LIST_HEADER(Audience::sendPlayerListHeader);

        private final BiConsumer<Audience, Component> sendFunc;

        private MessageType(BiConsumer<Audience, Component> sendFunc) {
            this.sendFunc = sendFunc;
        }

        public void send(Audience audience, Component component) {
            sendFunc.accept(audience, component);
        }

    }

    default void send(final Actor<?> actor) {
        send(actor, MessageType.CHAT);
    }

    default void send(final Actor<?> actor, final MessageType type) {
        type.send(actor.handle(), buildComponent());
    }

    default void send(final Audience audience) {
        send(audience, MessageType.CHAT);
    }

    default void send(final Audience audience, final MessageType type) {
        type.send(audience, buildComponent());
    }

    default void sendConsole() {
        send(MinecraftServer.getCommandManager().getConsoleSender());
    }

    default void sendBroadcast() {
        sendBroadcast(MessageType.CHAT);
    }

    default void sendBroadcast(final MessageType type) {
        Component component = buildComponent();
        for (Audience player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            type.send(player, component);
        }
    }

    Component buildComponent();

}
