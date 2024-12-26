package me.lauriichan.minecraft.minestom.server.util.logger;

import org.slf4j.Logger;

import me.lauriichan.laylib.logger.AbstractSimpleLogger;

public final class Slf4jSimpleLogger extends AbstractSimpleLogger {

    private final Logger delegate;

    public Slf4jSimpleLogger(final Logger delegate) {
        this.delegate = delegate;
        super.setDebug(delegate.isDebugEnabled());
        super.setTracking(delegate.isTraceEnabled());
    }

    @Override
    public void setDebug(final boolean debug) {}

    @Override
    public void setTracking(final boolean tracking) {}

    @Override
    protected void info(final String message) {
        delegate.info(message);
    }

    @Override
    protected void warning(final String message) {
        delegate.warn(message);
    }

    @Override
    protected void error(final String message) {
        delegate.error(message);
    }

    @Override
    protected void track(final String message) {
        delegate.trace(message);
    }

    @Override
    protected void debug(final String message) {
        delegate.debug(message);
    }

}
