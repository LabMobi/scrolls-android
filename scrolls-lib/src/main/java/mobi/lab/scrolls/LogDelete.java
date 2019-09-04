package mobi.lab.scrolls;

import java.io.File;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Abstract class for defining log file delete strategies
 * Harri Kirik, harri35@gmail.com
 */
public abstract class LogDelete {
    public static final long DELETE_START_DELAY = 987l; // millis
    protected static final boolean VERBOSE = true;

    /**
     * Start the cleanup
     *
     * @param logPath            Log file directory
     * @param currentLogFilename Current active log file name
     * @param prefix             Prefix of log files
     * @param extension          The extension of log files
     */
    public void execute(final File logPath, final String currentLogFilename, final String prefix, final String extension) {
        if (logPath == null || !logPath.exists() || !logPath.isDirectory()) {
            // Something is wrong. Abort
            return;
        }
        final File[] files = findFilesToDelete(logPath, currentLogFilename, prefix, extension);
        if (files == null || files.length == 0) {
            if (VERBOSE) {
                Log.getInstance(this).d("execute - no files found, done");
            }
            return;
        }

        if (VERBOSE) {
            Log.getInstance(this).d("execute - deleting " + files.length + " files ..");
        }

        // Delete the files
        for (int i = 0; i < files.length; i++) {
            LogHelper.deleteFile(files[i], VERBOSE);
        }

        if (VERBOSE) {
            Log.getInstance(this).d("execute - files deleted, done");
        }
    }

    /**
     * Return a list of files to delete
     *
     * @param logPath            Log file directory
     * @param currentLogFilename Current active log file name
     * @param prefix             Prefix of log files
     * @param extension          The extension of log files
     * @return List of log files to delete
     */
    protected abstract File[] findFilesToDelete(final File logPath, final String currentLogFilename, final String prefix, final String extension);
}
