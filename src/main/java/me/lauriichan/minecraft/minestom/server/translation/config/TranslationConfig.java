package me.lauriichan.minecraft.minestom.server.translation.config;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.minecraft.minestom.server.config.Configuration;
import me.lauriichan.minecraft.minestom.server.config.IConfigExtension;
import me.lauriichan.minecraft.minestom.server.translation.provider.SimpleMessage;
import me.lauriichan.minecraft.minestom.server.translation.provider.SimpleMessageProvider;

public abstract class TranslationConfig implements IConfigExtension {
    
    public static final String DEFAULT_LANGUAGE = "en-uk";

    protected void loadMessages(final Configuration configuration, final String language, final MessageProvider[] providers) {
        for (final MessageProvider provider : providers) {
            if (!(provider.getMessage(language) instanceof final SimpleMessage message)) {
                continue;
            }
            message.translation(configuration.get(provider.getId(), String.class));
        }
    }

    protected void saveMessages(final Configuration configuration, final String language, final MessageProvider[] providers) {
        IMessage message;
        for (final MessageProvider provider : providers) {
            message = provider.getMessage(language);
            if (message == null) {
                if (provider instanceof final SimpleMessageProvider simpleProvider) {
                    configuration.set(provider.getId(), simpleProvider.getFallback());
                }
                continue;
            }
            configuration.set(message.id(), message.value());
        }
    }

}
