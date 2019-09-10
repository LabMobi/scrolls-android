package mobi.lab.scrolls.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.List;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogImplFile;
import mobi.lab.scrolls.LogPostBuilder;
import mobi.lab.scrolls.LogViewBuilder;
import mobi.lab.scrolls.R;
import mobi.lab.scrolls.adapter.LogListAdapter;
import mobi.lab.scrolls.data.LogDeleteParams;
import mobi.lab.scrolls.data.LogFileActListener;
import mobi.lab.scrolls.data.LogItem;
import mobi.lab.scrolls.tools.GuiHelper;
import mobi.lab.scrolls.tools.LogDeleteWorker;
import mobi.lab.scrolls.tools.SharedConstants;

/**
 * Activity that will display a list of log files and allows to:<br>
 * 1. Open a log in LogReaderActivity<br>
 * 2. Delete a log<br>
 * 3. Post a log (needs to have project info for the post)<br>
 * 4. Delete all logs except the current active one<br>
 * <p>
 * To use you need to add all the following component declarations to the manifest:<br>
 * {@code <activity android:name="LogListActivity" />} <br>
 * {@code <activity android:name="LogReaderActivity" />} <br>
 * {@code <activity android:name="LogPostActivity" />} <br>
 * <p>
 * Posting logs also requires the following permission: {@code <uses-permission android:name="android.permission.INTERNET" /> }
 *
 * @author harri
 */
public class LogListActivity extends ListActivity implements SharedConstants {

    private static final int MENU_ITEM_POST = 0;
    private static final int MENU_ITEM_DELETE = 1;
    private static final int MENU_ITEM_DELETE_ALL = 2;
    private static final int MENU_ITEM_MARK_ERRORS = 3;

    private static final int REQUEST_READ_LOG = 0;

    private String logFolderPath;
    private ArrayList<LogItem> logs;

    private Log log;

    private LogListAdapter logListAdapter;

    /*
     * Params for log post
     */
    private String[] tags;
    private boolean highlightEnabled;
    private boolean isLoading;
    private LogListMarker markerWorker;
    private LogListLoader searchWorker;
    private boolean displayResult;
    private boolean confirmPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createLayout());

        log = Log.getInstance("LogViewerActivity");
        logFolderPath = null;
        isLoading = false;
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            logFolderPath = extras.getString(EXTRA_LOG_FOLDER_PATH);
            displayResult = extras.getBoolean(EXTRA_DISPLAY_RESULT, true);
            confirmPost = extras.getBoolean(EXTRA_CONFIRM, true);
            highlightEnabled = extras.getBoolean(EXTRA_HIGHLIGHT_ENABLED, true);
            tags = extras.getStringArray(EXTRA_POST_TAGS);
        }

        if (TextUtils.isEmpty(logFolderPath)) {
            logFolderPath = getFilesDir().getAbsolutePath();
        }

        final SearchResultHolder holder = (SearchResultHolder) getLastNonConfigurationInstance();
        if (holder != null) {
            logs = holder.logs;
        }
        if (logs != null && logs.size() > 0) {
            displayData(logs, false);
        } else {
            // Load the logs if needed
            loadData();
        }
        registerForContextMenu(getListView());
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (!isLoading && logs != null) {
            return new SearchResultHolder(logs);
        } else {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (markerWorker != null && markerWorker.getStatus() == AsyncTask.Status.RUNNING) {
            markerWorker.cancel(false);
            markerWorker = null;
        }
        if (searchWorker != null && searchWorker.getStatus() == AsyncTask.Status.RUNNING) {
            searchWorker.cancel(false);
            searchWorker = null;
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final LogItem item = (LogItem) getListAdapter().getItem(position);
        if (item != null) {
            final LogViewBuilder builder = new LogViewBuilder();
            builder.setFile(new File(item.getPath()));
            builder.addTags(tags);
            builder.setConfirmEnabled(confirmPost);
            builder.setHighlightEnabled(highlightEnabled);
            builder.setShowResultEnabled(displayResult);
            builder.launchActivity(this);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(ContextMenu.NONE, MENU_ITEM_POST, ContextMenu.NONE, getString(R.string.post_this_log));
        menu.add(ContextMenu.NONE, MENU_ITEM_DELETE, ContextMenu.NONE, getString(R.string.delete_this_log));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_READ_LOG && resultCode == LogReaderActivity.RESULT_DELETE_SUCCESS) {
            // We know what log got deleted, lets not reload, just remove this entry
            if (data != null && !TextUtils.isEmpty(data.getStringExtra(LogReaderActivity.EXTRA_LOG_FILE_PATH)) && logs != null) {
                // Find out the item id
                int index = -1;
                for (int i = 0; i < logs.size(); i++) {
                    if (TextUtils.equals(logs.get(i).getPath(), data.getStringExtra(LogReaderActivity.EXTRA_LOG_FILE_PATH))) {
                        index = i;
                        break;
                    }
                }
                // Remove the item from the list
                if (index != -1) {
                    removeLogEntry(index);
                }
            } else {
                // Something changed (but we do not know exactly what) with the log file and we should update the list
                loadData();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE:
                if (info != null) {
                    deleteLogFile((int) info.id);
                }
                return true;
            case MENU_ITEM_POST:
                if (info != null) {
                    postLogFile((int) info.id);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(ContextMenu.NONE, MENU_ITEM_DELETE_ALL, ContextMenu.NONE, getString(R.string.delete_all_except_current));
        menu.add(ContextMenu.NONE, MENU_ITEM_MARK_ERRORS, ContextMenu.NONE, getString(R.string.mark_error_logs));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem deleteItem = menu.findItem(MENU_ITEM_DELETE_ALL);
        final MenuItem markItem = menu.findItem(MENU_ITEM_MARK_ERRORS);
        if (logs == null || logs.size() == 0) {
            deleteItem.setVisible(false);
            markItem.setVisible(false);
        } else {
            deleteItem.setVisible(true);
            markItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE_ALL:
                deleteLogFiles();
                break;
            case MENU_ITEM_MARK_ERRORS:
                markLogFilesWithErrors();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * Create a typical ListActivity layout
     */
    protected View createLayout() {
        // LinearLayoyut container
        final LinearLayout viewContainer = new LinearLayout(this);
        final LinearLayout.LayoutParams viewContainerParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        viewContainer.setLayoutParams(viewContainerParams);

        // Android ListView
        final ListView listView = new ListView(this);
        final ListView.LayoutParams listViewParams = new ListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        listView.setLayoutParams(listViewParams);
        listView.setId(android.R.id.list);
        listView.setFastScrollEnabled(true);

        // Android empty ListView TextView
        final TextView textView = new TextView(this);
        final LayoutParams textViewParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        textView.setLayoutParams(textViewParams);
        textView.setId(android.R.id.empty);
        textView.setGravity(Gravity.CENTER);
        textView.setText(getString(R.string.no_logs_found));

        viewContainer.addView(listView);
        viewContainer.addView(textView);

        return GuiHelper.createAndAddProgressIndicator(this, viewContainer);
    }

    protected void loadData() {
        searchWorker = new LogListLoader();
        searchWorker.execute(logFolderPath);
    }

    protected void displayData(final ArrayList<LogItem> logs, final boolean isLoading) {
        this.logs = logs;
        this.isLoading = isLoading;
        if (logs == null) {
            setEmptyMessage(isLoading);
            logListAdapter = null;
            setListAdapter(null);
            return;
        } else {
            setEmptyMessage(isLoading);
            logListAdapter = new LogListAdapter(this, logs);
            setListAdapter(logListAdapter);
        }
    }

    protected void setEmptyMessage(final boolean isLoading) {
        final TextView textEmpty = (TextView) findViewById(android.R.id.empty);
        if (textEmpty != null && isLoading) {
            textEmpty.setText(getString(R.string.looking_for_logs));
        } else if (textEmpty != null && !isLoading) {
            textEmpty.setText(getString(R.string.no_logs_found));
        }
    }

    /**
     * Delete the log behind a given logs array index
     *
     * @param index
     */
    protected void deleteLogFile(final int index) {
        if (logs == null || logs.size() <= index) {
            // Fail
            log.e("deleteLog: logs == null or logs.size() <= id");
            Toast.makeText(LogListActivity.this, getString(R.string.failed_to_delete_the_file), Toast.LENGTH_LONG).show();
            return;
        }
        final LogItem item = logs.get(index);
        if (item == null) {
            // Fail
            log.e("deleteLog: LogItem == null");
            Toast.makeText(LogListActivity.this, getString(R.string.failed_to_delete_the_file), Toast.LENGTH_LONG).show();
            return;
        }

        final LogDeleteParams params = new LogDeleteParams(new File(item.getPath()), new LogFileActListener() {

            @Override
            public void onLogPostDone(String logId) {
            }

            @Override
            public void onLogDeleteDone(boolean isSuccess) {
                if (isSuccess) {
                    removeLogEntry(index);
                    Toast.makeText(LogListActivity.this, getString(R.string.file_deleted), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LogListActivity.this, getString(R.string.failed_to_delete_the_file), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLogCopyDone(boolean isSuccess) {
            }
        });
        new LogDeleteWorker().execute(params);
    }

    protected void removeLogEntry(final int index) {
        if (logs != null && logs.size() > index) {
            logs.remove(index);
            logListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Delete all log files except the one we are currently writing to
     */
    protected void deleteLogFiles() {
        final LogDeleteParams params = new LogDeleteParams(new File(logFolderPath), LogImplFile.getLogFilePrefix(), LogImplFile.getLogFilename(), new LogFileActListener() {

            @Override
            public void onLogPostDone(String logId) {
                // Not needed
            }

            @Override
            public void onLogDeleteDone(boolean isSuccess) {
                String text = getString(R.string.files_deleted);
                if (!isSuccess) {
                    text = getString(R.string.failed_to_delete_the_files);
                }
                Toast.makeText(LogListActivity.this, text, Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onLogCopyDone(boolean isSuccess) {
                // Not needed
            }
        });
        new LogDeleteWorker().execute(params);

    }

    protected void postLogFile(final int index) {
        if (logs == null || logs.size() <= index) {
            // Fail
            log.e("postLogFile: logs == null or logs.size() <= id");
            return;
        }
        final LogItem item = logs.get(index);
        if (item == null) {
            // Fail
            log.e("postLogFile: LogItem == null");
            return;
        }

        final LogPostBuilder builder = new LogPostBuilder();
        builder.setFile(new File(item.getPath()));
        builder.setConfirmEnabled(confirmPost);
        builder.setShowResultEnabled(displayResult);
        builder.addTags(tags);
        builder.launchActivity(this);
    }

    /**
     * Try to look inside the listed error files and indicate the ones that contain errors
     */
    protected void markLogFilesWithErrors() {
        markerWorker = new LogListMarker();
        LogItem[] contents = new LogItem[logs.size()];
        markerWorker.execute(logs.toArray(contents));
    }

    /**
     * Holder class for conf changes
     */
    protected static class SearchResultHolder {
        public ArrayList<LogItem> logs;

        public SearchResultHolder(final ArrayList<LogItem> logs) {
            this.logs = logs;
        }
    }

    /**
     * Worker to look up and display a list of available log files
     */
    protected class LogListLoader extends AsyncTask<String, Void, ArrayList<LogItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, true);
            displayData(null, true);
        }

        @Override
        protected ArrayList<LogItem> doInBackground(String... paths) {
            if (paths == null) {
                return null;
            }

            final ArrayList<LogItem> items = new ArrayList<LogItem>();
            for (String path : paths) {
                if (isCancelled()) {
                    break;
                }
                try {
                    final File dir = new File(path);
                    if (dir.exists() && dir.isDirectory()) {
                        final File[] fileList = dir.listFiles((dir1, filename) -> (filename != null && filename.startsWith(LogImplFile.getLogFilePrefix()) && filename.endsWith(LogImplFile.getLogFileExtension())));

                        // Sort the list please
                        Arrays.sort(fileList);
                        for (int i = fileList.length - 1; i >= 0; i--) {
                            items.add(new LogItem(fileList[i].getName(), fileList[i].getAbsolutePath(), fileList[i].length()));
                        }
                    }
                } catch (SecurityException e) {
                    log.e("LogListLoader", e);
                }
            }

            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<LogItem> result) {
            super.onPostExecute(result);
            displayData(result, false);
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, false);
        }

    }

    protected class LogListMarker extends AsyncTask<LogItem[], Integer, List<LogItem>> {

        @Override
        protected List<LogItem> doInBackground(LogItem[]... items) {
            if (items.length == 0) {
                return null;
            }
            // Check if the file contains error markings
            for (int i = 0; i < items[0].length; i++) {
                final File logFile = new File(items[0][i].getPath());
                if (logFile != null && logFile.exists() && logFile.isFile()) {
                    FileInputStream stream = null;
                    try {
                        stream = new FileInputStream(logFile);
                        final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                        String rawLine = "";
                        while ((rawLine = br.readLine()) != null && !isCancelled()) {
                            if (!TextUtils.isEmpty(rawLine) && rawLine.contains(LogImplFile.LEVEL_MARKER_ERROR + "/")) {
                                publishProgress(i);
                                // We need to find only one occurrence
                                break;
                            }
                        }
                    } catch (FileNotFoundException e) {
                        log.e("LogListMarker", e);
                    } catch (IOException e) {
                        log.e("LogListMarker", e);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                // Not needed
                            }
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (logs != null && logs.size() > values[0]) {
                final LogItem item = logs.get(values[0]);
                item.setContainsErrors(true);
                item.regenerateDisplayName();
                logs.set(values[0], item);
                if (logListAdapter != null) {
                    logListAdapter.notifyDataSetChanged();
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, true);
        }

        @Override
        protected void onPostExecute(List<LogItem> result) {
            super.onPostExecute(result);
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            GuiHelper.setProgressIndicatorVisibility(LogListActivity.this, false);
        }

    }
}
