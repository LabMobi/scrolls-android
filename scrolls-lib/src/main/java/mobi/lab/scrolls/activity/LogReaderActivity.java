package mobi.lab.scrolls.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogImplFile;
import mobi.lab.scrolls.LogPostBuilder;
import mobi.lab.scrolls.R;
import mobi.lab.scrolls.adapter.LogReaderAdapter;
import mobi.lab.scrolls.data.LogDeleteParams;
import mobi.lab.scrolls.data.LogFileActListener;
import mobi.lab.scrolls.tools.GuiHelper;
import mobi.lab.scrolls.tools.LogDeleteWorker;
import mobi.lab.scrolls.tools.SharedConstants;

/**
 * Activity to display the contents of a log file. Usually launched from LogListActivity.<br/>
 * Needs to get a path to log file from intent, use EXTRA_LOG_FILE_PATH<br/>.
 * PS: Posting logs needs the project info to be included in the intent bundle.<br/>
 * <p>
 * If you need to use this class directly then you need add the following components to Manifest:<br/>
 * <p>
 * {@code <activity android:name="LogReaderActivity" />} <br/>
 * {@code <activity android:name="LogPostActivity" />} <br/>
 * <br/>
 * Posting logs also requires the following permission: {@code <uses-permission android:name="android.permission.INTERNET" />}
 *
 * @author harri
 */
public class LogReaderActivity extends Activity implements SharedConstants {

    private static final int ID_TEXT_DATA = 1;
    private static final int ID_LIST_DATA = 2;

    private static final int MENU_ITEM_POST = 0;
    private static final int MENU_ITEM_DELETE = 1;
    private static final int MENU_ITEM_RELOAD = 2;

    private Log log;
    private String logFilePath;
    private ArrayList<CharSequence> logData;

    private TextView textData;

    /*
     * Params for log post
     */
    private String[] tags;
    private boolean highlightEnabled;
    private ListView listData;
    private LogReaderAdapter logReaderAdapter;
    private AsyncTask<String, CharSequence[], CharSequence[]> logLoader;
    private boolean isLoading;
    private boolean confirmPost;
    private boolean displayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProgressBarIndeterminate(true);
        setContentView(createLayout());

        textData = (TextView) findViewById(ID_TEXT_DATA);
        listData = (ListView) findViewById(ID_LIST_DATA);
        log = Log.getInstance("LogViewerActivity");
        logFilePath = null;
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            logFilePath = extras.getString(EXTRA_LOG_FILE_PATH);
            tags = extras.getStringArray(EXTRA_POST_TAGS);
            highlightEnabled = extras.getBoolean(EXTRA_HIGHLIGHT_ENABLED);
            confirmPost = extras.getBoolean(EXTRA_CONFIRM);
            displayResult = extras.getBoolean(EXTRA_DISPLAY_RESULT);
        }

        if (TextUtils.isEmpty(logFilePath)) {
            log.e("LogDetailsActivity: Please specify LOG_FILE_PATH");
            finish();
            return;
        }

        final ResultHolder holder = (ResultHolder) getLastNonConfigurationInstance();
        if (holder != null && holder.data != null) {
            logData = holder.data;
        }
        if (logData != null) {
            displayData(logData, false, false);
        } else {
            // Load the logs if needed
            loadData();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (!isLoading && logData != null) {
            return new ResultHolder(logData);
        } else {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(ContextMenu.NONE, MENU_ITEM_POST, ContextMenu.NONE, getString(R.string.post_this_log));
        menu.add(ContextMenu.NONE, MENU_ITEM_DELETE, ContextMenu.NONE, getString(R.string.delete_this_log));
        menu.add(ContextMenu.NONE, MENU_ITEM_RELOAD, ContextMenu.NONE, getString(R.string.reload_this_log));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem postItem = menu.findItem(MENU_ITEM_POST);
        final MenuItem deleteItem = menu.findItem(MENU_ITEM_DELETE);
        if (logData == null) {
            postItem.setVisible(false);
            deleteItem.setVisible(false);
        } else {
            postItem.setVisible(true);
            deleteItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_POST:
                postLogFile();
                break;
            case MENU_ITEM_DELETE:
                deleteLogFile();
                break;
            case MENU_ITEM_RELOAD:
                reloadLogFile();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logLoader != null && logLoader.getStatus() == AsyncTask.Status.RUNNING) {
            logLoader.cancel(false);
        }
    }

    protected void loadData() {
        logLoader = new LogDataLoader().execute(logFilePath);
    }

    protected void displayData(final ArrayList<CharSequence> data, final boolean add, final boolean isLoading) {
        this.isLoading = isLoading;
        if (add && this.logData != null && data != null) {
            this.logData.addAll(data);
        } else {
            this.logData = data;
        }

        if (this.logData == null) {
            if (isLoading) {
                textData.setText(getString(R.string.loading_the_log));
                textData.setVisibility(View.VISIBLE);
            } else {
                textData.setText(getString(R.string.failed_to_load_log_generic));
                textData.setVisibility(View.VISIBLE);
            }
            textData.setGravity(Gravity.CENTER);
            return;
        }

        // Fill in the log data
        if (logReaderAdapter == null || listData.getAdapter() == null) {
            logReaderAdapter = new LogReaderAdapter(this, data);
            listData.setAdapter(logReaderAdapter);
            setClickActions(listData, logData);
            textData.setVisibility(View.GONE);
            listData.setVisibility(View.VISIBLE);
        } else {
            logReaderAdapter.notifyDataSetChanged();
        }

    }

    protected void setClickActions(final ListView listView, final ArrayList<CharSequence> logData) {
        // Short click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (logData != null && logData.size() > pos) {
                    // Copy the text
                    copyLogLine(logData.get(pos));
                }
            }
        });

        // Long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                if (logData != null && logData.size() > pos) {
                    // Share the text
                    shareLogLine(logData.get(pos));
                }
                return true;
            }
        });

    }

    protected void copyLogLine(final CharSequence text) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setText(text);
            Toast.makeText(LogReaderActivity.this, getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT).show();
        }

    }

    protected void shareLogLine(final CharSequence text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    /**
     * Delete the log file
     */
    protected void deleteLogFile() {
        if (logData == null) {
            // Fail
            log.e("deleteLog: logData == null");
            Toast.makeText(this, getString(R.string.failed_to_delete_the_file), Toast.LENGTH_LONG).show();
            return;
        }

        final LogDeleteParams params = new LogDeleteParams(new File(logFilePath), new LogFileActListener() {

            @Override
            public void onLogPostDone(final String logId) {
            }

            @Override
            public void onLogDeleteDone(boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(LogReaderActivity.this, getString(R.string.file_deleted), Toast.LENGTH_LONG).show();
                    // Set the result so that the caller know about the delete
                    final Intent intent = new Intent();
                    intent.putExtra(EXTRA_LOG_FILE_PATH, logFilePath);
                    setResult(RESULT_DELETE_SUCCESS, intent);
                    finish();
                } else {
                    Toast.makeText(LogReaderActivity.this, getString(R.string.failed_to_delete_the_file), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLogCopyDone(boolean isSuccess) {
            }
        });

        new LogDeleteWorker().execute(params);
    }

    protected void postLogFile() {
        if (logData == null) {
            // Fail
            log.e("postLogFile: logData == null");
            return;
        }
        final LogPostBuilder builder = new LogPostBuilder();
        builder.setFile(new File(logFilePath));
        builder.setConfirmEnabled(confirmPost);
        builder.setShowResultEnabled(displayResult);
        builder.addTags(tags);
        builder.launchActivity(this);
    }

    protected void reloadLogFile() {
        startActivity(getIntent());
        finish();
    }

    /**
     * Create a typical ListActivity layout
     */
    protected View createLayout() {
        // Container
        final FrameLayout viewContainer = new FrameLayout(this);
        final FrameLayout.LayoutParams viewContainerParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        viewContainer.setLayoutParams(viewContainerParams);

        // TextView
        final TextView textView = new TextView(this);
        final FrameLayout.LayoutParams textViewParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        textView.setId(ID_TEXT_DATA);
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(View.VISIBLE);

        // ListView
        final ListView viewList = new ListView(this);
        viewList.setId(ID_LIST_DATA);
        viewList.setFastScrollEnabled(true);
        viewList.setDivider(null);
        viewList.setDividerHeight(0);
        viewList.setVisibility(View.INVISIBLE);
        final FrameLayout.LayoutParams viewListParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        viewContainer.addView(viewList, viewListParams);
        viewContainer.addView(textView, textViewParams);

        return GuiHelper.createAndAddProgressIndicator(this, viewContainer);
    }

    /**
     * Method to format a log line.<br/>
     */
    protected CharSequence formatLogLine(final CharSequence line, final char lastLineType) {
        if (TextUtils.isEmpty(line)) {
            return line;
        }
        // lastLineType = setLineType(lastLineType, data[i].toString());

        final SpannableString span = new SpannableString(line);
        if (lastLineType == LogImplFile.LEVEL_MARKER_ERROR) {
            span.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR_ERROR), 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (lastLineType == LogImplFile.LEVEL_MARKER_WARNING) {
            span.setSpan(new ForegroundColorSpan(HIGHLIGHT_COLOR_WARNING), 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return span;
    }


    protected char[] setLineType(String line, char[] result) {
        result[1] = '0';
        if (line.contains(LogImplFile.LEVEL_MARKER_ERROR + "/")) {
            result[0] = LogImplFile.LEVEL_MARKER_ERROR;
            result[1] = '1';
        } else if (line.contains(LogImplFile.LEVEL_MARKER_WARNING + "/")) {
            result[0] = LogImplFile.LEVEL_MARKER_WARNING;
            result[1] = '1';
        } else if (line.contains(LogImplFile.LEVEL_MARKER_INFO + "/")) {
            result[0] = LogImplFile.LEVEL_MARKER_INFO;
            result[1] = '1';
        } else if (line.contains(LogImplFile.LEVEL_MARKER_DEBUG + "/")) {
            result[0] = LogImplFile.LEVEL_MARKER_DEBUG;
            result[1] = '1';
        }
        return result;
    }

    protected static class ResultHolder {
        ArrayList<CharSequence> data;

        public ResultHolder(final ArrayList<CharSequence> data) {
            this.data = data;
        }
    }

    /**
     * Helper class to load in (and format?) log data
     */
    protected class LogDataLoader extends AsyncTask<String, CharSequence[], CharSequence[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GuiHelper.setProgressIndicatorVisibility(LogReaderActivity.this, true);
            displayData(null, false, true);
        }

        @Override
        protected CharSequence[] doInBackground(String... logPaths) {
            if (logPaths == null || logPaths.length == 0) {
                return null;
            }

            final File logFile = new File(logPaths[0]);
            if (logFile != null && logFile.exists() && logFile.isFile()) {
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(logFile);
                    final SpannableStringBuilder builder = new SpannableStringBuilder();
                    final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    String rawLine = "";
                    char[] lineProps = new char[]{' ', '1'};
                    while ((rawLine = br.readLine()) != null && !isCancelled()) {
                        char lastLineType = lineProps[0];
                        setLineType(rawLine, lineProps);
                        if (lineProps[1] == '1') {
                            // "New" line entry
                            // 1. Finish and send out the old one
                            // 1.1 Add formatting
                            CharSequence formattedLine;
                            if (highlightEnabled) {
                                formattedLine = formatLogLine(builder.subSequence(0, builder.length()), lastLineType);
                            } else {
                                formattedLine = builder.subSequence(0, builder.length());
                            }
                            // 1.2 Send out an update
                            publishProgress(new CharSequence[]{formattedLine});
                            // 2. Start a new one
                            builder.clear();
                            builder.append(rawLine);
                        } else {
                            // Append to old line entry
                            if (builder.length() > 0) {
                                builder.append("\n");
                            }
                            builder.append(rawLine);
                        }

                    }


                    CharSequence formattedLine;
                    if (highlightEnabled) {
                        formattedLine = formatLogLine(builder.subSequence(0, builder.length()), lineProps[0]);
                    } else {
                        formattedLine = builder.subSequence(0, builder.length());
                    }
                    return new CharSequence[]{formattedLine};

                } catch (FileNotFoundException e) {
                    log.e("LogDataLoader", e);
                } catch (IOException e) {
                    log.e("LogDataLoader", e);
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            // Not needed
                        }
                    }
                }
            } else {
                log.e("LogDataLoader: log file " + logPaths[0] + " not found");
            }

            return null;
        }

        @Override
        protected void onPostExecute(CharSequence[] result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result[0])) {
                displayData(createArrayList(result), true, false);
            }
            GuiHelper.setProgressIndicatorVisibility(LogReaderActivity.this, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            GuiHelper.setProgressIndicatorVisibility(LogReaderActivity.this, false);
        }

        @Override
        protected void onProgressUpdate(CharSequence[]... values) {
            super.onProgressUpdate(values);
            displayData(createArrayList(values[0]), true, true);
        }

        private ArrayList<CharSequence> createArrayList(CharSequence[] result) {
            final ArrayList<CharSequence> arrayList = new ArrayList<CharSequence>();
            for (int i = 0; i < result.length; i++) {
                arrayList.add(result[i]);
            }
            return arrayList;
        }

    }

}
