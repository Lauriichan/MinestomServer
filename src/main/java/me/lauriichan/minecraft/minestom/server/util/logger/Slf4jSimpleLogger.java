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
    public void setDebug(boolean debug) {}
    
    @Override
    public void setTracking(boolean tracking) {}

    @Override
    protected void info(String message) {
        delegate.info(message);
    }

    @Override
    protected void warning(String message) {
        delegate.warn(message);
    }

    @Override
    protected void error(String message) {
        delegate.error(message);
    }

    @Override
    protected void track(String message) {
        delegate.trace(message);
    }

    @Override
    protected void debug(String message) {
        delegate.debug(message);
    }

}
