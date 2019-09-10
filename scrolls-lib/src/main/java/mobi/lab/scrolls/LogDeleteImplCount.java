package mobi.lab.scrolls;

import java.io.File;
import java.util.Arrays;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Log delete strategy by keeping only last N files
 * Harri Kirik, harri35@gmail.com
 */
@SuppressWarnings("unused")
public class LogDeleteImplCount extends LogDelete {
    @SuppressWarnings("unused")
    public static final int COUNT_KEEP_ALL = -1;
    @SuppressWarnings("unused")
    public static final int COUNT_KEEP_ACTIVE = 0;
    private int count;

    /**
     * Keeps N files in addition to the current active log file
     *
     * @param count Amount of files to keep.
     */
    @SuppressWarnings("unused")
    public LogDeleteImplCount(final int count) {
        this.count = count;
    }

    @Override
    protected File[] findFilesToDelete(final File logPath, final String currentLogFilename, final String prefix, final String extension) {
        if (logPath == null || !logPath.exists() || !logPath.isDirectory() || count < 0) {
            // Something is wrong. Or COUNT_KEEP_ALL. Abort
            return null;
        }

        // Get a list of log files
        final File[] logFiles = logPath.listFiles((dir, name) -> LogHelper.isALogFileButNotAnActiveOne(name, currentLogFilename, prefix, extension));

        // Did we get something?
        if (logFiles == null || logFiles.length <= count) {
            // We have nothing to remove
            return null;
        }

        // Sort by date modified
        Arrays.sort(logFiles, (o1, o2) -> {
            if (o1.lastModified() == o2.lastModified()) {
                return 0;
            }
            // Age before beauty
            return o1.lastModified() < o2.lastModified() ? -1 : 1;
        });

        // Remember and return the files we do not need
        final File[] filesToDelete = new File[logFiles.length - count];
        System.arraycopy(logFiles, 0, filesToDelete, 0, filesToDelete.length);
        return filesToDelete;
    }
}
