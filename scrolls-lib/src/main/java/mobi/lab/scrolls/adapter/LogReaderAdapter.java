package mobi.lab.scrolls.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mobi.lab.scrolls.tools.LogHelper;

/**
 * Adapter for log reader
 */
public class LogReaderAdapter extends BaseAdapter {

    private static final int ID_TEXT_LOG = 0;

    private Context context;
    private ArrayList<CharSequence> data;

    public LogReaderAdapter(final Context context, final ArrayList<CharSequence> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(final int position) {
        return !TextUtils.isEmpty(getItem(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getItem(final int index) {
        return data.get(index);
    }

    @Override
    public long getItemId(final int id) {
        // Use array index as id for now
        return id;
    }

    @Override
    public View getView(final int pos, View view, final ViewGroup arg2) {
        ViewHolder holder = null;
        if (view == null) {
            view = createItemLayout();

            holder = new ViewHolder();
            holder.textLog = (TextView) view.findViewById(ID_TEXT_LOG);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Fill in the contents
        holder.textLog.setText(getItem(pos));

        return view;
    }

    private View createItemLayout() {
        // Log text
        final TextView textLog = new TextView(context);
        textLog.setId(ID_TEXT_LOG);
        textLog.setTextAppearance(context, android.R.style.TextAppearance_Small);
        textLog.setSingleLine(false);
        int padding = LogHelper.toPixels(context, 8);
        textLog.setPadding(padding, 0, padding, 0);
        return textLog;
    }

    private static class ViewHolder {
        public TextView textLog;
    }

}
