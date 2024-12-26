package me.lauriichan.minecraft.minestom.server.config.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.lauriichan.minecraft.minestom.server.config.Configuration;
import me.lauriichan.minecraft.minestom.server.config.IConfigHandler;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;

public final class YamlConfigHandler implements IConfigHandler {

    public static final YamlConfigHandler YAML = new YamlConfigHandler();

    private final Yaml yaml;

    private YamlConfigHandler() {
        final LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setCodePointLimit(Integer.MAX_VALUE);
        loaderOptions.setProcessComments(false);

        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(2);
        dumperOptions.setWidth(80);
        loaderOptions.setProcessComments(false);

        this.yaml = new Yaml(new SafeConstructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions);
    }

    @Override
    public void load(final Configuration configuration, final IDataSource source, final boolean onlyRaw) throws Exception {
        Map<String, Object> map;
        try (BufferedReader reader = source.openReader()) {
            map = yaml.load(reader);
        }
        loadConfigFromMap(configuration, map);
    }

    @SuppressWarnings("unchecked")
    private void loadConfigFromMap(final Configuration config, final Map<String, Object> map) {
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof final Map<?, ?> otherMap) {
                loadConfigFromMap(config.getConfiguration(key, true), (Map<String, Object>) otherMap);
                continue;
            }
            config.set(key, value);
        }
    }

    @Override
    public void save(final Configuration configuration, final IDataSource source) throws Exception {
        final Map<String, Object> map = createMapFromConfig(configuration);
        try (BufferedWriter writer = source.openWriter()) {
            yaml.dump(map, writer);
        }
    }

    private Map<String, Object> createMapFromConfig(final Configuration config) {
        final Object2ObjectArrayMap<String, Object> map = new Object2ObjectArrayMap<>();
        for (final String key : config.keySet()) {
            if (config.isConfiguration(key)) {
                map.put(key, createMapFromConfig(config.getConfiguration(key)));
                continue;
            }
            map.put(key, config.get(key));
        }
        return map;
    }

}
