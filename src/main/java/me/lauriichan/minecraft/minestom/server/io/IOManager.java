package me.lauriichan.minecraft.minestom.server.io;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.lauriichan.minecraft.minestom.server.io.serialization.SerializationException;
import me.lauriichan.minecraft.minestom.server.io.serialization.SerializationHandler;
import me.lauriichan.minecraft.minestom.server.module.SystemModule;

public final class IOManager {
    
    // TODO: Add data handlers

    private final Object2ObjectMap<Class<?>, Object2ObjectMap<Class<?>, IIOHandler<?, ?>>> handlers = new Object2ObjectOpenHashMap<>();

    public IOManager(SystemModule module) {
        module.extension(IIOHandler.class, true).callInstances((mod, handler) -> {
            Class<?> next = handler.getClass().getSuperclass();
            while (next != null && next.getAnnotation(HandlerPoint.class) == null) {
                next = next.getSuperclass();
            }
            if (next == null) {
                mod.logger().error("Couldn't load handler '{0}' as it has no valid handler point as super class.",
                    handler.getClass().getName());
                return;
            }
            Object2ObjectMap<Class<?>, IIOHandler<?, ?>> map = handlers.get(next);
            if (map == null) {
                map = new Object2ObjectArrayMap<>();
                handlers.put(next, map);
            } else if (map.containsKey(handler.valueType())) {
                mod.logger().error("There is already a handler of type '{0}' for value type '{1}'.", next.getName(),
                    handler.valueType().getName());
                return;
            }
            map.put(handler.valueType(), handler);
        });
    }

    public <B, H extends SerializationHandler<B, ?>> B serialize(Class<H> handlerType, Object data) throws SerializationException {
        if (data == null) {
            return null;
        }
        Object2ObjectMap<Class<?>, IIOHandler<?, ?>> map = handlers.get(handlerType);
        if (map == null) {
            throw new SerializationException("Unknown handler type '" + handlerType.getName() + "'");
        }
        Class<?> dataClass = data.getClass();
        IIOHandler<?, ?> handler = map.get(dataClass);
        if (handler == null) {
            handler = map.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(dataClass)).findFirst()
                .map(entry -> entry.getValue()).orElse(null);
            if (handler == null) {
                throw new SerializationException("Failed to find handler of type '" + handlerType.getName()
                    + "' in order to serialize data of type '" + data.getClass().getName() + "'.");
            }
        }
        SerializationHandler<B, ?> serialHandler;
        try {
            serialHandler = handlerType.cast(handler);
        } catch (ClassCastException e) {
            throw new SerializationException("Failed to find handler of type '" + handlerType.getName()
                + "' in order to serialize data of type '" + data.getClass().getName() + "'.", e);
        }
        return serialHandler.serializeAny(data);
    }

    public <B, H extends SerializationHandler<B, ?>, V> V deserialize(Class<H> handlerType, B buffer, Class<V> valueType)
        throws SerializationException {
        if (buffer == null) {
            return null;
        }
        Object2ObjectMap<Class<?>, IIOHandler<?, ?>> map = handlers.get(handlerType);
        if (map == null) {
            throw new SerializationException("Unknown handler type '" + handlerType.getName() + "'");
        }
        IIOHandler<?, ?> handler = map.get(valueType);
        if (handler == null) {
            handler = map.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(valueType)).findFirst()
                .map(entry -> entry.getValue()).orElse(null);
            if (handler == null) {
                throw new SerializationException("Failed to find handler of type '" + handlerType.getName()
                    + "' in order to serialize data of type '" + valueType.getName() + "'.");
            }
        }
        SerializationHandler<B, ?> serialHandler;
        try {
            serialHandler = handlerType.cast(handler);
        } catch (ClassCastException e) {
            throw new SerializationException("Failed to find handler of type '" + handlerType.getName()
                + "' in order to serialize data of type '" + valueType.getName() + "'.", e);
        }
        if (!valueType.isAssignableFrom(serialHandler.valueType())) {
            throw new SerializationException("Failed to find handler of type '" + handlerType.getName()
                + "' in order to serialize data of type '" + valueType.getName() + "'.");
        }
        return valueType.cast(serialHandler.deserialize(buffer));
    }

}
