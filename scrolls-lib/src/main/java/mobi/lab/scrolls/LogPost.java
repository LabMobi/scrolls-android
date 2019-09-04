package mobi.lab.scrolls;

import android.content.Context;

import java.io.File;

/**
 * Version 2.0 (two-point-oh) of the LogPost class.
 * <p>
 * Has no default/dummy impl, you'll need to use an integration
 * library or roll your own.
 *
 * @author madis
 *         <p>
 *         Sample post:
 *         <p>
 *         ➜  bin
 *         <p>
 *         POST /groups/madis-pink-s-group--2/projects/1/postings.json,
 *         {"platform":"android","version":"1.0.0","secret":"c6CsRcOyx3Vqk6gbTIJr84Lg9LGgt6AqXS4yksS9GloqKxcK","metadata":{"random":"data"},"tags":[{"name":"crash"},{"name":"madis"}]} -H "Content-Type: application/json"
 *         {"id":320,"platform":"android","device_model":null,"device_id":null,"version":"1.0.0","revision":null,"metadata":{"random":"data"},"url":"http://localhost.madisp.com:3000/groups/madis-pink-s-group--2/projects/1/postings/320","tags":[{"id":1,"name":"crash"},{"id":3,"name":"madis"}]}
 *         <p>
 *         After that pull a PUT request to the returned URI with the raw file contents
 */
public abstract class LogPost {

    /**
     * Use this type if posting logs from some file log implementation
     * Needs {@link LogImplFile#init(java.io.File)} to be called before posting.
     */
    public static String LOG_TYPE_MOBILE = "mobilog";
    /**
     * Use this log type if posting full Android logcat logs
     */
    public static String LOG_TYPE_LOGCAT = "logcat";

    /**
     * Tag used for test tag
     */
    public static String LOG_TAG_TEST = "test";
    /**
     * Tag used for app crash
     */
    public static String LOG_TAG_CRASH = "crash";
    /**
     * Tag that can be used to denote an automatic post
     */
    public static String LOG_TAG_AUTOMATIC_POST = "autopost";

    /*
     * Optional fields
     */
    protected static String deviceModel;
    protected static String deviceId;
    protected static String version;
    protected static String fileProviderAuthority;
    protected String type;
    protected String[] tags;

    private static Class logPostImplementation;

    /**
     * Sets the device model string
     *
     * @param deviceModel
     */
    public static void setDeviceModel(String deviceModel) {
        LogPost.deviceModel = deviceModel;
    }

    /**
     * Sets the unique device id
     *
     * @param deviceId
     */
    public static void setDeviceId(String deviceId) {
        LogPost.deviceId = deviceId;
    }

    /**
     * Sets the application version
     *
     * @param version
     */
    public static void setVersion(String version) {
        LogPost.version = version;
    }

    /**
     * Sets the FileProvider authority used to create log file uris
     *
     * @param authority
     */
    public static void setFileProviderAuthority(String authority) {
        LogPost.fileProviderAuthority = authority;
    }


    /**
     * Sets the LogPost implementation for posting
     *
     * @param clazz
     */
    public static void setImplementation(Class clazz) {
        logPostImplementation = clazz;
    }

    /**
     * Get a new instance of LogPost.
     *
     * @return A new instance of LogPost implementation
     */
    public static LogPost getInstance() {
        if (logPostImplementation == null) {
            logPostImplementation = LogPostImpl.class;
        }
        try {
            final LogPost logPost = (LogPost) logPostImplementation.newInstance();
            return logPost;
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getClass() + " " + e.getMessage());
        }
    }

    /**
     * Sets the log type
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets custom tags associated with the posted log
     *
     * @param tags
     */
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * Post log data from InputStream
     *
     * @param content The string content to post. Most likely a logcat log
     * @param attachment The file to post
     * @return Null if post unsuccessful
     */
    public abstract String post(Context context, String content, File attachment);
}
