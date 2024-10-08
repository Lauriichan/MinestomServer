package me.lauriichan.minecraft.minestom.server.command;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.permission.IPermissionAccess;
import me.lauriichan.minecraft.minestom.server.translation.component.ComponentBuilder;
import me.lauriichan.minecraft.minestom.server.util.attribute.Attributable;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

public final class Actor<P extends CommandSender> extends Attributable {

    public static final UUID IMPL_ID = new UUID(0, 0);
    public static final String DEFAULT_LANGUAGE = "en-uk";

    @SuppressWarnings({
        "rawtypes"
    })
    private static final Actor EMPTY = new Actor();

    private final P handle;
    private final MessageManager messageManager;
    
    private final IPermissionAccess permissionAccess;

    private Actor() {
        if (EMPTY != null) {
            throw new UnsupportedOperationException();
        }
        this.handle = null;
        this.messageManager = null;
        this.permissionAccess = null;
    }

    public Actor(P handle, IMinestomModule module) {
        this.handle = Objects.requireNonNull(handle);
        this.messageManager = Objects.requireNonNull(module.messageManager());
        this.permissionAccess = module.server().permissionProvider().access(this);
    }

    public P handle() {
        return handle;
    }

    public MessageManager messageManager() {
        return messageManager;
    }
    
    public IPermissionAccess permissionAccess() {
        return permissionAccess;
    }

    @SuppressWarnings("unchecked")
    public <C extends CommandSender> Actor<C> as(Class<C> type) {
        if (handle != null && type.isAssignableFrom(handle.getClass())) {
            return (Actor<C>) this;
        }
        return (Actor<C>) EMPTY;
    }

    public boolean isValid() {
        return handle != null;
    }

    public String getName() {
        if (handle instanceof Player player) {
            return player.getUsername();
        }
        return "Console";
    }

    public String getLanguage() {
        if (handle instanceof Player player) {
            return player.getLocale().getDisplayLanguage(Locale.ENGLISH).toLowerCase();
        }
        return DEFAULT_LANGUAGE;
    }

    public String getMessageAsString(MessageProvider provider, Key... placeholders) {
        return messageManager.translate(provider, getLanguage(), placeholders);
    }

    public String getMessageAsString(String messageId, Key... placeholders) {
        return messageManager.translate(messageId, getLanguage(), placeholders);
    }

    public ComponentBuilder<?, ?> getMessageAsComponent(MessageProvider provider, Key... placeholders) {
        return ComponentBuilder.parse(messageManager.translate(provider, getLanguage(), placeholders));
    }

    public ComponentBuilder<?, ?> getMessageAsComponent(String messageId, Key... placeholders) {
        return ComponentBuilder.parse(messageManager.translate(messageId, getLanguage(), placeholders));
    }

    public IMessage getMessage(MessageProvider provider) {
        return provider.getMessage(getLanguage());
    }

    public IMessage getMessage(String messageId) {
        return messageManager.getMessage(messageId, getLanguage());
    }

    public void send(String message) {
        ComponentBuilder.parse(message).send(handle);
    }

    public void send(IMessage message, Key... placeholders) {
        send(messageManager.format(message, placeholders));
    }

    public void send(MessageProvider provider, Key... placeholders) {
        send(messageManager.translate(provider, getLanguage(), placeholders));
    }

    public void send(String messageId, Key... placeholders) {
        send(messageManager.translate(messageId, getLanguage(), placeholders));
    }

    public boolean isPermitted(String permission) {
        if (permissionAccess == null) {
            return true;
        }
        return permissionAccess.isAllowed(permission);
    }

}
