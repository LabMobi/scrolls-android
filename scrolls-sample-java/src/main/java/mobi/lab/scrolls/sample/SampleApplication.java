package mobi.lab.scrolls.sample;

import android.app.Application;

import mobi.lab.scrolls.Log;
import mobi.lab.scrolls.LogDeleteImplAge;
import mobi.lab.scrolls.LogImplCat;
import mobi.lab.scrolls.LogImplComposite;
import mobi.lab.scrolls.LogImplFile;
import mobi.lab.scrolls.LogPost;
import mobi.lab.scrolls.LogPostBuilder;
import mobi.lab.scrolls.LogPostImpl;
import mobi.lab.scrolls.tools.LogHelper;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            /* Init Logging to go both to logcat and file when we have a debug build*/
            LogImplFile.init(getFilesDir(), new LogDeleteImplAge(LogDeleteImplAge.AGE_KEEP_3_DAYS));
            //LogImplFile.init(getFilesDir(), new LogDeleteImplCount(LogDeleteImplCount.COUNT_KEEP_ALL));
            LogImplComposite.init(new Class[]{LogImplCat.class, LogImplFile.class});
            Log.setImplementation(LogImplComposite.class);

            /* Configure posting to backend */
            LogPostImpl.configure(this, "mobi.lab.scrolls.provider");
        } else {
            /* On release builds (e.g. Google Play Store releases) lets just log errors */
            Log.setImplementation(LogImplCat.class);
            Log.setVerbosity(Log.VERBOSITY_LOG_ERRORS);
        }

        // lets use our new shiny configured log framework
        final Log log = Log.getInstance(this);

        // the above statement is equivalent to:
        // log = Log.getInstance(SampleApplication.class);
        // log = Log.getInstance("ee.mobi.scrolls.sample.SampleApplication");

        // Log some information
        log.d(LogHelper.getDeviceInfo());
        log.d(LogHelper.getDateTimeString());
        log.d(String.format("Application '%s' started", getString(getApplicationInfo().labelRes)));

        // Catch Exceptions thrown on the main thread and post logs
        // Intercepts exception handling, does its magic and then forwards the
        // exception to the default exception handler
        if (BuildConfig.DEBUG) {
            final Thread.UncaughtExceptionHandler defaultExceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {

                    // Log out the current exception
                    log.e(ex, "FATAL ERROR!");
                    // Start a new Log post with Activity
                    // With the debug build we've also initated LogImplFile
                    // If you want to use only LogImplCat, use LogPostBuilder(LogPost.LOG_TYPE_LOGCAT) constructor instead
                    LogPostBuilder builder = new LogPostBuilder();
                    builder.addTags(LogPost.LOG_TAG_CRASH)
                            .setConfirmEnabled(true)
                            .setShowResultEnabled(true)
                            .launchActivity(getApplicationContext());

                    // Call the system default handler
                    defaultExceptionHandler.uncaughtException(thread, ex);
                }
            });

            // the above statement is equivalent to:
            //LogHelper.setUncaughtExceptionHandler(getApplicationContext(), true, true, LogPost.LOG_TAG_CRASH);
        }
    }
}
