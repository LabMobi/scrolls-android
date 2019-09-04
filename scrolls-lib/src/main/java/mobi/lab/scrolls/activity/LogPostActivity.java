package mobi.lab.scrolls.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogPost;
import mobi.lab.scrolls.R;
import mobi.lab.scrolls.data.LogFileActListener;
import mobi.lab.scrolls.tools.LogHelper;
import mobi.lab.scrolls.tools.SharedConstants;
import mobi.lab.scrolls.view.ConfirmDialogLayout;

/**
 * Activity for allowing user to post logs. If there is need to use this in case of app crashes the you should use run this in another process<br/>
 * <p>
 * To make use of this you have to declare this in the Manifest: {@code <activity android:name="LogPostActivity" android:process="com.mobi.scrolls.android.activity.someOtherProcess" /> }
 * <br/>
 * You'll also need {@code  <uses-permission android:name="android.permission.READ_LOGS" />} to post logcat logs
 */
public class LogPostActivity extends Activity implements LogFileActListener, SharedConstants {
    private static final String STATE_POST_PROCESS = "com.mobi.scrolls.android.STATE_POST_PROCESS";

    private static final int DIALOG_CONFIRM_LOG_POST = 2;
    private static final int DIALOG_RETRY_POST = 3;

    private Log log;
    private boolean processStarted;
    private String[] tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = Log.getInstance(this);
        processStarted = false;
        // Assume activity result as canceled
        setResult(RESULT_POST_CANCELED, new Intent());

        if (savedInstanceState != null) {
            processStarted = savedInstanceState.getBoolean(STATE_POST_PROCESS);
        }
        final Bundle extras = getIntent().getExtras();
        final String filename = extras.getString(EXTRA_LOG_FILE_PATH);
        String logType = extras.getString(EXTRA_LOGTYPE);
        if (TextUtils.isEmpty(logType)) {
            logType = LogPost.LOG_TYPE_MOBILE;
        }
        final boolean confirm = extras.getBoolean(EXTRA_CONFIRM, false);
        // Check if we have something to post
        if (TextUtils.equals(logType, LogPost.LOG_TYPE_MOBILE) && (TextUtils.isEmpty(filename) || !(new File(filename).exists()))) {
            log.e("Failed to find the log file. " + (filename == null ? "No file was given." : ("Was asked to use: " + filename)));
            log.e("Maybe you wanted to do a LogPost.LOG_TYPE_LOGCAT post instead?");
            setResultAndFinish(RESULT_POST_FAILED);
            finish();
            return;
        }

        if (!processStarted) {
            processStarted = true;
            if (confirm) {
                // Ask confirmation if needed
                showDialog(DIALOG_CONFIRM_LOG_POST);
            } else {
                // Post the logs
                startLogpost();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_POST_PROCESS, processStarted);
        outState.putStringArray(EXTRA_POST_TAGS, tags);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(EXTRA_POST_TAGS)) {
            tags = savedInstanceState.getStringArray(EXTRA_POST_TAGS);
        } else {
            tags = new String[0];
        }
    }

    protected void startLogpost() {
        startLogpost(new String[0]);
    }

    protected void startLogpost(String[] tags) {
        // // Let's assume no dialog bundles so get the extras again
        final Bundle extras = getIntent().getExtras();
        final String filename = extras.getString(EXTRA_LOG_FILE_PATH);
        final String[] extraTags = extras.getStringArray(EXTRA_POST_TAGS);

        String[] finalTags;
        if (tags.length > 0) {
            finalTags = new String[extraTags.length + tags.length];
            System.arraycopy(extraTags, 0, finalTags, 0, extraTags.length);
            System.arraycopy(tags, 0, finalTags, extraTags.length, tags.length);
        } else {
            finalTags = extraTags;
        }

        String logType = extras.getString(EXTRA_LOGTYPE);
        if (TextUtils.isEmpty(logType)) {
            logType = LogPost.LOG_TYPE_MOBILE;
        }

        // Post the logs

        try {
            File attachment = null;
            String message = null;
            if (TextUtils.equals(logType, LogPost.LOG_TYPE_LOGCAT)) {
                InputStream is = LogHelper.getLogcatStream(this, "time", "main", "V");
                message = LogHelper.logcatStreamToString(is);
            } else if (!TextUtils.isEmpty(filename)) {
                attachment = new File(filename);
            }

            if (TextUtils.isEmpty(message) && (attachment == null)) {
                // Fail on empty log
                Toast.makeText(this, getString(R.string.failed_to_post_empty), Toast.LENGTH_LONG).show();
                setResultAndFinish(RESULT_POST_FAILED);
            } else {
                LogPost logPost = LogPost.getInstance();
                logPost.setTags(finalTags);
                logPost.setType(logType);
                logPost.post(getApplicationContext(), message, attachment);
                setResultAndFinish(RESULT_POST_SUCCESS);
            }
        } catch (Exception ioe) {
            log.e(ioe, "postLog(): file open failed");
            Toast.makeText(LogPostActivity.this, getString(R.string.failed_to_post_generic), Toast.LENGTH_LONG).show();
            setResultAndFinish(RESULT_POST_FAILED);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CONFIRM_LOG_POST:
                AlertDialog.Builder postDialogBuilder = new AlertDialog.Builder(this);
                postDialogBuilder.setTitle(getString(R.string.confirm_post_log));

                final ConfirmDialogLayout layout = new ConfirmDialogLayout(this);
                int sidePadding = dpToPixel(10);
                layout.setInputMargins(sidePadding, 0, sidePadding, 0);

                postDialogBuilder.setView(layout);
                postDialogBuilder.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResultAndFinish(RESULT_POST_CANCELED);
                    }
                });
                postDialogBuilder.setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do the post
                        tags = layout.getTags(); //for retry dialog
                        startLogpost(tags);
                    }
                });
                // Return the dialog
                return postDialogBuilder.create();
            case DIALOG_RETRY_POST:
                AlertDialog.Builder retryDialogBuilder = new AlertDialog.Builder(this);
                retryDialogBuilder.setTitle(getString(R.string.title_dialog_retry));

                retryDialogBuilder.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResultAndFinish(RESULT_POST_CANCELED);
                    }
                });
                retryDialogBuilder.setPositiveButton(getString(R.string.retry), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do the post
                        startLogpost(tags);
                    }
                });
                return retryDialogBuilder.create();

        }
        return super.onCreateDialog(id);

    }

    @Override
    public void onLogPostDone(final String logId) {
        final Bundle extras = getIntent().getExtras();
        final boolean displayResult = extras.getBoolean(EXTRA_DISPLAY_RESULT);
        if (displayResult) {
            String resultText = null;
            if (!TextUtils.isEmpty(logId)) {
                Toast.makeText(this, R.string.log_post_success, Toast.LENGTH_LONG).show();
                setResultAndFinish(RESULT_POST_SUCCESS);
            } else {
                //resultText = Res.getString(this, "failed_to_post_generic");
                //showDialog(DIALOG_RETRY_POST);
                showDialog(DIALOG_RETRY_POST);
            }
        }
    }

    @Override
    public void onLogDeleteDone(boolean success) {
        // Do not need
    }

    @Override
    public void onLogCopyDone(boolean success) {
        // Do not need
    }

    protected void setResultAndFinish(final int resultCode) {
        final Intent data = new Intent();
        setResult(resultCode, data);
        finish();
    }

    private int dpToPixel(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
