package me.lauriichan.minecraft.minestom.server.util.logger.slf4j;

import org.slf4j.Marker;

import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.minecraft.minestom.server.MinestomArguments;

public interface ILoggerAdapter extends org.slf4j.Logger {
    
    static enum LogType {
        INFO(0), WARN(1), ERROR(2), DEBUG(3), TRACE(4);
        
        private final int level;
        
        private LogType(int level) {
            this.level = level;
        }
        
        public int level() {
            return level;
        }
        
        public boolean isEnabled(LogType type) {
            return level >= type.level;
        }
    }
    static final record Slf4JEntry(LogType type, String message, Throwable throwable) {}
    
    public void log(Slf4JEntry entry);

    /*
     * Info
     */
    
    default LogType allowedLevel() {
        return MinestomArguments.LOGGER_LEVEL.value();
    }

    @Override
    public default boolean isInfoEnabled() {
        return true;
    }

    @Override
    public default boolean isInfoEnabled(final Marker marker) {
        return true;
    }

    @Override
    public default void info(String msg) {
        log(new Slf4JEntry(LogType.INFO, msg, null));
    }

    @Override
    public default void info(final String format, final Object arg) {
        info(StringUtil.format(format, new Object[] {arg}));
    }

    @Override
    public default void info(final String format, final Object arg1, final Object arg2) {
        info(StringUtil.format(format, new Object[] {arg1, arg2}));
    }

    @Override
    public default void info(final String format, final Object... arguments) {
        info(StringUtil.format(format, arguments));
    }

    @Override
    public default void info(final String msg, final Throwable t) {
        log(new Slf4JEntry(LogType.INFO, msg, t));
    }

    @Override
    public default void info(final Marker marker, final String msg) {
        info(msg);
    }

    @Override
    public default void info(final Marker marker, final String format, final Object arg) {
        info(format, arg);
    }

    @Override
    public default void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public default void info(final Marker marker, final String format, final Object... arguments) {
        info(format, arguments);
    }

    @Override
    public default void info(final Marker marker, final String msg, final Throwable t) {
        info(msg, t);
    }

    /*
     * Warn
     */

    @Override
    public default boolean isWarnEnabled() {
        return allowedLevel().isEnabled(LogType.WARN);
    }

    @Override
    public default boolean isWarnEnabled(final Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public default void warn(String msg) {
        if (!isWarnEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.WARN, msg, null));
    }

    @Override
    public default void warn(final String format, final Object arg) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(StringUtil.format(format, new Object[] {arg}));
    }

    @Override
    public default void warn(final String format, final Object... arguments) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(StringUtil.format(format, arguments));
    }

    @Override
    public default void warn(final String format, final Object arg1, final Object arg2) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(StringUtil.format(format, new Object[] {arg1, arg2}));
    }

    @Override
    public default void warn(final String msg, final Throwable t) {
        if (!isWarnEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.WARN, msg, t));
    }

    @Override
    public default void warn(final Marker marker, final String msg) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(msg);
    }

    @Override
    public default void warn(final Marker marker, final String format, final Object arg) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(format, arg);
    }

    @Override
    public default void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(format, arg1, arg2);
    }

    @Override
    public default void warn(final Marker marker, final String format, final Object... arguments) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(format, arguments);
    }

    @Override
    public default void warn(final Marker marker, final String msg, final Throwable t) {
        if (!isWarnEnabled()) {
            return;
        }
        warn(msg, t);
    }

    /*
     * Error
     */

    @Override
    public default boolean isErrorEnabled() {
        return allowedLevel().isEnabled(LogType.ERROR);
    }

    @Override
    public default boolean isErrorEnabled(final Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public default void error(String msg) {
        if (!isErrorEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.ERROR, msg, null));
    }

    @Override
    public default void error(final String format, final Object arg) {
        if (!isErrorEnabled()) {
            return;
        }
        error(StringUtil.format(format, new Object[]{arg}));
    }

    @Override
    public default void error(final String format, final Object arg1, final Object arg2) {
        if (!isErrorEnabled()) {
            return;
        }
        error(StringUtil.format(format, new Object[] {arg1, arg2}));
    }

    @Override
    public default void error(final String format, final Object... arguments) {
        if (!isErrorEnabled()) {
            return;
        }
        error(StringUtil.format(format, arguments));
    }

    @Override
    public default void error(final String msg, final Throwable t) {
        if (!isErrorEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.ERROR, msg, t));
    }

    @Override
    public default void error(final Marker marker, final String msg) {
        if (!isErrorEnabled()) {
            return;
        }
        error(msg);
    }

    @Override
    public default void error(final Marker marker, final String format, final Object arg) {
        if (!isErrorEnabled()) {
            return;
        }
        error(format, arg);
    }

    @Override
    public default void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        if (!isErrorEnabled()) {
            return;
        }
        error(format, arg1, arg2);
    }

    @Override
    public default void error(final Marker marker, final String format, final Object... arguments) {
        if (!isErrorEnabled()) {
            return;
        }
        error(format, arguments);
    }

    @Override
    public default void error(final Marker marker, final String msg, final Throwable t) {
        if (!isErrorEnabled()) {
            return;
        }
        error(msg, t);
    }

    /*
     * Trace
     */

    @Override
    public default boolean isTraceEnabled() {
        return allowedLevel().isEnabled(LogType.TRACE);
    }

    @Override
    public default boolean isTraceEnabled(final Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public default void trace(String msg) {
        if (!isTraceEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.ERROR, msg, null));
    }

    @Override
    public default void trace(final String format, final Object arg) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(StringUtil.format(format, new Object[]{arg}));
    }

    @Override
    public default void trace(final String format, final Object arg1, final Object arg2) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(StringUtil.format(format, new Object[] {arg1, arg2}));
    }

    @Override
    public default void trace(final String format, final Object... arguments) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(StringUtil.format(format, arguments));
    }

    @Override
    public default void trace(final String msg, final Throwable t) {
        if (!isTraceEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.ERROR, msg, t));
    }

    @Override
    public default void trace(final Marker marker, final String msg) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(msg);
    }

    @Override
    public default void trace(final Marker marker, final String format, final Object arg) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(format, arg);
    }

    @Override
    public default void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(format, arg1, arg2);
    }

    @Override
    public default void trace(final Marker marker, final String format, final Object... argArray) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(format, argArray);
    }

    @Override
    public default void trace(final Marker marker, final String msg, final Throwable t) {
        if (!isTraceEnabled()) {
            return;
        }
        trace(msg, t);
    }

    /*
     * Debug
     */

    @Override
    public default boolean isDebugEnabled() {
        return allowedLevel().isEnabled(LogType.DEBUG);
    }

    @Override
    public default boolean isDebugEnabled(final Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public default void debug(String msg) {
        if (!isDebugEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.DEBUG, msg, null));
    }

    @Override
    public default void debug(final String format, final Object arg) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(StringUtil.format(format, new Object[]{arg}));
    }

    @Override
    public default void debug(final String format, final Object arg1, final Object arg2) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(StringUtil.format(format, new Object[] {arg1, arg2}));
    }

    @Override
    public default void debug(final String format, final Object... arguments) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(StringUtil.format(format, arguments));
    }

    @Override
    public default void debug(final String msg, final Throwable t) {
        if (!isDebugEnabled()) {
            return;
        }
        log(new Slf4JEntry(LogType.DEBUG, msg, t));
    }

    @Override
    public default void debug(final Marker marker, final String msg) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(msg);
    }

    @Override
    public default void debug(final Marker marker, final String format, final Object arg) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(format, arg);
    }

    @Override
    public default void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(format, arg1, arg2);
    }

    @Override
    public default void debug(final Marker marker, final String format, final Object... arguments) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(format, arguments);
    }

    @Override
    public default void debug(final Marker marker, final String msg, final Throwable t) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(msg, t);
    }

}
