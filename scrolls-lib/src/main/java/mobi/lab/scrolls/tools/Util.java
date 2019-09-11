package mobi.lab.scrolls.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return versionName;
    }

    public static String getAppName(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return info.loadLabel(context.getPackageManager()).toString();
    }

    public static String getCurrentTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ssZ", Locale.US);
        return dateFormat.format(new Date());
    }

}
