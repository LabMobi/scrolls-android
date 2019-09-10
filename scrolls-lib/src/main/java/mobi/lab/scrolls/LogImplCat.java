package mobi.lab.scrolls;

import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple Log implementation that logs to Android LogCat.<br>
 * Doesn't need init().
 *
 * @author harri
 */
public class LogImplCat extends Log {
    public static final int LOG_LINE_LENGTH_LIMIT = 4000;

    public static boolean tagOnlyClass = true;

    /**
     * Default constructor, use Log.setImplementation(LogImplCat.class) followed by Log.getInstance(); or LogImplCat(final String tag) instead<br>
     */
    public LogImplCat() {
        super();
    }

    /**
     * Preferred constructor to use in case you need to manually create an instance of this class. Prefer to use Log.setImplementation(LogImplCat.class) followed by Log.getInstance()<br>
     */
    public LogImplCat(final String tag) {
        super(tag);
    }

    /**
     * If true, only classname will be used as tag. Otherwise, the full name is used.
     *
     * @param tagOnlyClass
     */
    public static void setTagOnlyClass(boolean tagOnlyClass) {
        LogImplCat.tagOnlyClass = tagOnlyClass;
    }

    @Override
    protected void error(final String tag, final String msg) {
        log(android.util.Log.ERROR, tag, msg, null);
    }

    @Override
    protected void error(final String tag, final String msg, final Throwable tr) {
        log(android.util.Log.ERROR, tag, msg, tr);
    }

    @Override
    protected void debug(final String tag, final String msg) {
        log(android.util.Log.DEBUG, tag, msg, null);
    }

    @Override
    protected void debug(String tag, String msg, Throwable tr) {
        log(android.util.Log.DEBUG, tag, msg, tr);
    }

    @Override
    protected void warning(final String tag, final String msg) {
        log(android.util.Log.WARN, tag, msg, null);
    }

    @Override
    protected void warning(final String tag, final String msg, final Throwable tr) {
        log(android.util.Log.WARN, tag, msg, tr);
    }

    @Override
    protected void info(final String tag, final String msg) {
        log(android.util.Log.INFO, tag, msg, null);
    }

    @Override
    protected void info(String tag, String msg, Throwable tr) {
        log(android.util.Log.INFO, tag, msg, tr);
    }

    @Override
    protected void wtf(String tag, String msg) {
        log(android.util.Log.ASSERT, tag, msg, null);
    }

    @Override
    protected void wtf(String tag, String msg, Throwable tr) {
        log(android.util.Log.ASSERT, tag, msg, tr);
    }

    @Override
    protected void verbose(String tag, String msg) {
        log(android.util.Log.VERBOSE, tag, msg, null);
    }

    @Override
    protected void verbose(String tag, String msg, Throwable tr) {
        log(android.util.Log.VERBOSE, tag, msg, tr);
    }

    private String tag(String tag) {
        return LogImplCat.tagOnlyClass ? tag.substring(tag.lastIndexOf('.') + 1, tag.length()) : tag;
    }

    private void log(int level, String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg) && tr == null) {
            return; // Ignore
        }

        // Prep the log
        String message = null;
        if (TextUtils.isEmpty(msg)) {
            message = getStackTraceString(tr);
        } else if (tr == null) {
            message = msg;
        } else {
            message = msg + "\n" + getStackTraceString(tr);
        }

        if (TextUtils.isEmpty(message)) {
            return; // Ignore
        }

        // Print it so we limit it to MAX length
        if (message.length() < LOG_LINE_LENGTH_LIMIT) {
            android.util.Log.println(level, tag, message);
            return;
        }

        // Long line, lets break it up
        for (int i = 0; i < message.length(); i++) {
            // Find the end of the first line
            int lineBreak = message.indexOf('\n', i);
            lineBreak = lineBreak != -1 ? lineBreak : message.length();
            // lineBreak is either the end of the first line or end of the log (if no line breaks)
            do {
                // Log the first part so we never write more than LOG_LINE_LENGTH_LIMIT
                final int partEnd = Math.min(lineBreak, i + LOG_LINE_LENGTH_LIMIT);
                // partEnd is either the end of the line or the LOG_LINE_LENGTH_LIMIT (in that case we only log part of the line)
                android.util.Log.println(level, tag, message.substring(i, partEnd));
                i = partEnd;
                // Continue this if we have more parts that went over LOG_LINE_LENGTH_LIMIT before
            } while (i < lineBreak);
        }

    }

    private String getStackTraceString(final Throwable tr) {
        if (tr == null) {
            return null;
        }
        final StringWriter stringWriter = new StringWriter(256);
        final PrintWriter printWriter = new PrintWriter(stringWriter, false);
        tr.printStackTrace(printWriter);
        printWriter.flush();
        final String trace = stringWriter.toString();
        printWriter.close();
        return trace;
    }

}
