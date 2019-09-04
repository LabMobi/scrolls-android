package mobi.lab.scrolls.data;

/**
 * Listener used to notify the caller about finished actions on log files
 */
public interface LogFileActListener {
    /**
     * LogPost finished
     *
     * @param logId Log id on success, null on falure
     */
    void onLogPostDone(String logId);

    /**
     * Log delete finished
     *
     * @param isSuccess true if success
     */
    void onLogDeleteDone(boolean isSuccess);

    /**
     * Log copy finished
     *
     * @param isSuccess
     */
    void onLogCopyDone(boolean isSuccess);
}
