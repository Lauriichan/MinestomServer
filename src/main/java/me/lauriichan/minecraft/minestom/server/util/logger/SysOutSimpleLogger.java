package me.lauriichan.minecraft.minestom.server.util.logger;

import me.lauriichan.laylib.logger.AbstractSimpleLogger;

public final class SysOutSimpleLogger extends AbstractSimpleLogger {

    public static SysOutSimpleLogger INSTANCE = new SysOutSimpleLogger();

    private SysOutSimpleLogger() {}

    @Override
    protected void info(final String message) {
        System.out.println(message);
    }

    @Override
    protected void warning(final String message) {
        System.out.println(message);
    }

    @Override
    protected void debug(final String message) {
        System.out.println(message);
    }

    @Override
    protected void error(final String message) {
        System.err.println(message);
    }

    @Override
    protected void track(final String message) {
        System.err.println(message);
    }

}
