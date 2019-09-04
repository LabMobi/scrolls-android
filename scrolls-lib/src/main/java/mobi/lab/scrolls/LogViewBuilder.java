package mobi.lab.scrolls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import mobi.lab.scrolls.activity.LogListActivity;
import mobi.lab.scrolls.activity.LogReaderActivity;
import mobi.lab.scrolls.tools.SharedConstants;

/**
 * Builder class for an easy way to launch {@link mobi.lab.scrolls.activity.LogListActivity} or {@link mobi.lab.scrolls.activity.LogReaderActivity}.
 * Choice is determined by either calling {@link #setDirectory(java.io.File)} or {@link #setFile(java.io.File)}
 */
public class LogViewBuilder {
    private File path;
    private boolean confirmPost;
    private boolean displayResult;
    private boolean highlightEnabled;
    private HashSet<String> tags;

    /**
     * Builder for opening an activity to see a list of logs or one single log form some specific path
     */
    public LogViewBuilder() {
        this.path = null;
        this.confirmPost = true;
        this.displayResult = true;
        this.highlightEnabled = true;
        this.tags = new HashSet<String>();
    }

    public LogViewBuilder setDirectory(final File dir) {
        if (dir == null || (dir.exists() && !dir.isDirectory())) {
            throw new IllegalArgumentException("Path " + dir + " is not a valid logs directory to use!");
        }
        this.path = dir;
        return this;
    }

    public LogViewBuilder setFile(final File file) {
        if (file == null || (file.exists() && file.isDirectory())) {
            throw new IllegalArgumentException("File " + file + " is not a valid log file to use!");
        }
        this.path = file;
        return this;
    }

    public LogViewBuilder currentLog() {
        setFile(new File(LogImplFile.getLogDir(), LogImplFile.getLogFilename()));
        setHighlightEnabled(true); // default choice
        setShowResultEnabled(true); // default choice
        setConfirmEnabled(true); // default choice
        return this;
    }

    public LogViewBuilder defaultLogs() {
        setDirectory(LogImplFile.getLogDir());
        setHighlightEnabled(true); // default choice
        setShowResultEnabled(true); // default choice
        setConfirmEnabled(true); // default choice
        return this;
    }

    /**
     * Adds custom tags to the log
     *
     * @param tags
     * @return
     */
    public LogViewBuilder addTags(String... tags) {
        if (tags != null && tags.length > 0) {
            this.tags.addAll(Arrays.asList(tags));
        } else {
            this.tags.clear();
        }
        return this;
    }

    /**
     * If true: a confirmation dialog will be shown before posting the log (default)
     * If false: the log is posted without confirmation
     *
     * @param confirmEnabled
     * @return
     */
    public LogViewBuilder setConfirmEnabled(boolean confirmEnabled) {
        this.confirmPost = confirmEnabled;
        return this;
    }

    /**
     * If true: Log highlighting is enabled (default)
     * If false: Log highlighting is disabled
     *
     * @param highlightEnabled
     * @return
     */
    public LogViewBuilder setHighlightEnabled(boolean highlightEnabled) {
        this.highlightEnabled = highlightEnabled;
        return this;
    }

    /**
     * If true (default choice), a Toast with the post result is shown.
     *
     * @param showResultEnabled
     * @return
     */
    public LogViewBuilder setShowResultEnabled(boolean showResultEnabled) {
        this.displayResult = showResultEnabled;
        return this;
    }

    public void launchActivity(final Context context) {
        if (context == null) {
            return;
        } else if (path == null) {
            throw new IllegalArgumentException("You need to call either setDirectory() or setFile() first!");
        } else if (!path.exists()) {
            throw new IllegalArgumentException("The given directory or file doesn't exist: " + path);
        }

        final Intent intent = new Intent(context, path.isDirectory() ? LogListActivity.class : LogReaderActivity.class);
        intent.putExtra(path.isDirectory() ? SharedConstants.EXTRA_LOG_FOLDER_PATH : SharedConstants.EXTRA_LOG_FILE_PATH, path.getAbsolutePath());
        intent.putExtra(SharedConstants.EXTRA_CONFIRM, confirmPost);
        intent.putExtra(SharedConstants.EXTRA_DISPLAY_RESULT, displayResult);
        intent.putExtra(SharedConstants.EXTRA_HIGHLIGHT_ENABLED, highlightEnabled);
        final String[] contents = new String[tags.size()];
        intent.putExtra(SharedConstants.EXTRA_POST_TAGS, tags.toArray(contents));

        if (!(context instanceof Activity)) {
            // This is no an activity context, seems we need to the new task flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }
}
