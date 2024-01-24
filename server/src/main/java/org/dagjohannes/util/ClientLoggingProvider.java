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
        LogLevel l;
        switch (logLevel) {
            case "off": l = LogLevel.OFF; break;
            case "messages": l = LogLevel.MESSAGES; break;
            case "verbose": l = LogLevel.VERBOSE; break;
            default: throw new IllegalStateException("Unexpected value: " + logLevel);
        };
        this.l = l;
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
        if (l.equals(LogLevel.OFF)) {
            return false;
        } else if (l.equals(LogLevel.MESSAGES)) {
            return level.ordinal() >= Level.WARN.ordinal();
        } else { // LogLevel must be verbose
            return true;
        }
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
