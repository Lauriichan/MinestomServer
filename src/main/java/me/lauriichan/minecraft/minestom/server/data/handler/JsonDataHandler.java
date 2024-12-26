package me.lauriichan.minecraft.minestom.server.data.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.io.JsonParser;
import me.lauriichan.laylib.json.io.JsonWriter;
import me.lauriichan.minecraft.minestom.server.config.handler.JsonConfigHandler;
import me.lauriichan.minecraft.minestom.server.data.IDataHandler;
import me.lauriichan.minecraft.minestom.server.resource.source.IDataSource;

public final class JsonDataHandler implements IDataHandler<IJson<?>> {

    public static final JsonWriter WRITER = JsonConfigHandler.WRITER;

    public static final JsonDataHandler JSON_DEFAULT = new JsonDataHandler("data");

    public static JsonDataHandler forKey(final String key) {
        return new JsonDataHandler(key);
    }

    private final String key;

    private JsonDataHandler(final String key) {
        this.key = key;
    }

    @Override
    public void load(final Wrapper<IJson<?>> wrapper, final IDataSource source) throws Exception {
        IJson<?> element;
        try (BufferedReader reader = source.openReader()) {
            element = JsonParser.fromReader(reader);
        }
        if (!element.isObject()) {
            throw new IllegalStateException("Data source doesn't contain a JsonObject");
        }
        final JsonObject object = element.asJsonObject();
        wrapper.version(object.getAsInt("version", 0));
        wrapper.value(object.get(key));
    }

    @Override
    public void save(final Wrapper<IJson<?>> wrapper, final IDataSource source) throws Exception {
        final JsonObject root = new JsonObject();
        root.put("version", wrapper.version());
        if (wrapper.value() != null) {
            root.put(key, wrapper.value());
        }
        try (BufferedWriter writer = source.openWriter()) {
            WRITER.toWriter(root, writer);
        }
    }

}
