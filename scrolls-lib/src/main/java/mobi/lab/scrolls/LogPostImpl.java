package mobi.lab.scrolls;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

import mobi.lab.scrolls.tools.FileCompressor;
import mobi.lab.scrolls.tools.LogHelper;
import mobi.lab.scrolls.tools.Util;

public class LogPostImpl extends LogPost {

    /**
     * Sets the default values for model, application version.
     * Also provide FileProvider authority
     * Must be called before any posting can be done.
     *
     * @param context               Context
     * @param fileProviderAuthority File Provider Authority
     */
    public static void configure(final Context context, final String fileProviderAuthority) {
        LogPost.setDeviceModel(LogHelper.getDeviceInfo());
        LogPost.setVersion(Util.getAppVersionName(context));
        LogPost.setFileProviderAuthority(fileProviderAuthority);
        LogPost.setImplementation(LogPostImpl.class);

    }

    @Override
    public void post(@NonNull Context context, @NonNull final String callbackTag, @Nullable final String content, @Nullable final File originalAttachment, @Nullable LogPostListener l) {
        this.setListener(l);
        final Handler handler = new Handler();
        new Thread(() -> doPostSync(context, callbackTag, content, originalAttachment, handler)).start();
    }

    private void doPostSync(@NonNull Context context, @NonNull String callbackTag, @Nullable String content, @Nullable File originalAttachment, Handler handler) {
        try {
            String message = content;
            File targetAttachment = compressAttachmentIfNeeded(originalAttachment);
            message = createLogCatMessageIfNeeded(context, message);

            Uri uri = createUriIfNeeded(context, targetAttachment);
            sendEmail(context, createMessageWithHeader(message), uri, isCompressedFile(targetAttachment));
            handler.post(() -> {
                if (listener != null) {
                    listener.onLogPostSuccess(callbackTag, content, originalAttachment);
                }
            });
        } catch (Throwable e) {
            handler.post(() -> {
                if (listener != null) {
                    listener.onLogPostFailed(callbackTag, e);
                }
            });
        }
    }

    private File compressAttachmentIfNeeded(File originalAttachment) {
        if (!compressLogFile || originalAttachment == null || isCompressedFile(originalAttachment)) {
            return originalAttachment;
        }
        final File targetFile = FileCompressor.createCompressedFileCandidateFromUncompressedFilePath(originalAttachment);
        if (targetFile.equals(originalAttachment)) {
            return originalAttachment;
        }
        final boolean success = FileCompressor.compressFiles(new File[]{originalAttachment}, targetFile);
        return success ? targetFile : originalAttachment;
    }

    private boolean isCompressedFile(@Nullable File file) {
        return file != null && file.getName().toLowerCase(Locale.ROOT).endsWith(LogImplFile.getCompressedLogFileExtension());
    }

    private Uri createUriIfNeeded(@NonNull Context context, @Nullable File attachment) {
        if (attachment != null) {
            return FileProvider.getUriForFile(context, fileProviderAuthority, attachment);
        }
        return null;
    }

    private String createLogCatMessageIfNeeded(@NonNull Context context, String message) {
        if (TextUtils.equals(type, LogPost.LOG_TYPE_LOGCAT)) {
            InputStream is = LogHelper.getLogcatStream(context, "time", "main", "V");
            message = LogHelper.logcatStreamToString(is);
            // TODO: Append?
        }
        return message;
    }

    private void sendEmail(Context context, String message, Uri attachment, boolean isCompressedAttachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, createTitle(context));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (attachment != null) {
            intent.setType(isCompressedAttachment ? "application/zip" : "application/octet-stream");
            intent.putExtra(Intent.EXTRA_STREAM, attachment);
            intent.setClipData(ClipData.newRawUri(null, attachment));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setType("text/plain");
        }

        Intent chooserIntent = Intent.createChooser(intent, context.getString(R.string.title_email_chooser));
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(chooserIntent);
    }

    private StringBuilder createHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: ").append(version)
                .append("\nDeviceModel: ").append(deviceModel)
                .append("\nLogType: ").append(type);
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
