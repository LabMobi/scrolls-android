package mobi.lab.scrolls;

/**
 * Log wrapper that allows to use more than one log implementation at a time.<br>
 * Specify the actual implementations used in the init() method.<br>
 * <p>
 * NB: Must call init() before creating an instance or a RuntimeException will be thrown<br>
 *
 * @author harri
 */
public class LogImplComposite extends Log {
    private static Log[] logImpls;
    private static boolean isInitDone;

    /**
     * Default constructor, use Log.setImplementation(LogImplComposite.class) followed by Log.getInstance(); or LogImplComposite(final String tag) instead<br>
     * NB: You need to call the init() method first.
     */
    public LogImplComposite() {
        super();
        if (!isInitDone) {
            throw (new RuntimeException("You forgot to call the LogImplComposite class init() method before creating an instance"));
        }
    }

    /**
     * Preferred constructor to use in case you need to manually create an instance of this class. Prefer to use Log.setImplementation(LogImplComposite.class) followed by Log.getInstance()<br>
     * NB: You need to call the init() method first.
     */
    public LogImplComposite(final String tag) {
        super(tag);
        if (!isInitDone) {
            throw (new RuntimeException("You forgot to call the LogImplComposite class init() method before creating an instance"));
        }
    }

    /**
     * Init the composite logs by giving an non-empty array of log implementations to use.<br>
     * NB: Instances are created from these classes and you should call init() on them first if needed.
     *
     * @param logClasses Array of log implementations to use
     */
    public static void init(final Class[] logClasses) {
        isInitDone = false;
        logImpls = new Log[logClasses.length];
        for (int i = 0; i < logClasses.length; i++) {
            try {
                logImpls[i] = (Log) logClasses[i].newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e.getClass() + " " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getClass() + " " + e.getMessage());
            } catch (ClassCastException e) {
                throw new RuntimeException(e.getClass() + " " + e.getMessage());
            }
        }
        isInitDone = true;
    }

    /**
     * Is init() called yet or not
     *
     * @return
     */
    public static boolean isInitDone() {
        return isInitDone;
    }

    protected void error(final String tag, final String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].error(tag, msg);
            }
        }
    }

    protected void error(final String tag, final String msg, final Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].error(tag, msg, tr);
            }
        }
    }

    protected void debug(final String tag, final String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].debug(tag, msg);
            }
        }
    }

    @Override
    protected void debug(String tag, String msg, Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].debug(tag, msg, tr);
            }
        }
    }

    protected void warning(final String tag, final String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].warning(tag, msg);
            }
        }
    }

    protected void warning(final String tag, final String msg, final Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].warning(tag, msg, tr);
            }
        }
    }

    protected void info(final String tag, final String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].info(tag, msg);
            }
        }
    }

    @Override
    protected void info(String tag, String msg, Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].info(tag, msg, tr);
            }
        }
    }

    @Override
    protected void wtf(String tag, String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].wtf(tag, msg);
            }
        }
    }

    @Override
    protected void wtf(String tag, String msg, Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].wtf(tag, msg, tr);
            }
        }
    }

    @Override
    protected void verbose(String tag, String msg) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].verbose(tag, msg);
            }
        }
    }

    @Override
    protected void verbose(String tag, String msg, Throwable tr) {
        if (logImpls == null || logImpls.length == 0) {
            return;
        }
        for (int i = 0; i < logImpls.length; i++) {
            if (logImpls[i] != null) {
                logImpls[i].verbose(tag, msg, tr);
            }
        }
    }
}
