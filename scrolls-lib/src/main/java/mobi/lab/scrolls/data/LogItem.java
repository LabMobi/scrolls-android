package mobi.lab.scrolls.data;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import mobi.lab.scrolls.LogImplFile;
import mobi.lab.scrolls.tools.SharedConstants;

/**
 * Data class for a log item
 *
 * @author harri
 */
public class LogItem {
    private CharSequence displayName;
    private String filename;
    private String path;
    private long fileSize; // Length of this file in bytes.
    private boolean containsErrors;

    public LogItem(String filename, String path) {
        this(filename, path, 0);
    }

    public LogItem(String filename, String path, long fileSize) {
        this.filename = filename;
        this.path = path;
        this.fileSize = fileSize;
        this.containsErrors = false;
    }

    /**
     * Create a nice display name for the item
     *
     * @param filename
     * @param fileSize
     * @param containsErrors
     * @return
     */
    private static CharSequence generateDisplayName(final String filename, final long fileSize, final boolean containsErrors) {
        SpannableStringBuilder displayNameBuff = new SpannableStringBuilder(filename);
        // Check if we know the format
        if (!TextUtils.isEmpty(filename) && filename.startsWith(LogImplFile.getLogFilePrefix()) && filename.endsWith(LogImplFile.getLogFileExtension())) {
            displayNameBuff = new SpannableStringBuilder();
            // Create the displayName
            String parts[] = filename.split("_");
            for (int i = 1; i < parts.length; i++) {
                switch (i) {
                    case 1:
                        displayNameBuff.append(parts[i]);
                        break;
                    case 4:
                        displayNameBuff.append(" " + parts[i]);
                        break;
                    case 2:
                    case 3:
                        displayNameBuff.append("." + parts[i]);
                        break;
                    case 5:
                    case 6:
                        displayNameBuff.append(":" + parts[i]);
                        break;
                    // case 7:
                    // displayNameBuff.append(":" + parts[i].substring(0,
                    // parts[i].length() -
                    // LogImplFile.getLogFileExtension().length()));
                    // break;

                }
            }

        }

        // Append the file size if given
        if (fileSize > 0) {
            displayNameBuff.append(" " + getFileSizeString(fileSize));
        }

        // If the file contains errors, then append
        if (containsErrors) {
            displayNameBuff.append(" " + SharedConstants.DISPLAY_MARKER_ERROR);
            displayNameBuff.setSpan(new ForegroundColorSpan(SharedConstants.HIGHLIGHT_COLOR_ERROR), displayNameBuff.length() - SharedConstants.DISPLAY_MARKER_ERROR.length(), displayNameBuff.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return displayNameBuff.subSequence(0, displayNameBuff.length());
    }

    /**
     * Return a human readable filesize string
     *
     * @param fileSize
     * @return
     */
    private static String getFileSizeString(final long fileSize) {
        if (fileSize >= (1024L * 1024L)) {
            // return mbytes
            double result = (((double) fileSize) / (1024.0d * 1024.0d));
            return (Math.round(result * 100.0d) / 100.0d) + " mB";
        } else if (fileSize >= 1024L) {
            // return kbytes
            double result = (((double) fileSize) / 1024.0d);
            return (Math.round(result * 100.0d) / 100.0d) + " kB";
        }
        return fileSize + " B";
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return filename;
    }

    public CharSequence getDisplayName() {
        if (TextUtils.isEmpty(displayName)) {
            displayName = generateDisplayName(filename, fileSize, containsErrors);
        }
        return displayName;
    }

    public void regenerateDisplayName() {
        displayName = null;
        getDisplayName();
    }

    public void setContainsErrors(final boolean containsErrors) {
        this.containsErrors = containsErrors;
    }

    public boolean containsErrors() {
        return containsErrors;
    }
}
