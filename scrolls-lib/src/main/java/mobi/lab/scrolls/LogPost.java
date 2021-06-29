package mobi.lab.scrolls;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

/**
 * Version 2.0 (two-point-oh) of the LogPost class.
 *
 * @author madis
 */
public abstract class LogPost {

    /**
     * Use this type if posting logs from some file log implementation
     * Needs {@link LogImplFile#init(java.io.File)} to be called before posting.
     */
    public static String LOG_TYPE_MOBILE = "mobilog";
    /**
     * Use this log type if posting full Android logcat logs
     */
    public static String LOG_TYPE_LOGCAT = "logcat";

    /**
     * Tag used for test tag
     */
    public static String LOG_TAG_TEST = "test";
    /**
     * Tag used for app crash
     */
    public static String LOG_TAG_CRASH = "crash";

    /*
     * Optional fields
     */
    protected static String deviceModel;
    protected static String version;
    protected static String fileProviderAuthority;
    protected String type;
    protected String[] tags;
    protected boolean compressLogFile;
    protected LogPostListener listener;

    private static Class logPostImplementation;

    /**
     * Sets the device model string
     *
     * @param deviceModel device model
     */
    public static void setDeviceModel(String deviceModel) {
        LogPost.deviceModel = deviceModel;
    }

    /**
     * Sets the application version
     *
     * @param version application version
     */
    public static void setVersion(String version) {
        LogPost.version = version;
    }

    /**
     * Sets the FileProvider authority used to create log file uris
     *
     * @param authority File Provider Authority
     */
    public static void setFileProviderAuthority(String authority) {
        LogPost.fileProviderAuthority = authority;
    }


    /**
     * Sets the LogPost implementation for posting
     *
     * @param clazz Class
     */
    public static void setImplementation(Class clazz) {
        logPostImplementation = clazz;
    }

    /**
     * Get a new instance of LogPost.
     *
     * @return A new instance of LogPost implementation
     */
    public static LogPost getInstance() {
        if (logPostImplementation == null) {
            logPostImplementation = LogPostImpl.class;
        }
        try {
            return (LogPost) logPostImplementation.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        }
    }

    /**
     * Sets the log type.
     *
     * @param type Log type
     */
    public void setType(String type) {
        if (TextUtils.isEmpty(type)) {
            type = LogPost.LOG_TYPE_MOBILE;
        }
        this.type = type;
    }


    /**
     * Sets custom tags associated with the posted log
     *
     * @param tags Tags
     */
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * Compress the log file.
     *
     * @param compressLogFile Should the log file be compressed?
     */
    public void setCompressLogFile(boolean compressLogFile) {
        this.compressLogFile = compressLogFile;
    }

    /**
     * Set or update the listener.
     *
     * @param listener Listener
     */
    public void setListener(@Nullable LogPostListener listener) {
        this.listener = listener;
    }

    /**
     * Post log data.
     *
     * @param content     The string content to post.
     * @param callbackTag Listener callback tag
     * @param attachment  The file to post
     * @param listener    Listener. Can be updated via {@link #setListener(LogPostListener)}.
     */
    public abstract void post(@NonNull Context context, @NonNull final String callbackTag, @Nullable String content, @Nullable File attachment, @Nullable LogPostListener listener);

    public interface LogPostListener {
        /**
         * Log post was successful.
         *
         * @param postTag    Log post listener tag
         * @param content    Content
         * @param attachment attachment
         */
        void onLogPostSuccess(@NonNull String postTag, String content, File attachment);

        /**
         * Log post failed
         *
         * @param postTag Log post listener tag
         * @param e       Error
         */
        void onLogPostFailed(@NonNull String postTag, @NonNull Throwable e);
    }
}
