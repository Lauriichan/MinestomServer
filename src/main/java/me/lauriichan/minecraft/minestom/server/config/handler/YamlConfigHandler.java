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
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setCodePointLimit(Integer.MAX_VALUE);
        loaderOptions.setProcessComments(false);
        
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(2);
        dumperOptions.setWidth(80);
        loaderOptions.setProcessComments(false);

        this.yaml = new Yaml(new SafeConstructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions);
    }

    @Override
    public void load(Configuration configuration, IDataSource source, boolean onlyRaw) throws Exception {
        Map<String, Object> map;
        try (BufferedReader reader = source.openReader()) {
            map = yaml.load(reader);
        }
        loadConfigFromMap(configuration, map);
    }
    
    @SuppressWarnings("unchecked")
    private void loadConfigFromMap(Configuration config, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> otherMap) {
                loadConfigFromMap(config.getConfiguration(key, true), (Map<String, Object>) otherMap);
                continue;
            }
            config.set(key, value);
        }
    }

    @Override
    public void save(Configuration configuration, IDataSource source) throws Exception {
        Map<String, Object> map = createMapFromConfig(configuration);
        try (BufferedWriter writer = source.openWriter()) {
            yaml.dump(map, writer);
        }
    }
    
    private Map<String, Object> createMapFromConfig(Configuration config) {
        Object2ObjectArrayMap<String, Object> map = new Object2ObjectArrayMap<>();
        for (String key : config.keySet()) {
            if (config.isConfiguration(key)) {
                map.put(key, createMapFromConfig(config.getConfiguration(key)));
                continue;
            }
            map.put(key, config.get(key));
        }
        return map;
    }

}
