package mobi.lab.scrolls;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

import mobi.lab.scrolls.tools.LogHelper;
import mobi.lab.scrolls.tools.Util;

public class LogPostImpl extends LogPost {

    /**
     * Sets the default values for device id, model, application version.
     * Also provide FileProvider authority
     * Must be called before any posting can be done.
     *
     * @param context
     */
    public static void configure(Context context, String fileProviderAuthority) {
        LogPost.setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        LogPost.setDeviceModel(LogHelper.getDeviceInfo());
        LogPost.setVersion(Util.getAppVersionName(context));
        LogPost.setFileProviderAuthority(fileProviderAuthority);
        LogPost.setImplementation(LogPostImpl.class);

    }

    @Override
    public String post(Context context, String content, File attachment) {
        Uri uri = null;
        if (attachment != null) {
            uri = FileProvider.getUriForFile(context, fileProviderAuthority, attachment);
        }
        sendEmail(context, createMessageWithHeader(content), uri);
        return null;
    }

    private void sendEmail(Context context, String message, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, createTitle(context));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (attachment != null) {
            intent.putExtra(Intent.EXTRA_STREAM, attachment);
        }

        Intent chooserIntent = Intent.createChooser(intent, context.getString(R.string.title_email_chooser));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(chooserIntent);
    }

    private StringBuilder createHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: ").append(version)
                .append("\nDeviceModel: ").append(deviceModel)
                .append("\nDeviceId: ").append(deviceId)
                .append("\nLogType: ").append(type);

        if (tags != null) {
            sb.append("\nTags: ").append(TextUtils.join(", ", tags));
        }
        return sb;
    }

    private String createMessageWithHeader(String message) {
        StringBuilder sb = createHeader();
        if (!TextUtils.isEmpty(message)) {
            sb.append("\n\n").append(message);
        }
        return sb.toString();
    }

    private String createTitle(Context context) {
        return "Log: " +
                Util.getAppName(context) +
                " " +
                version +
                " " +
                Util.getCurrentTimeString();
    }
}
