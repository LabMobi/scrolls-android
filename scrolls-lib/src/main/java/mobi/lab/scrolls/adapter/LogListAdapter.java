package mobi.lab.scrolls.adapter;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mobi.lab.scrolls.data.LogItem;
import mobi.lab.scrolls.tools.LogHelper;

/**
 * Adapter for displaying a list of log files
 *
 * @author harri
 */
public class LogListAdapter extends BaseAdapter {

    private static final int ID_TEXT_NAME = 123;
    private List<LogItem> logs;
    private Context context;

    public LogListAdapter(Context context, List<LogItem> logs) {
        this.context = context;
        this.logs = logs;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int index) {
        return logs.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            // Create the view
            view = createItemLayout();
            // view = new View(context);
            holder = new ViewHolder();
            holder.textName = (TextView) view.findViewById(ID_TEXT_NAME);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // fill with data
        final LogItem item = (LogItem) getItem(position);
        holder.textName.setText(item.getDisplayName());

        return view;
    }

    private View createItemLayout() {
        // Container
        final LinearLayout layoutContainer = new LinearLayout(context);
        final int paddSides = LogHelper.toPixels(context, 8);
        final int paddTopAndBottom = LogHelper.toPixels(context, 16);
        layoutContainer.setPadding(paddSides, paddTopAndBottom, paddSides, paddTopAndBottom);

        // Name text
        final TextView textName = new TextView(context);
        textName.setId(ID_TEXT_NAME);
        textName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        textName.setEllipsize(TruncateAt.END);
        layoutContainer.addView(textName);
        return layoutContainer;
    }

    private static class ViewHolder {
        public TextView textName;
    }

}
