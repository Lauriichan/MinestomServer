package me.lauriichan.minecraft.minestom.translation.config.advanced;

import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.laylib.localization.MessageProvider;
import me.lauriichan.minecraft.minestom.config.Configuration;
import me.lauriichan.minecraft.minestom.config.IConfigHandler;
import me.lauriichan.minecraft.minestom.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.translation.config.TranslationConfig;

public final class AdvancedTranslationConfig extends TranslationConfig {

    private final MessageManager messageManager;

    public AdvancedTranslationConfig(final IMinestomModule module) {
        this.messageManager = module.messageManager();
    }

    @Override
    public IConfigHandler handler() {
        return LanguageConfigHandler.LANGUAGE;
    }

    @Override
    public void onLoad(final Configuration configuration) throws Exception {
        if (!configuration.contains(DEFAULT_LANGUAGE)) {
            configuration.getConfiguration(DEFAULT_LANGUAGE, true);
        }
        final MessageProvider[] providers = messageManager.getProviders();
        for (final String key : configuration.keySet()) {
            loadMessages(configuration.getConfiguration(key), key, providers);
        }
    }

    @Override
    public void onSave(final Configuration configuration) throws Exception {
        if (!configuration.contains(DEFAULT_LANGUAGE)) {
            configuration.getConfiguration(DEFAULT_LANGUAGE, true);
        }
        final MessageProvider[] providers = messageManager.getProviders();
        for (final String key : configuration.keySet()) {
            saveMessages(configuration.getConfiguration(key), key, providers);
        }
    }

}
