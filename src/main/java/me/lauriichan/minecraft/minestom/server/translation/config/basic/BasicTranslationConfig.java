package me.lauriichan.minecraft.minestom.server.translation.config.basic;

import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.minecraft.minestom.server.config.Configuration;
import me.lauriichan.minecraft.minestom.server.config.IConfigHandler;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.translation.config.TranslationConfig;

public final class BasicTranslationConfig extends TranslationConfig {

    private final MessageManager messageManager;

    public BasicTranslationConfig(final IMinestomModule module) {
        this.messageManager = module.messageManager();
    }

    @Override
    public IConfigHandler handler() {
        return TranslationConfigHandler.TRANSLATION;
    }

    @Override
    public void onLoad(final Configuration configuration) throws Exception {
        loadMessages(configuration, DEFAULT_LANGUAGE, messageManager.getProviders());
    }

    @Override
    public void onSave(final Configuration configuration) throws Exception {
        saveMessages(configuration, DEFAULT_LANGUAGE, messageManager.getProviders());
    }

}
