package mobi.lab.scrolls.data;

import java.io.File;

/**
 * Parameter object for the LogDeleteWorker.
 */
public class LogDeleteParams {
    private boolean singleFile;
    private File logFile;
    private File logDir;
    private String logPrefix;
    private String fileNameToKeep;
    private LogFileActListener listener;
    private boolean verbose;

    /**
     * Use this if you want to delete a single file and keep all others
     *
     * @param logFile
     * @param listener
     */
    public LogDeleteParams(final File logFile, final LogFileActListener listener) {
        super();
        this.logFile = logFile;
        this.listener = listener;
        this.verbose = false;
        this.singleFile = true;
    }

    /**
     * Use this if you want to delete all the logs and keep only a single file
     *
     * @param logDir
     * @param logPrefix
     * @param fileNameToKeep
     * @param listener
     */
    public LogDeleteParams(final File logDir, final String logPrefix, final String fileNameToKeep, final LogFileActListener listener) {
        super();
        this.logDir = logDir;
        this.logPrefix = logPrefix;
        this.fileNameToKeep = fileNameToKeep;
        this.listener = listener;
        this.verbose = false;
        this.singleFile = false;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public File getLogDir() {
        return logDir;
    }

    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
    }

    public String getFileNameToKeep() {
        return fileNameToKeep;
    }

    public void setFileNameToKeep(String fileNameToKeep) {
        this.fileNameToKeep = fileNameToKeep;
    }

    public LogFileActListener getListener() {
        return listener;
    }

    public void setListener(LogFileActListener listener) {
        this.listener = listener;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isSingleFile() {
        return singleFile;
    }

}
