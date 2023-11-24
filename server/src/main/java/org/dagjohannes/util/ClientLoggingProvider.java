package org.dagjohannes.util;

import org.tinylog.Level;
import org.tinylog.core.TinylogLoggingProvider;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.NopContextProvider;


/**
 * Interfaces with LSP for logTrace notifications.
 * ERROR, WARN are sent as logTrace
 * INFO, DEBUG, TRACE are sent as logTrace (with verbose)
 * <p>
 * Currently, we're just printing instead of sending window/logTrace messages, are there any drawbacks to this?
 * TODO possibly interface with logMessage, to show popups?
 */
public class ClientLoggingProvider extends TinylogLoggingProvider {
    private LogLevel l;

    public ClientLoggingProvider() {
        this.l = LogLevel.MESSAGES; // set level at instantiation
    }

    public void setLogLevel(String logLevel) {
        this.l = switch (logLevel) {
            case "off" -> LogLevel.OFF;
            case "messages" -> LogLevel.MESSAGES;
            case "verbose" -> LogLevel.VERBOSE;
            default -> throw new IllegalStateException("Unexpected value: " + logLevel);
        };
    }

    @Override
    public ContextProvider getContextProvider() {
        return new NopContextProvider();
    }

    @Override
    public Level getMinimumLevel() {
        return Level.TRACE;
    }

    @Override
    public Level getMinimumLevel(String tag) {
        return Level.TRACE;
    }

    @Override
    public boolean isEnabled(int depth, String tag, Level level) {
        // Level severity order: Trace -> Debug -> Info -> Warn -> Error
        return switch (l) {
            case OFF -> false;
            case MESSAGES -> level.ordinal() >= Level.WARN.ordinal();
            case VERBOSE -> true;
        };
    }

    @Override
    public void log(int depth, String tag, Level level, Throwable exception, MessageFormatter formatter, Object obj, Object... arguments) {
        if (isEnabled(depth, tag, level)) {
            var e = this.l.compareTo(LogLevel.VERBOSE) == 0 ? exception : null;
            super.log(depth + 2, tag, level, e, formatter, obj, arguments);
        }
    }

    @Override
    public void log(String loggerClassName, String tag, Level level, Throwable exception, MessageFormatter formatter, Object obj, Object... arguments) {
        if (isEnabled(0, tag, level)) {
            var e = this.l.compareTo(LogLevel.VERBOSE) == 0 ? exception : null;
            super.log(loggerClassName, tag, level, e, formatter, obj, arguments);
        }
    }

    public enum LogLevel {OFF, MESSAGES, VERBOSE}
}
