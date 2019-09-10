package mobi.lab.scrolls.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @WorkerThread
    public static void compressFiles(@NonNull final File[] targetFiles, @NonNull final File compressedFile) {
        try {
            final int bufferSize = 4096;
            BufferedInputStream origin;
            final FileOutputStream dest = new FileOutputStream(compressedFile);
            final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte[] data = new byte[bufferSize];
            for (File file : targetFiles) {
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, bufferSize);

                final ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, bufferSize)) != -1) {
                    out.write(data, 0, count);
                }
                LogHelper.closeStream(origin);
            }
            LogHelper.closeStream(out);
        } catch (Exception e) {
            Log.w("LogPost", "compressFiles failed", e);
        }
    }

}
