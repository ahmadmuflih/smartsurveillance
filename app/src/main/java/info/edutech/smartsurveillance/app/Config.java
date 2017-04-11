package info.edutech.smartsurveillance.app;

/**
 * Created by Baso on 2/10/2017.
 */
public class Config {
    private static String IP_ADDRESS = "http://192.168.0.99/smartsurveillance/";
    private static String DOMAIN = "edutechteam.tk/smartsurveillance/";
    private static String WIFI_NAME = "";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static String getBaseUrl(){
        return IP_ADDRESS;
    }
}