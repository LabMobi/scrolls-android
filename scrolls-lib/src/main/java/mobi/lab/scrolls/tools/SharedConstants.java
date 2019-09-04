package mobi.lab.scrolls.tools;

import android.app.Activity;
import android.graphics.Color;

public interface SharedConstants {
    /*
     * INTENT EXTRAS
     */
    /**
     * For displaying/posting logs: Path of the log file to display/post
     */
    String EXTRA_LOG_FILE_PATH = "com.mobi.scrolls.android.EXTRA_LOG_FILE_PATH";
    /**
     * For displaying a list of logs: Folder path from where to look for the logs (PS: for LogListActivity only)
     */
    String EXTRA_LOG_FOLDER_PATH = "com.mobi.scrolls.android.EXTRA_LOG_FOLDER_PATH";
    /**
     * For posting logs: String array of tags to add
     */
    String EXTRA_POST_TAGS = "com.mobi.scrolls.android.EXTRA_POST_TAGS";
    /**
     * For viewing a log: Should the LogReaderActivity try to add highlight color to error and warning log lines or not (default: enabled)
     */
    String EXTRA_HIGHLIGHT_ENABLED = "com.mobi.scrolls.android.EXTRA_HIGHLIGHT_ENABLED";
    /**
     * For posting logs: Logtype of the post. Default is LogPost.LOG_TYPE_MOBILE (file log). For posting Logcat logs (LogPost.LOG_TYPE_LOGCAT) you need the permission to read logs: {@code <uses-permission android:name="android.permission.READ_LOGS" /> }
     */
    String EXTRA_LOGTYPE = "com.mobi.scrolls.android.EXTRA_LOGTYPE";
    /**
     * If true, then user will be asked to confirm the logpost
     */
    String EXTRA_CONFIRM = "com.mobi.scrolls.android.EXTRA_CONFIRM";
    /**
     * If true, then user will be notified about the result of the post by a TOAST message
     */
    String EXTRA_DISPLAY_RESULT = "com.mobi.scrolls.android.EXTRA_DISPLAY_RESULT";
    /*
     *  * REQUEST CODES
     */
    /**
     * Log post was success
     */
    int RESULT_POST_SUCCESS = Activity.RESULT_OK;
    /**
     * Log post was canceled
     */
    int RESULT_POST_CANCELED = Activity.RESULT_CANCELED;
    /**
     * Log post failed
     */
    int RESULT_POST_FAILED = Activity.RESULT_FIRST_USER;
    /**
     * Log file deleted
     */
    int RESULT_DELETE_SUCCESS = Activity.RESULT_FIRST_USER + 1;


    int HIGHLIGHT_COLOR_ERROR = Color.parseColor("#FFFF0000");
    int HIGHLIGHT_COLOR_WARNING = Color.parseColor("#FFFF7F00");
    CharSequence DISPLAY_MARKER_ERROR = "(>_<)";
}
