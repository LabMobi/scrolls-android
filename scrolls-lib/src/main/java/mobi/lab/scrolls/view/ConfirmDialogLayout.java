package mobi.lab.scrolls.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import mobi.lab.scrolls.R;

public class ConfirmDialogLayout extends LinearLayout {

    private TextView text;
    private EditText tagInput;

    public ConfirmDialogLayout(Context context) {
        super(context);
        init();
    }

    public ConfirmDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConfirmDialogLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setInputMargins(int left, int top, int right, int bottom) {
        ((LinearLayout.LayoutParams) tagInput.getLayoutParams()).setMargins(left, top, right, bottom);
    }

    private void init() {
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(rootParams);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        tagInput = new EditText(getContext());
        tagInput.setId(android.R.id.text1);
        LinearLayout.LayoutParams tagParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagInput.setLayoutParams(tagParams);
        tagInput.setHint(getContext().getString(R.string.hint_tags_input));

        //addView(text);
        addView(tagInput);
    }

    public String[] getTags() {
        String raw = tagInput.getText().toString();
        if (TextUtils.isEmpty(raw)) {
            return new String[0];
        } else {
            return TextUtils.split(raw, ",");
        }
    }
}
