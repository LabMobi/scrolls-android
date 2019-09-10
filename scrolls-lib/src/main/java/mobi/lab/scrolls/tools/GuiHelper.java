package mobi.lab.scrolls.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

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

    /**
     * Create and add the ProgressIndicator to the activity layout
     */
    public static ViewGroup createAndAddProgressIndicator(final Context context, final ViewGroup viewContainer) {
        if (context == null || viewContainer == null) {
            return viewContainer;
        }
        ViewGroup newViewContainer = viewContainer;

        // Put the current stuff inside a FrameLayout if it isn't yet
        if (!(newViewContainer instanceof FrameLayout)) {

            final FrameLayout frame = new FrameLayout(context);
            frame.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
            newViewContainer = frame;
            newViewContainer.addView(viewContainer);
        }

        // Add the progress indicator
        final ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(ID_PROGRESS_INDICATOR);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        final Drawable drawable = progressBar.getIndeterminateDrawable();
        drawable.setAlpha(150); // 0 means fully transparent, and 255 means fully opaque.

        final FrameLayout.LayoutParams textProgressParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        textProgressParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        newViewContainer.addView(progressBar, textProgressParams);

        return newViewContainer;
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
