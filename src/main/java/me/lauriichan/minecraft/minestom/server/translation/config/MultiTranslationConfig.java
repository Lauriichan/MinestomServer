package me.lauriichan.minecraft.minestom.server.translation.config;

import me.lauriichan.minecraft.minestom.server.config.IMultiConfigExtension;
import me.lauriichan.minecraft.minestom.server.extension.Extension;
import me.lauriichan.minecraft.minestom.server.module.IMinestomModule;
import me.lauriichan.minecraft.minestom.server.module.ModuleConditionConstant;
import me.lauriichan.minecraft.minestom.server.translation.config.advanced.AdvancedTranslationConfig;
import me.lauriichan.minecraft.minestom.server.translation.config.basic.BasicTranslationConfig;

@Extension
public final class MultiTranslationConfig implements IMultiConfigExtension<String, IMinestomModule, TranslationConfig> {

    @Override
    public String getConfigKey(IMinestomModule element) {
        return element.description().id();
    }

    @Override
    public String path(IMinestomModule element) {
        if (isMultiLanguage(element)) {
            return "fs://" + element.dataRoot().resolve("translation").toString();
        }
        return "fs://" + element.dataRoot().resolve("translation.json").toString();
    }

    @Override
    public TranslationConfig create(IMinestomModule element) {
        return isMultiLanguage(element) ? new AdvancedTranslationConfig(element) : new BasicTranslationConfig(element);
    }

    private boolean isMultiLanguage(IMinestomModule module) {
        return module.conditionMap().set(ModuleConditionConstant.USE_MULTILANG_CONFIG)
            && module.conditionMap().value(ModuleConditionConstant.USE_MULTILANG_CONFIG);
    }

}
