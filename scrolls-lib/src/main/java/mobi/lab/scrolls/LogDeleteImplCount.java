package mobi.lab.scrolls;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Log delete strategy by keeping only last N files
 * Harri Kirik, harri35@gmail.com
 */
public class LogDeleteImplCount extends LogDelete {
    public static final int COUNT_KEEP_ALL = -1;
    public static final int COUNT_KEEP_ACTIVE = 0;
    private int count;

    /**
     * Keeps N files in addition to the current active log file
     *
     * @param count
     */
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
        final File[] logFiles = logPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return LogHelper.isNotActiveLogFile(name, currentLogFilename, prefix, extension);
            }
        });

        // Did we get something?
        if (logFiles == null || logFiles.length <= count) {
            // We have nothing to remove
            return null;
        }

        // Sort by date modified
        Arrays.sort(logFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.lastModified() == o2.lastModified()) {
                    return 0;
                }
                // Age before beauty
                return o1.lastModified() < o2.lastModified() ? -1 : 1;
            }
        });

        // Remember and return the files we do not need
        final File[] filesToDelete = new File[logFiles.length - count];
        for (int i = 0; i < filesToDelete.length; i++) {
            filesToDelete[i] = logFiles[i];
        }

        return filesToDelete;
    }
}
