package mobi.lab.scrolls;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mobi.lab.scrolls.activity.LogPostActivity;

/**
 * Builder class for easy Log posting. Enables configuration and easy posting with {@link LogPostActivity}
 */
public class LogPostBuilder {
    private File file = null;
    private Set<String> tags = new HashSet<String>();
    private boolean confirmPost = true;
    private boolean displayResult = true;
    private String logType = LogPost.LOG_TYPE_MOBILE;

    /**
     * Default constructor. Uses the current log's file as data source (sets logType to {@link LogPost#LOG_TYPE_MOBILE})
     */
    public LogPostBuilder() {
        this(LogPost.LOG_TYPE_MOBILE);
    }

    /**
     * Constructor, sets the logType used for log posting.
     *
     * @param logTypeConst
     * @see LogPost#LOG_TYPE_MOBILE
     * @see LogPost#LOG_TYPE_LOGCAT
     */
    public LogPostBuilder(String logTypeConst) {
        setLogType(logTypeConst);
    }

    /**
     * Sets the log file.
     *
     * @param f Log file
     * @return
     */
    public LogPostBuilder setFile(File f) {
        this.file = f;
        return this;
    }

    /**
     * Adds custom tags to the log
     *
     * @param tags
     * @return
     */
    public LogPostBuilder addTags(String... tags) {
        if (tags != null && tags.length > 0) {
            this.tags.addAll(Arrays.asList(tags));
        } else {
            this.tags.clear();
        }
        return this;
    }

    /**
     * If true: a confirmation dialog will be shown before posting the log
     * If false: the log is posted without confirmation
     *
     * @param confirmEnabled
     * @return
     */
    public LogPostBuilder setConfirmEnabled(boolean confirmEnabled) {
        this.confirmPost = confirmEnabled;
        return this;
    }

    /**
     * If true, a Toast with the post result is shown.
     *
     * @param showResultEnabled
     * @return
     */
    public LogPostBuilder setShowResultEnabled(boolean showResultEnabled) {
        this.displayResult = showResultEnabled;
        return this;
    }

    /**
     * Sets the log's type.
     *
     * @param logTypeConst
     * @return
     * @see LogPost#LOG_TYPE_MOBILE
     * @see LogPost#LOG_TYPE_LOGCAT
     */
    public LogPostBuilder setLogType(final String logTypeConst) {
        this.logType = logTypeConst;
        if (LogPost.LOG_TYPE_MOBILE.equals(logTypeConst)) {
            this.file = new File(LogImplFile.getLogDir(), LogImplFile.getLogFilename());
        }
        return this;
    }

    /**
     * Launches {@link LogPostActivity} in a new task to post the log.
     *
     * @param context
     */
    public void launchActivity(Context context) {
        Intent intent = new Intent(context, LogPostActivity.class);
        intent.putExtra(LogPostActivity.EXTRA_LOG_FILE_PATH, getLogFilesPath());
        intent.putExtra(LogPostActivity.EXTRA_POST_TAGS, tags.toArray(new String[tags.size()]));
        intent.putExtra(LogPostActivity.EXTRA_CONFIRM, confirmPost);
        intent.putExtra(LogPostActivity.EXTRA_LOGTYPE, logType);
        intent.putExtra(LogPostActivity.EXTRA_DISPLAY_RESULT, displayResult);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String getLogFilesPath() {
        return LogPost.LOG_TYPE_MOBILE.equals(logType) ? file.getAbsolutePath() : null;
    }
}
