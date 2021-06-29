package mobi.lab.scrolls;

import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Log to a file on android private storage area.
 * You need to call init(File) (by giving a directory) to correctly start this implementation.
 * Log file name: [PREFIX]_Year_Month_Day_Hour_Minute_Second_Millisecond[EXTENSION_TEXT_LOG].
 *
 * @author Madis Pink, Harri Kirik
 */
public class LogImplFile extends Log {

    public static final char LEVEL_MARKER_ERROR = 'E';
    public static final char LEVEL_MARKER_WARNING = 'W';
    public static final char LEVEL_MARKER_DEBUG = 'D';
    public static final char LEVEL_MARKER_INFO = 'I';
    @SuppressWarnings("WeakerAccess")
    public static final char LEVEL_MARKER_WTF = 'A';
    @SuppressWarnings("WeakerAccess")
    public static final char LEVEL_MARKER_VERBOSE = 'V';

    private static final int MSG_LOG_DEBUG = 0;
    private static final int MSG_LOG_ERROR = 1;
    private static final int MSG_LOG_INFO = 2;
    private static final int MSG_LOG_HEADER = 3;
    private static final int MSG_LOG_WARNING = 4;
    private static final int MSG_LOG_VERBOSE = 5;
    private static final int MSG_LOG_WTF = 6;
    private static final int MSG_TASK_START_CLEANUP = 10;
    private static final int MSG_TASK_STOP = 11;
    private static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US);
    private static final SimpleDateFormat logLineDateFormat = new SimpleDateFormat("HH:mm:ss.SSSZZZZZ", Locale.US);
    private static final String PREFIX = "LOG_";
    private static final String EXTENSION_TEXT_LOG = ".txt";
    private static final String EXTENSION_COMPRESSED_TEXT_LOG = ".zip";
    private static FileOutputStream os;
    private static PrintStream ps;
    private static LogHandlerThread logHandlerThread;
    private static File path;
    private static String filename;
    private static boolean isInitDone;
    private static LogDelete cleaningStrategy;
    private static final Object initLock = new Object();

    /**
     * Default constructor, use Log.setImplementation(LogImplFile.class) followed by Log.getInstance(); or LogImplFile(final String tag) instead.
     * NB: You need to call the init() method first.
     */
    @SuppressWarnings("unused")
    public LogImplFile() {
        super();
        initGuard();
    }

    /**
     * Preferred constructor to use in case you need to manually create an instance of this class. Prefer to use Log.setImplementation(LogImplFile.class) followed by Log.getInstance().
     * NB: You need to call the init() method first.
     */
    @SuppressWarnings("unused")
    public LogImplFile(final String tag) {
        super(tag);
        initGuard();
    }

    private static void initGuard() throws IllegalStateException {
        if (!isInitDone) {
            throw (new IllegalStateException("You forgot to call the LogImplFile class init() method before creating an instance"));
        }
    }

    /**
     * Create the file and set up the logger thread.
     *
     * @param dir File object denoting a directory to write to log to
     */
    @SuppressWarnings("WeakerAccess")
    public static void init(@NonNull final File dir) {
        init(dir, null);
    }


    public static void init(@NonNull final File dir, @Nullable final LogDelete strategy) {
        synchronized (initLock) {
            isInitDone = false;
            path = dir;
            cleaningStrategy = strategy;
            try {
                // Start the HandlerThread
                logHandlerThread = new LogHandlerThread();
                // Open the FileOutputStream
                filename = PREFIX + fileNameDateFormat.format(Calendar.getInstance().getTime()) + EXTENSION_TEXT_LOG;
                os = new FileOutputStream(new File(path, filename));
                ps = new PrintStream(os, true);
                // Create a log file header
                logHandlerThread.sendMessage(MSG_LOG_HEADER, new LogMessage(null, "--- LOG device info: " + LogHelper.getDeviceInfo() + "; device time: " + LogHelper.getDateTimeString() + " ---\n"));
                os.flush();
                isInitDone = true;

                // If we have a strategy for log cleanup then execute it now
                if (cleaningStrategy != null) {
                    final LogMessage msg = new LogMessage(null, null);
                    msg.setDelay(LogDelete.DELETE_START_DELAY);
                    logHandlerThread.sendMessage(MSG_TASK_START_CLEANUP, msg);
                }
            } catch (IOException e) {
                isInitDone = false;
                LogHelper.closeStream(os);
                os = null;
                LogHelper.closeStream(ps);
                throw new RuntimeException(e.getClass() + " " + e.getMessage());
            }
        }
    }

    /**
     * The reverse of init.
     * Destroys the impl and closes the streams.
     * The init method needs to be called after this to use the impl again.
     * A new file will be created.
     */
    @SuppressWarnings("WeakerAccess")
    public static synchronized void destroy() {
        synchronized (initLock) {
            if (!isInitDone) {
                return;
            }
            final LogMessage msg = new LogMessage(null, null);
            logHandlerThread.sendMessage(MSG_TASK_STOP, msg);
            isInitDone = false;
            path = null;
            logHandlerThread = null;
            filename = null;
            cleaningStrategy = null;
            LogHelper.closeStream(os);
            LogHelper.closeStream(ps);
            os = null;
            ps = null;
        }
    }

    /**
     * Closes the current file and does a new init and starts using a new file.
     * WARNING: Some log lines may be lost while the library transitions from one impl to the next one.
     */
    public static void createNewFile() {
        synchronized (initLock) {
            initGuard();
            final File currentPath = path;
            final LogDelete currentCleaningStrategy = cleaningStrategy;
            destroy();
            init(currentPath, currentCleaningStrategy);
        }
    }

    /**
     * Is init() called yet or not.
     *
     * @return is init done?
     */
    public static boolean isInitDone() {
        return isInitDone;
    }

    /**
     * Return prefix used on all log files. Can be used to copy/delete files later.
     *
     * @return Log file prefix used on all files
     */
    public static String getLogFilePrefix() {
        return PREFIX;
    }

    /**
     * Return extension used on all log files.
     *
     * @return Log file extension used on all files
     */
    public static String getLogFileExtension() {
        return EXTENSION_TEXT_LOG;
    }

    public static String getCompressedLogFileExtension() {
        return EXTENSION_COMPRESSED_TEXT_LOG;
    }

    /**
     * Return current log file name. This does not include the path of the file. Path is available from {@link #getLogDir()}. Full file is returned from {@link #getLogFile()}.
     * NB: will exist after init().
     *
     * @return Current log file name (or null)
     */
    public static String getLogFilename() {
        initGuard();
        return filename;
    }

    /**
     * Return the current log File.
     * NB: Will exist after init()
     *
     * @return Current log File
     */
    @Nullable
    public static File getLogFile() {
        initGuard();
        if (path == null || TextUtils.isEmpty(filename)) {
            return null;
        }
        return new File(path, filename);
    }

    /**
     * Return the directory used to store logs. Full file is returned from {@link #getLogFile()}.
     * NB: will exist after init(), same as given there.
     *
     * @return Directory used to store logs
     */
    public static File getLogDir() {
        initGuard();
        return path;
    }

    @Override
    protected void error(final String tag, final String msg) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_ERROR, new LogMessage(tag, msg));
        }
    }

    @Override
    protected void error(final String tag, final String msg, final Throwable tr) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_ERROR, new LogMessage(tag, msg, tr));
        }
    }

    @Override
    protected void debug(final String tag, final String msg) {
        synchronized (initLock) {
            if (logHandlerThread != null) {
                logHandlerThread.sendMessage(MSG_LOG_DEBUG, new LogMessage(tag, msg));
            }
        }
    }

    @Override
    protected void debug(final String tag, final String msg, Throwable tr) {
        synchronized (initLock) {
            if (logHandlerThread != null) {
                logHandlerThread.sendMessage(MSG_LOG_DEBUG, new LogMessage(tag, msg, tr));
            }
        }
    }

    @Override
    protected void warning(final String tag, final String msg) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_WARNING, new LogMessage(tag, msg));
        }
    }

    @Override
    protected void warning(final String tag, final String msg, final Throwable tr) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_WARNING, new LogMessage(tag, msg, tr));
        }
    }

    @Override
    protected void info(final String tag, final String msg) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_INFO, new LogMessage(tag, msg));
        }
    }

    @Override
    protected void info(final String tag, final String msg, Throwable tr) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_INFO, new LogMessage(tag, msg, tr));
        }
    }

    @Override
    protected void wtf(String tag, String msg) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_WTF, new LogMessage(tag, msg));
        }
    }

    @Override
    protected void wtf(String tag, String msg, Throwable tr) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_WTF, new LogMessage(tag, msg, tr));
        }
    }

    @Override
    protected void verbose(String tag, String msg) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_VERBOSE, new LogMessage(tag, msg));
        }
    }

    @Override
    protected void verbose(String tag, String msg, Throwable tr) {
        if (logHandlerThread != null) {
            logHandlerThread.sendMessage(MSG_LOG_VERBOSE, new LogMessage(tag, msg, tr));
        }
    }

    /**
     * HandlerThread to write log stuff
     */
    private static class LogHandlerThread extends HandlerThread implements Callback {
        /**
         * For messages that come before onLooperPrepared()
         */
        private Queue<LogMessage> messageQueue = new LinkedBlockingQueue<>();
        /**
         * Handler for posting messages
         */
        private Handler mHandler;

        /**
         * Create and start the thread
         */
        LogHandlerThread() {
            super("LogHandlerThread");
            start();
        }

        private static void write(final char level, final String tag, final String msg) {
            String sb = createFormattedTimestamp() +
                    ' ' +
                    level +
                    '/' +
                    tag +
                    ' ' +
                    msg +
                    '\n';
            write(sb);
        }

        private static void write(final char level, final String tag, final String msg, final Throwable tr) {
            write(level, tag, msg);
            String sb = createFormattedTimestamp() +
                    ' ' +
                    level +
                    '/' +
                    tag +
                    ' ';
            write(sb);
            tr.printStackTrace(ps);
            write("\n");
        }

        private static String createFormattedTimestamp() {
            return logLineDateFormat.format(new Date(System.currentTimeMillis()));
        }

        private static void write(String s) {
            try {
                if (os != null) {
                    os.write(s.getBytes());
                    os.flush();
                } else {
                    throw new IOException("outputstream null :(");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        /**
         * Execute the current log cleanup strategy
         */
        private static void cleanup() {
            if (cleaningStrategy == null) {
                return;
            }
            try {
                cleaningStrategy.execute(path, filename, getLogFilePrefix(), getLogFileExtension());
                cleaningStrategy.execute(path, filename, getLogFilePrefix(), getCompressedLogFileExtension());
            } catch (Exception e) {
                write(LEVEL_MARKER_ERROR, "LogImplFile", "cleanup()", e);
                // If something goes wrong then abandon the strategy
                cleaningStrategy = null;
            }
        }

        /**
         * Then looper is prepared, then log all the queued stuff
         */
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            // Create a handler
            mHandler = new Handler(getLooper(), this);
            // Send all the queued messages
            for (LogMessage msg : messageQueue) {
                sendMessage(msg.getWhat(), msg);
            }
            messageQueue.clear();
            messageQueue = null;
        }

        /**
         * Send a log message
         *
         * @param what    Command code
         * @param message LogMessage
         */
        synchronized void sendMessage(final int what, final LogMessage message) {
            if (mHandler != null) {
                // Looper is prepared, safe to send the message directly
                if (message.getDelay() < 0L) {
                    mHandler.sendMessage(android.os.Message.obtain(mHandler, what, message));
                } else {
                    mHandler.sendMessageDelayed(android.os.Message.obtain(mHandler, what, message), message.getDelay());
                }
            } else {
                // Looper not ready yet, queue the message
                message.setWhat(what);
                messageQueue.add(message);
            }
        }

        /**
         * Handle messages to LogHandlerThread
         */
        @Override
        public boolean handleMessage(final Message msg) {
            final LogMessage message = (LogMessage) msg.obj;

            // Do we have a task?
            if (msg.what == MSG_TASK_START_CLEANUP) {
                cleanup();
                return false;
            } else if (msg.what == MSG_TASK_STOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mHandler.getLooper().quitSafely();
                } else {
                    mHandler.getLooper().quit();
                }
                return false;
            }

            // Do we have a log message?
            char marker;
            switch (msg.what) {
                case MSG_LOG_HEADER:
                    // Write data here and return
                    write(message.getData());
                    return false;
                case MSG_LOG_ERROR:
                    marker = LEVEL_MARKER_ERROR;
                    break;
                case MSG_LOG_INFO:
                    marker = LEVEL_MARKER_INFO;
                    break;
                case MSG_LOG_DEBUG:
                    marker = LEVEL_MARKER_DEBUG;
                    break;
                case MSG_LOG_WARNING:
                    marker = LEVEL_MARKER_WARNING;
                    break;
                case MSG_LOG_WTF:
                    marker = LEVEL_MARKER_WTF;
                    break;
                case MSG_LOG_VERBOSE:
                    marker = LEVEL_MARKER_VERBOSE;
                    break;
                default:
                    // No matches found, return here
                    return false;
            }

            if (message.getThrowable() == null) {
                write(marker, message.getTag(), message.getData());
            } else {
                write(marker, message.getTag(), message.getData(), message.getThrowable());
            }
            return false;
        }

    }

    /**
     * Helper class to store log data
     */
    private static class LogMessage {
        private int what;
        private String tag;
        private String data;
        private Throwable throwable;
        private long delay;

        LogMessage(final String tag, final String data) {
            this(tag, data, null);
        }

        LogMessage(final String tag, final String data, final Throwable throwable) {
            this.tag = tag;
            this.data = data;
            this.throwable = throwable;
            this.what = -1;
        }

        String getTag() {
            return tag;
        }

        public String getData() {
            return data;
        }

        Throwable getThrowable() {
            return throwable;
        }

        int getWhat() {
            return what;
        }

        void setWhat(final int what) {
            this.what = what;
        }

        long getDelay() {
            return delay;
        }

        void setDelay(@SuppressWarnings("SameParameterValue") long delay) {
            this.delay = delay;
        }
    }

}
