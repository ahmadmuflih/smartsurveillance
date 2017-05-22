package info.edutech.smartsurveillance.app;

import android.content.Context;
import android.util.Log;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.util.Preferences;

/**
 * Created by Baso on 2/10/2017.
 */
public class Config {

    private static String WIFI_NAME = "";
    private static String TOKEN = "token";
    private static String MAIN_SERVER = "http://192.168.7.107/raspi/";
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
        String url =  Preferences.getStringPreferences("server",null,context);
        Log.d("URL","URLNYA : "+url);
        return url;
    }
    public static String getPrivateKey(){
        return Preferences.getStringPreferences("private_key",null, MyApplication.getAppContext());
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getMainServer() {
        return MAIN_SERVER;
    }
}