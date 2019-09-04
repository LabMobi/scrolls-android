package mobi.lab.scrolls.tools;

import android.os.AsyncTask;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.data.LogDeleteParams;
import mobi.lab.scrolls.data.LogFileActListener;

/**
 * Worker to delete logs in a non-UI thread.
 *
 * @author harri
 */
public class LogDeleteWorker extends AsyncTask<LogDeleteParams, Void, Boolean> {

    private LogFileActListener listener;

    @Override
    protected Boolean doInBackground(LogDeleteParams... args) {
        if (args[0] == null) {
            Log.getInstance(this).e("Invalid LogDeleteParams object");
            return false;
        }
        this.listener = args[0].getListener();
        if (args[0].isSingleFile()) {
            return LogHelper.deleteFile(args[0].getLogFile(), args[0].isVerbose());
        } else {
            return LogHelper.deleteAllLogs(args[0].getLogDir(), args[0].getLogPrefix(), args[0].getFileNameToKeep(), args[0].isVerbose());
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (this.listener != null) {
            this.listener.onLogDeleteDone(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.listener != null) {
            this.listener.onLogDeleteDone(false);
        }
    }

}
