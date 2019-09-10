package mobi.lab.scrolls.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;
import java.util.Calendar;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogImplFile;
import mobi.lab.scrolls.LogPostBuilder;
import mobi.lab.scrolls.activity.LogPostActivity;

/**
 * LogHelper extension with Android stuff
 *
 * @author harri
 */
public class LogHelper {

    /**
     * Returns a String containing of the following: MANUFACTURER, MODEL, PRODUCT, Firmware
     */
    public static String getDeviceInfo() {
        final StringBuilder info = new StringBuilder();
        // MANUFACTURER
        info.append(android.os.Build.MANUFACTURER + " ");
        // Model
        info.append(android.os.Build.MODEL + " ");
        // PRODUCT
        info.append("(" + android.os.Build.PRODUCT + ") ");
        // Firmware
        info.append(android.os.Build.VERSION.RELEASE);
        return info.toString();
    }

    /**
     * Returns information on display metrics and orientation.
     *
     * @param activity
     * @return
     */
    public static String getScreenInfo(final Activity activity) {
        final StringBuilder info = new StringBuilder();
        info.append("--- LOG device screen info start---\n");

        final Display display = activity.getWindowManager().getDefaultDisplay();
        info.append("Display id: " + display.getDisplayId() + "\n");
        info.append("Display width: " + display.getWidth() + "\n");
        info.append("Display height: " + display.getHeight() + "\n");

        String orientation = "ORIENTATION_UNDEFINED";
        switch (activity.getResources().getConfiguration().orientation) {
            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                orientation = "ORIENTATION_LANDSCAPE";
                break;
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                orientation = "ORIENTATION_PORTRAIT";
                break;
            case android.content.res.Configuration.ORIENTATION_SQUARE:
                orientation = "ORIENTATION_SQUARE";
                break;
        }
        info.append("Screen orientation (on app start): " + orientation + "\n");

        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        info.append("Screen density: " + metrics.density + "\n");
        info.append("Screen densityDpi: " + metrics.densityDpi + "\n");
        info.append("Screen scaledDensity: " + metrics.scaledDensity + "\n");

        final int screenSize = (activity.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK);

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            info.append("Screen size: NORMAL\n");
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            info.append("Screen size: SMALL\n");
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            info.append("Screen size: LARGE\n");
        } else if (screenSize == 4) {
            info.append("Screen size: XLARGE\n");
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_UNDEFINED) {
            info.append("Screen size: UNDEFINED\n");
        }

        final int screenLength = (activity.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_LONG_MASK);

        if (screenLength == Configuration.SCREENLAYOUT_LONG_YES) {
            info.append("Screen length: LONG\n");
        } else if (screenLength == Configuration.SCREENLAYOUT_LONG_NO) {
            info.append("Screen length: NOTLONG\n");
        } else if (screenLength == Configuration.SCREENLAYOUT_LONG_UNDEFINED) {
            info.append("Screen length: UNDEFINED\n");
        }

        info.append("--- LOG device screen info end ---\n");
        return info.toString();
    }

    /**
     * Return the current date and time in local time
     */
    public static String getDateTimeString() {
        final Calendar cal = Calendar.getInstance();
        if (cal != null) {
            return cal.getTime().toLocaleString() + " (" + cal.getTimeZone().getDisplayName() + ")";
        }
        return "N/A";
    }

    /**
     * Copy all log files to different destination. NB: Do not call in the UI thread!
     *
     * @param logDir    - Directory of log files
     * @param dstDir    - Directory to copy to
     * @param logPrefix - Prefix to identify log files
     * @return true if at least some files were copied
     */
    public static boolean copyLogs(final File logDir, final File dstDir, final String logPrefix) {
        return copyLogs(logDir, dstDir, logPrefix, false);
    }

    /**
     * Copy all log files to different destination. NB: Do not call in the UI thread!
     *
     * @param logDir    - Directory of log files
     * @param dstDir    - Directory to copy to
     * @param logPrefix - Prefix to identify log files
     * @param verbose   Log out the files copied.
     * @return true if at least some files were copied
     */
    public static boolean copyLogs(final File logDir, final File dstDir, final String logPrefix, final boolean verbose) {
        final Log log = Log.getInstance(LogHelper.class);
        if (logDir != null && logDir.exists() && logDir.isDirectory() && dstDir != null && dstDir.exists() && dstDir.isDirectory() && !isEmpty(logPrefix)) {
            // Find the files
            final File[] fileList = logDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String filename) {
                    if (filename.toLowerCase().startsWith(logPrefix.toLowerCase())) {
                        return true;
                    }
                    return false;
                }
            });
            // Copy the files
            for (int i = 0; i < fileList.length; i++) {
                File dst = new File(dstDir, fileList[i].getName());
                try {
                    copyFile(fileList[i], dst);
                    if (verbose) {
                        log.d("Copied file: " + fileList[i].getName());
                    }
                } catch (IOException e) {
                    log.e("copyLogs", e);
                }
            }

            if (fileList.length == 0) {
                if (verbose) {
                    log.d("Found no log files to copy");
                }
                return false;
            }

        } else {
            log.e("Failed to copy log files to sdcard. Incorrect parameters?");
            return false;
        }
        return true;
    }

    /**
     * Copy file from one location to another.<br>
     * NB: Do not call in the UI thread!<br>
     * NB: Make sure you have the correct rights!<br>
     *
     * @param src Source file
     * @param dst Destination file
     * @return Destination file is success
     * @throws IOException
     */
    public static File copyFile(final File src, final File dst) throws IOException {
        if (src.exists() && src.canRead()) {
            final InputStream inStream = new FileInputStream(src);
            final OutputStream outStream = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inStream.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
            inStream.close();
            outStream.close();
            return dst;
        }
        return null;
    }

    /**
     * Delete a file
     *
     * @param file    File to delete
     * @param verbose Verbose error messages of not
     * @return
     */
    public static boolean deleteFile(final File file, final boolean verbose) {
        boolean success = false;
        final Log log = Log.getInstance(LogHelper.class);

        try {
            if (file != null && file.exists() && file.isFile()) {
                success = file.delete();
            }
        } catch (SecurityException e) {
            if (verbose) {
                log.e("deleteLog", e);
            }
            success = false;
        }

        return success;
    }

    /**
     * Delete log files. <br>
     * Will not remove file named fileNameToKeep<br>
     * NB: Do not call in the UI thread!<br>
     * NB: Make sure you have the correct rights!<br>
     *
     * @param logDir         Directory of logs files
     * @param logPrefix      Prefix to identify log files
     * @param fileNameToKeep Name of a file to keep (can be used to not to delete the current log file)
     * @return true on success
     */
    public static boolean deleteAllLogs(final File logDir, final String logPrefix, final String fileNameToKeep) {
        return deleteAllLogs(logDir, logPrefix, fileNameToKeep, false);
    }

    /**
     * Delete log files. <br>
     * Will not remove file named fileNameToKeep<br>
     * NB: Do not call in the UI thread!<br>
     * NB: Make sure you have the correct rights!<br>
     *
     * @param logDir         Directory of logs files
     * @param logPrefix      Prefix to identify log files
     * @param fileNameToKeep Name of a file to keep (can be used to not to delete the current log file)
     * @param verbose        Log out the names of deleted files (or files on which delete failed)
     * @return true on success
     */
    public static boolean deleteAllLogs(final File logDir, final String logPrefix, final String fileNameToKeep, final boolean verbose) {
        final Log log = Log.getInstance(LogHelper.class);
        if (logDir != null && logDir.exists() && logDir.isDirectory() && !isEmpty(logPrefix)) {
            // Find the files
            final File[] fileList = logDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String filename) {
                    if (filename.toLowerCase().startsWith(logPrefix.toLowerCase()) && !filename.equals(fileNameToKeep)) {
                        return true;
                    }
                    return false;
                }
            });
            // Delete the files
            for (int i = 0; i < fileList.length; i++) {
                try {
                    fileList[i].delete();
                    if (verbose) {
                        log.d("Deleted file: " + fileList[i].getName());
                    }
                } catch (Exception e) {
                    if (verbose) {
                        log.w("Failed to delete file: " + fileList[i].getName());
                    }
                }

            }
            if (fileList.length == 0) {
                if (verbose) {
                    log.d("Found no log files");
                }
                return false;
            }
        } else {
            log.e("Failed to delete log files. Incorrect parameters?");
            return false;
        }
        return true;
    }

    /**
     * Helper method to safely close a stream
     *
     * @param closable
     */
    public static void closeStream(final Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Encode a string so it would be safe to use as a parameter in log post url
     *
     * @param param String to encode
     */
    public static String urlEncodeParam(final String param) {
        String encodedParam = param;
        // Replace all the '/' a.k.a %2f symbols with '_' underscore and then urlencode
        return URLEncoder.encode(encodedParam.replace('/', '_'));
    }

    /**
     * Get an InputStream from LogCat logs.<br>
     * Needs a permission to read logs: {@code <uses-permission android:name="android.permission.READ_LOGS" />}<br>
     *
     * @param context      Activity context
     * @param logcatFormat One of 'brief' 'process' 'tag' 'thread' 'raw' 'time' 'threadtime' 'long'
     * @param logcatBuffer One of 'main', 'radio', 'events'
     * @param logcatLevel  One of 'V' Verbose 'D' Debug 'I' Info 'W' Warn 'E' Error 'F' Fatal 'S' Silent (suppress all output)
     * @return InputStream or null
     */
    public static InputStream getLogcatStream(final Context context, final String logcatFormat, final String logcatBuffer, final String logcatLevel) {
        Process process;
        try {
            // Execute the logcat process with the correct params
            process = Runtime.getRuntime().exec(new String[]{"logcat", "-d", "-v", logcatFormat, "-b", logcatBuffer, "*:" + logcatLevel});
            // Return the stream
            return process.getInputStream();
        } catch (IOException e) {
            Log.getInstance(LogHelper.class).e("getLogcatStream", e);
        }
        return null;
    }

    /**
     * Try to get a stream to the current file log<br>
     * NB: LogImplFile.class init() must have been called before using this
     *
     * @return FileInputStream or null
     */
    public static InputStream getCurrentLogFileStream() {
        if (!LogImplFile.isInitDone()) {
            throw (new IllegalStateException("You forgot to call the LogImplFile class init() method"));
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(LogImplFile.getLogDir(), LogImplFile.getLogFilename()));
        } catch (IOException e) {
            Log.getInstance(LogHelper.class).e("getCurrentLogFileStream", e);
        }

        return stream;
    }

    /**
     * Convert DP -> Pixels
     *
     * @return
     */
    public static int toPixels(final Context context, final float valueDP) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDP, context.getResources().getDisplayMetrics());
    }

    public static byte[] logcatStreamToBytes(InputStream is) {
        String logcat = logcatStreamToString(is);

        byte[] bytes = {};
        try {
            bytes = logcat.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            bytes = logcat.getBytes();
        }
        return bytes;
    }

    public static String logcatStreamToString(InputStream is) {
        StringBuilder logcat = new StringBuilder();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            reader = new InputStreamReader(is);
        }
        char[] buf = new char[4096];
        int read;
        try {
            while ((read = reader.read(buf)) != -1) {
                logcat.append(buf, 0, read);
            }
        } catch (IOException ioe) {

        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return logcat.toString();
    }

    /**
     * Add a new {@link UncaughtExceptionHandler} that starts {@link LogPostActivity} with the given extras.
     * Calls through to the app's default UncaughtExceptionHandler.
     *
     * @param applicationContext
     * @param logType
     * @param projectName
     * @param versionName
     * @param versionCode
     * @param logFilePath
     * @param postTags
     * @param confirm
     * @param displayResult
     * @deprecated Was meant for Scrolls 1.0 and contains unused parameters
     */
    public static void setUncaughtExceptionHandlerAndLogPostExtras(final Context applicationContext, final String logType,
                                                                   final String projectName, final String versionName, final int versionCode,
                                                                   final String logFilePath, final String[] postTags, final boolean confirm, final boolean displayResult) {

        final UncaughtExceptionHandler defaultExceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();

        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                // Do the things we need to do
                final Intent intent = new Intent(applicationContext, LogPostActivity.class);
                intent.putExtra(LogPostActivity.EXTRA_LOGTYPE, logType);
                intent.putExtra(LogPostActivity.EXTRA_LOG_FILE_PATH, logFilePath);
                intent.putExtra(LogPostActivity.EXTRA_POST_TAGS, postTags);
                intent.putExtra(LogPostActivity.EXTRA_CONFIRM, confirm);
                intent.putExtra(LogPostActivity.EXTRA_DISPLAY_RESULT, displayResult);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);

                // Call the system default handler
                defaultExceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    /**
     * A helper to set up the automatic crash posting. Currently only supports posting file logs.
     *
     * @param applicationContext
     * @param confirm
     * @param displayResult
     * @param tags
     */
    public static void setUncaughtExceptionHandler(final Context applicationContext, final boolean confirm, final boolean displayResult, final String... tags) {
        final UncaughtExceptionHandler defaultExceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.getInstance(applicationContext.getClass().getSimpleName()).e("FATAL ERROR!", ex);
                // Do the things we need to do
                final LogPostBuilder builder = new LogPostBuilder();
                builder.setConfirmEnabled(confirm)
                        .setShowResultEnabled(displayResult)
                        .launchActivity(applicationContext);

                if (tags != null) {
                    builder.addTags(tags);
                }

                // Call the system default handler
                defaultExceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    public static boolean isEmpty(final String str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isALogFileButNotAnActiveOne(final String name, final String currentLogFilename, final String prefix, final String extension) {
        return (!TextUtils.isEmpty(name) && !TextUtils.equals(currentLogFilename, name) && name.toLowerCase().startsWith(prefix.toLowerCase()) && name.toLowerCase().endsWith(extension.toLowerCase()));
    }
}
