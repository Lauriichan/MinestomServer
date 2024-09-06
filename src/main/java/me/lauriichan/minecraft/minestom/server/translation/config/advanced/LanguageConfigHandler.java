package me.lauriichan.minecraft.minestom.server.translation.config.advanced;

import java.io.File;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import me.lauriichan.minecraft.minestom.server.config.Configuration;
import me.lauriichan.minecraft.minestom.server.config.IConfigHandler;
import me.lauriichan.minecraft.minestom.server.resource.source.FileDataSource;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;
import me.lauriichan.minecraft.minestom.server.translation.config.basic.TranslationConfigHandler;

final class LanguageConfigHandler implements IConfigHandler {

    private static final Predicate<String> VALID_LANG_NAME = Pattern.compile("[a-z][a-z\\-]*").asMatchPredicate();

    static final LanguageConfigHandler LANGUAGE = new LanguageConfigHandler();

    @Override
    public void load(final Configuration configuration, final IDataSource source, final boolean onlyRaw) throws Exception {
        final File rootDir = (File) source.getSource();
        final File[] langFiles = rootDir.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
        if (langFiles == null || langFiles.length == 0) {
            return;
        }
        String langName;
        for (final File langFile : langFiles) {
            langName = extractName(langFile.getName());
            if (!VALID_LANG_NAME.test(langName)) {
                continue;
            }
            TranslationConfigHandler.TRANSLATION.load(configuration.getConfiguration(langName, true), new FileDataSource(langFile), onlyRaw);
        }
    }

    private String extractName(final String name) {
        final int index = name.lastIndexOf('.');
        return name.substring(0, index + 1);
    }

    @Override
    public void save(final Configuration configuration, final IDataSource source) throws Exception {
        final File rootDir = (File) source.getSource();
        for (final String key : configuration.keySet()) {
            TranslationConfigHandler.TRANSLATION.save(configuration.getConfiguration(key), new FileDataSource(new File(rootDir, key + ".json")));
        }
    }

}
