package info.edutech.smartsurveillance.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Baso on 10/17/2016.
 */
public class Preferences {
    private static final String SHARED_PREF="smartsurveillance";
    public static void setStringPreferences(String prefName, String value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putString(prefName, value);
        editor.apply();
    }
    public static String getStringPreferences(String prefName,String type,Context context){
        String defaultValue="";

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getString(prefName, defaultValue);
    }
    public static void setIntPreferences(String prefName, int value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(prefName, value);
        editor.apply();
    }
    public static int getIntPreferences(String prefName,String type,Context context){
        int defaultValue=0;
        if(type.equals("setting")){
            if(prefName.equals("lock"))
                defaultValue=3;
            else if(prefName.equals("alarm"))
                defaultValue=3;
            else if(prefName.equals("camera"))
                defaultValue=2;
            else if(prefName.equals("notif"))
                defaultValue=3;
        }
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getInt(prefName, defaultValue);
    }
    public static void setBooleanPreferences(String prefName, boolean value, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putBoolean(prefName, value);
        editor.apply();
    }
    public static Boolean getBooleanPreferences(String prefName,boolean defaultValue, Context context){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        return preferences.getBoolean(prefName, defaultValue);
    }
}
