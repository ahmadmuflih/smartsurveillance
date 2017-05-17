package info.edutech.smartsurveillance.app;

import android.content.Context;

import info.edutech.smartsurveillance.util.Preferences;

/**
 * Created by Baso on 2/10/2017.
 */
public class Config {

    private static String WIFI_NAME = "";
    private static String TOKEN = "token";
    private static String PRIVATE_KEY = "8yTLJV9VYU";
    private static String MAIN_SERVER = "http://192.168.0.104/raspi/";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static String getBaseUrl(Context context){

        return Preferences.getStringPreferences("server",null,context);
    }
    public static String getPrivateKey(){
        return PRIVATE_KEY;
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getMainServer() {
        return MAIN_SERVER;
    }
}