package mobi.lab.scrolls.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
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

    public static String inputStreamToString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] buf = new byte[4096]; // not always 4k, but most of the times
            while (in.read(buf) != -1) {
                sb.append(new String(buf, "UTF-8"));
            }
        } catch (IOException ioe) {
            Log.w("LogPost", "logpost failed", ioe);
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
        return sb.toString();
    }

    public static String getCurrentTimeString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ssZ", Locale.US);
        return dateFormat.format(new Date());
    }

}
