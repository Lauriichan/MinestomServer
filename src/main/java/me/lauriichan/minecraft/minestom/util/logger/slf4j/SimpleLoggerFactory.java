package me.lauriichan.minecraft.minestom.util.logger.slf4j;

import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import me.lauriichan.minecraft.minestom.MinestomArguments;

final class SimpleLoggerFactory implements ILoggerFactory {

    private final Object2ObjectMap<String, SimpleLogger> loggers = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());
    private final Function<String, SimpleLogger> func;

    public SimpleLoggerFactory(LogCache log) {
        Consumer<String> println = MinestomArguments.LOG_TO_CONSOLE.value() ? msg -> {
            synchronized (log) {
                log.println(msg);
            }
        } : msg -> {
            synchronized (log) {
                System.out.println(msg);
                log.println(msg);
            }
        };
        func = name -> new SimpleLogger(name, println);
    }

    @Override
    public Logger getLogger(final String name) {
        return loggers.computeIfAbsent(name, func);
    }

}
