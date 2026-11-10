package mobi.lab.scrolls.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Helper for internal GUI stuff
 */
public class GuiHelper {
    public static final int THEME_DEVICEDEFAULT = 16974120;
    public static final int THEME_DEVICEDEFAULT_LIGHT = 16974123;
    public static final int THEME_HOLO = 16973931;
    public static final int THEME_HOLO_LIGHT = 16973934;
    private static final int VERSION_CODE_ICE_CREAM_SANDWICH = 14;
    private static final int VERSION_CODE_HONEYCOMB = 11;
    private static final int ID_PROGRESS_INDICATOR = 42;
    private static final int ID_TOOLBAR = 43;

    /**
     * Create and add the ProgressIndicator (and fallback Toolbar if needed) to the activity layout,
     * and apply edge-to-edge WindowInsets.
     */
    public static ViewGroup createAndAddProgressIndicator(final Context context, final ViewGroup viewContainer) {
        if (context == null || viewContainer == null) {
            return viewContainer;
        }
        ViewGroup contentFrame = viewContainer;

        // Put the current stuff inside a FrameLayout if it isn't yet
        if (!(contentFrame instanceof FrameLayout)) {
            final FrameLayout frame = new FrameLayout(context);
            frame.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            contentFrame = frame;
            contentFrame.addView(viewContainer);
        }

        // Add the progress indicator
        final ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(ID_PROGRESS_INDICATOR);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        final Drawable drawable = progressBar.getIndeterminateDrawable();
        if (drawable != null) {
            drawable.setAlpha(150); // 0 means fully transparent, and 255 means fully opaque.
        }

        final FrameLayout.LayoutParams textProgressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textProgressParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        contentFrame.addView(progressBar, textProgressParams);

        ViewGroup rootContainer = contentFrame;
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            // If the host theme is NoActionBar (e.g. Compose or custom Toolbar apps),
            // provide a built-in Toolbar so the title and options menu are always accessible.
            if (activity.getActionBar() == null) {
                final LinearLayout wrapper = new LinearLayout(context);
                wrapper.setOrientation(LinearLayout.VERTICAL);
                wrapper.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));

                final Toolbar toolbar = new Toolbar(context);
                toolbar.setId(ID_TOOLBAR);
                final TypedValue typedValue = new TypedValue();
                int actionBarHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(
                            typedValue.data,
                            context.getResources().getDisplayMetrics()
                    );
                }
                final LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        actionBarHeight
                );
                wrapper.addView(toolbar, toolbarParams);

                final LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                );
                wrapper.addView(contentFrame, contentParams);

                activity.setActionBar(toolbar);
                rootContainer = wrapper;
            }
        }

        applySystemBarInsets(rootContainer);
        return rootContainer;
    }

    /**
     * Apply system bar and display cutout window insets as padding on the given view.
     */
    public static void applySystemBarInsets(final View view) {
        if (view == null) {
            return;
        }
        final int initialLeft = view.getPaddingLeft();
        final int initialTop = view.getPaddingTop();
        final int initialRight = view.getPaddingRight();
        final int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            final Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(
                    initialLeft + insets.left,
                    initialTop + insets.top,
                    initialRight + insets.right,
                    initialBottom + insets.bottom
            );
            return windowInsets;
        });
    }

    /**
     * Set the ProgressIndicator visibility.<br>
     * PS: Be sure to use GuiHelper.createAndAddProgressIndicator first
     */
    public static void setProgressIndicatorVisibility(final Activity activity, final boolean isVisible) {
        if (activity == null) {
            return;
        }
        final View progressIndicator = activity.findViewById(ID_PROGRESS_INDICATOR);
        if (progressIndicator == null) {
            return;
        }
        progressIndicator.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public static void enforceDeviceDefaultTheme(final Activity activity) {
        if (activity == null) {
            return;
        }
//        if (Build.VERSION.SDK_INT >= VERSION_CODE_ICE_CREAM_SANDWICH) {
//            // If we are API level 14 or higher then use Theme_DeviceDefault_Dialog
//            activity.setTheme(THEME_DEVICEDEFAULT_LIGHT);
//        } else if (Build.VERSION.SDK_INT >= VERSION_CODE_HONEYCOMB) {
//            // If we are below API level 14 but higher than 11 then show Holo
//            activity.setTheme(THEME_HOLO_LIGHT);
//        }
        // For lower levels use whatever the app sets
    }
}
