package mobi.lab.scrolls;

/**
 * Class for easy scrolls:)<br/>
 * Default implementation is LogImplSysout.class <br/>
 * Allows to:<br/>
 * 1) Log on info, debug, warning and error levels.<br/>
 * 2) Control the log verbosity so that logs can be easily limited in the release builds<br/>
 * 3) Set a platform specific implementation.<br/>
 *
 * @author madis
 * @author ergo
 * @author harri
 */
public abstract class Log {

    /**
     * All level of log lines (from ERROR to VERBOSE) are logged to output
     */
    @SuppressWarnings("WeakerAccess")
    public static final int VERBOSITY_LOG_ALL = 6;

    /**
     * All level from ERROR to DEBUG logged to output
     */
    @SuppressWarnings("WeakerAccess")
    public static final int VERBOSITY_LOG_DEBUG = 5;

    /**
     * All level from ERROR to INFO logged to output
     */
    @SuppressWarnings("WeakerAccess")
    public static final int VERBOSITY_LOG_INFO = 4;

    /**
     * All level from ERROR to WARNINGS logged to output
     */
    @SuppressWarnings("WeakerAccess")
    public static final int VERBOSITY_LOG_WARNINGS = 3;

    /**
     * Only ERROR levels are logged
     */
    public static final int VERBOSITY_LOG_ERRORS = 2;

    /**
     * Only WTF levels are logged
     */
    @SuppressWarnings("WeakerAccess")
    public static final int VERBOSITY_LOG_WTF = 1;

    /**
     * Nothing is logged, everything is discarded
     */
    @SuppressWarnings("unused")
    public static final int VERBOSITY_LOG_NOTHING = 0;

    private static final String DEFAULT_TAG = "MobiLog";
    private static Class logImplementation = LogImplCat.class;
    private static int logVerbosity = VERBOSITY_LOG_ALL;
    private String tag = DEFAULT_TAG;
    private final Object writeLock = new Object();

    protected Log() {
    }

    protected Log(final String tag) {
        setTag(tag);
    }

    /**
     * Get log instance
     *
     * @param obj Object for the tag name
     * @return A new instance of the log implementation
     */
    public static Log getInstance(final Object obj) {
        if (obj == null) {
            throw new RuntimeException("Object given to getInstance() was null");
        }
        return getInstance(obj.getClass());
    }

    /**
     * Get log instance
     *
     * @param clazz Name of this class used as the log tag
     * @return A new instance of the log implementation
     */
    public static Log getInstance(final Class clazz) {
        if (clazz == null) {
            throw new RuntimeException("Class given to getInstance() was null");
        }
        return getInstance(clazz.getName());
    }

    /**
     * Get log instance
     *
     * @param tag Log tag to use
     * @return A new instance of the log implementation
     */
    public static Log getInstance(final String tag) {
        try {
            final Log log = (Log) logImplementation.newInstance();
            log.setTag(tag);
            return log;
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        }
    }

    /**
     * Set a custom log implementation.
     *
     * @param clazz Implementation class to use
     */
    public static void setImplementation(final Class clazz) {
        if (clazz == null) {
            logImplementation = LogImplCat.class;
            return;
        }
        logImplementation = clazz;
    }

    /**
     * Set the verbosity level for the log<br/>
     * VERBOSITY_LOG_ALL - log everything<br/>
     * VERBOSITY_LOG_ERRORS - log only error level stuff<br/>
     * VERBOSITY_LOG_NOTHING - turn off logs<br/>
     *
     * @param verbosity verbosity level
     */
    public static void setVerbosity(final int verbosity) {
        logVerbosity = verbosity;
    }

    @SuppressWarnings("WeakerAccess")
    protected final void setTag(final String tag) {
        this.tag = tag;
    }

    /**
     * Log to wtf level
     *
     * @param o Log content
     */
    public final void wtf(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_WTF) {
            return;
        }

        synchronized (writeLock) {
            wtf(tag, o == null ? "null" : o.toString());
        }
    }

    /**
     * Log to wtf level
     *
     * @param o  Log content
     * @param tr Throwable to log
     */
    public final void wtf(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_WTF) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                wtf(tag, o == null ? "null" : o.toString(), tr);
            } else {
                wtf(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to wtf level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    @SuppressWarnings("unused")
    public final void wtf(final String format, final Object... args) {
        wtf(format(format, args));
    }

    /**
     * Log to wtf level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    @SuppressWarnings("unused")
    public final void wtf(final Throwable tr, final String format, final Object... args) {
        wtf(tr, format(format, args));
    }

    /**
     * Log to error level
     *
     * @param o Log content
     */
    public final void e(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_ERRORS) {
            return;
        }

        synchronized (writeLock) {
            error(tag, o == null ? "null" : o.toString());
        }
    }

    /**
     * Log to error level
     *
     * @param o  Log content
     * @param tr Throwable to log
     */
    public final void e(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_ERRORS) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                error(tag, o == null ? "null" : o.toString(), tr);
            } else {
                error(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to error level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    public final void e(final String format, final Object... args) {
        e(format(format, args));
    }

    /**
     * Log to error level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    public final void e(final Throwable tr, final String format, final Object... args) {
        e(tr, format(format, args));
    }

    /**
     * Log to warning level
     *
     * @param o Log content
     */
    public final void w(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_WARNINGS) {
            return;
        }

        synchronized (writeLock) {
            warning(tag, o == null ? "null" : o.toString());
        }
    }

    /**
     * Log to warning level
     *
     * @param o Log content
     */
    @SuppressWarnings("WeakerAccess")
    public final void w(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_WARNINGS) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                warning(tag, o == null ? "null" : o.toString(), tr);
            } else {
                warning(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to warning level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    @SuppressWarnings("unused")
    public final void w(final String format, final Object... args) {
        w(format(format, args));
    }

    /**
     * Log to warning level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    @SuppressWarnings("unused")
    public final void w(final Throwable tr, final String format, final Object... args) {
        w(tr, format(format, args));
    }

    /**
     * Log to debug level
     *
     * @param o Log content
     */
    public final void d(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_DEBUG) {
            return;
        }

        synchronized (writeLock) {
            debug(tag, o == null ? "null" : o.toString());
        }
    }


    /**
     * Log to debug level
     *
     * @param o Log content
     */
    @SuppressWarnings("WeakerAccess")
    public final void d(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_DEBUG) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                debug(tag, o == null ? "null" : o.toString(), tr);
            } else {
                debug(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to debug level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    @SuppressWarnings("unused")
    public final void d(final String format, final Object... args) {
        d(format(format, args));
    }

    /**
     * Log to debug level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    @SuppressWarnings("unused")
    public final void d(final Throwable tr, final String format, final Object... args) {
        d(tr, format(format, args));
    }

    /**
     * Log to info level
     *
     * @param o Log content
     */
    public final void i(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_INFO) {
            return;
        }

        synchronized (writeLock) {
            info(tag, o == null ? "null" : o.toString());
        }
    }

    /**
     * Log to info level
     *
     * @param o Log content
     */
    public final void i(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_INFO) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                info(tag, o == null ? "null" : o.toString(), tr);
            } else {
                info(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to info level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    public final void i(final String format, final Object... args) {
        i(format(format, args));
    }

    /**
     * Log to info level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    public final void i(final Throwable tr, final String format, final Object... args) {
        i(tr, format(format, args));
    }

    /**
     * Log to verbose level
     *
     * @param o Log content
     */
    public final void v(final Object o) {
        if (logVerbosity < VERBOSITY_LOG_ALL) {
            return;
        }

        synchronized (writeLock) {
            verbose(tag, o == null ? "null" : o.toString());
        }
    }

    /**
     * Log to verbose level
     *
     * @param o Log content
     */
    @SuppressWarnings("WeakerAccess")
    public final void v(final Throwable tr, final Object o) {
        if (logVerbosity < VERBOSITY_LOG_ALL) {
            return;
        }

        synchronized (writeLock) {
            if (tr != null) {
                verbose(tag, o == null ? "null" : o.toString(), tr);
            } else {
                verbose(tag, o == null ? "null" : o.toString());
            }
        }
    }

    /**
     * Log to verbose level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     */
    @SuppressWarnings("unused")
    public final void v(final String format, final Object... args) {
        v(format(format, args));
    }

    /**
     * Log to verbose level using String formatting
     *
     * @param format Log content
     * @param args   Log content arguments
     * @param tr     Throwable to log
     */
    @SuppressWarnings("unused")
    public final void v(final Throwable tr, final String format, final Object... args) {
        v(tr, format(format, args));
    }

    /**
     * Get the Scrolls library version.
     *
     * @return Scrolls library version
     */
    public static String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private String format(final String format, final Object... args) {
        if (args == null || args.length == 0) {
            return format;
        }
        return String.format(format, args);
    }

    /**
     * Override for scrolls to wtf level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void wtf(String tag, String msg);

    /**
     * Override for scrolls to wtf level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void wtf(String tag, String msg, Throwable tr);

    /**
     * Override for scrolls to error level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void error(String tag, String msg);

    /**
     * Override for scrolls to error level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void error(String tag, String msg, Throwable tr);

    /**
     * Override for scrolls to debug level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void debug(String tag, String msg);

    /**
     * Override for scrolls to debug level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void debug(String tag, String msg, Throwable tr);


    /**
     * Override for scrolls to warning level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void warning(String tag, String msg);

    /**
     * Override for scrolls to warning level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void warning(String tag, String msg, Throwable tr);


    /**
     * Override for scrolls to info level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void info(String tag, String msg);

    /**
     * Override for scrolls to info level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void info(String tag, String msg, Throwable tr);

    /**
     * Override for scrolls to verbose level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     */
    abstract protected void verbose(String tag, String msg);

    /**
     * Override for scrolls to verbose level.
     *
     * @param tag Tag of log line
     * @param msg Content of log line
     * @param tr  Throwable to log
     */
    abstract protected void verbose(String tag, String msg, Throwable tr);
}
