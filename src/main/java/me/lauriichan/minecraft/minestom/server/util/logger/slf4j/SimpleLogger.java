package me.lauriichan.minecraft.minestom.server.util.logger.slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

final class SimpleLogger implements ILoggerAdapter {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd/HH:mm:ss.SSS");

    private final String name;

    private final Consumer<String> println;

    SimpleLogger(String name, Consumer<String> println) {
        this.name = name;
        this.println = println;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void log(Slf4JEntry entry) {
        if (entry.message().indexOf('\n') == -1) {
            println(entry.type(), entry.message());
            if (entry.throwable() != null) {
                println(entry.type(), Helper.stackTraceToString(entry.throwable()).split("\n"));
            }
            return;
        }
        println(entry.type(), entry.message().split("\n"));
        if (entry.throwable() != null) {
            println(entry.type(), Helper.stackTraceToString(entry.throwable()).split("\n"));
        }
    }

    private void println(LogType type, String[] message) {
        String messagePrefix = new StringBuilder().append('[').append(TIME_FORMATTER.format(LocalDateTime.now())).append("][")
            .append(Thread.currentThread().getName()).append('/').append(type.name()).append("][").append(name).append("]: ").toString();
        for (String line : message) {
            println.accept(messagePrefix.concat(line));
        }
    }

    private void println(LogType type, String message) {
        println.accept(new StringBuilder().append('[').append(TIME_FORMATTER.format(LocalDateTime.now())).append("][")
            .append(Thread.currentThread().getName()).append('/').append(type.name()).append("][").append(name).append("]: ")
            .append(message).toString());
    }

}
