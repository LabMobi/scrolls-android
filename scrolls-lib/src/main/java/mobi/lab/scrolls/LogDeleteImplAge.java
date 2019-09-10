package mobi.lab.scrolls;

import java.io.File;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Log delete strategy by log file age
 * Harri Kirik, harri35@gmail.com
 */
public class LogDeleteImplAge extends LogDelete {
    @SuppressWarnings("WeakerAccess")
    public static final long AGE_KEEP_30_MINUTES = 30 * 60 * 1000L;
    @SuppressWarnings("WeakerAccess")
    public static final long AGE_KEEP_1_HOUR = 2 * AGE_KEEP_30_MINUTES;
    @SuppressWarnings("WeakerAccess")
    public static final long AGE_KEEP_12_HOURS = 12 * AGE_KEEP_1_HOUR;
    @SuppressWarnings("WeakerAccess")
    public static final long AGE_KEEP_1_DAY = 2 * AGE_KEEP_12_HOURS;
    public static final long AGE_KEEP_3_DAYS = 3 * AGE_KEEP_1_DAY;
    @SuppressWarnings("unused")
    public static final long AGE_KEEP_5_DAYS = 5 * AGE_KEEP_1_DAY;
    @SuppressWarnings("unused")
    public static final long AGE_KEEP_7_DAYS = 7 * AGE_KEEP_1_DAY;
    @SuppressWarnings("unused")
    public static final long AGE_KEEP_1_MONTH = 31 * AGE_KEEP_1_DAY;
    @SuppressWarnings("unused")
    public static final long AGE_KEEP_FOREVER = -1L;
    private final long ageInMillis;

    /**
     * Delete files older than ageInMillis.
     *
     * @param ageInMillis age in milliseconds
     */
    public LogDeleteImplAge(final long ageInMillis) {
        this.ageInMillis = ageInMillis;
    }

    @Override
    protected File[] findFilesToDelete(final File logPath, final String currentLogFilename, final String prefix, final String extension) {
        if (logPath == null || !logPath.exists() || !logPath.isDirectory() || ageInMillis < 0L) {
            // Something is wrong. Abort
            return null;
        }

        final long currentTime = System.currentTimeMillis();
        // Find the files we want to delete
        return logPath.listFiles((dir, name) -> {
            if (!LogHelper.isALogFileButNotAnActiveOne(name, currentLogFilename, prefix, extension)) {
                // U Can't Touch This (hammertime)
                return false;
            }
            final File file = new File(dir, name);
            if (file.isDirectory()) {
                return false;
            }
            // Seems we found a log file
            final long fileAge = file.lastModified();
            return (currentTime - fileAge) > ageInMillis;
        });
    }

}
