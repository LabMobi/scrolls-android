package mobi.lab.scrolls.tools;

import android.os.AsyncTask;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.data.LogCopyParams;
import mobi.lab.scrolls.data.LogFileActListener;

/**
 * Helper worker to copy logs in a non-UI thread
 *
 * @author harri
 */
public class LogCopyWorker extends AsyncTask<LogCopyParams, Void, Boolean> {
    LogFileActListener listener;

    @Override
    protected Boolean doInBackground(final LogCopyParams... args) {
        if (args[0] == null) {
            Log.getInstance(this).e("Invalid LogCopyParams object");
            return false;
        }
        this.listener = args[0].getListener();
        return LogHelper.copyLogs(args[0].getLogDir(), args[0].getDstDir(), args[0].getLogPrefix(), args[0].isVerbose());
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        super.onPostExecute(result);
        if (this.listener != null) {
            this.listener.onLogCopyDone(result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.listener != null) {
            this.listener.onLogCopyDone(false);
        }
    }
}
