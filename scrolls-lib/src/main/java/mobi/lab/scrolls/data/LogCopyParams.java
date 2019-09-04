package mobi.lab.scrolls.data;

import java.io.File;

/**
 * Params for LogCopyWorker
 */
public class LogCopyParams {
    private File logDir;
    private File dstDir;
    private String logPrefix;
    private LogFileActListener listener;
    private boolean verbose;

    /**
     * Copy all the logs to a different location
     *
     * @param logDir
     * @param dstDir
     * @param logPrefix
     * @param listener
     */
    public LogCopyParams(File logDir, File dstDir, String logPrefix, LogFileActListener listener) {
        super();
        this.logDir = logDir;
        this.dstDir = dstDir;
        this.logPrefix = logPrefix;
        this.listener = listener;
    }

    public File getLogDir() {
        return logDir;
    }

    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    public File getDstDir() {
        return dstDir;
    }

    public void setDstDir(File dstDir) {
        this.dstDir = dstDir;
    }

    public String getLogPrefix() {
        return logPrefix;
    }

    public void setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
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

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
