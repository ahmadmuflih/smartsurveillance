package info.edutech.smartsurveillance.service;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.edutech.smartsurveillance.activity.GalleryActivity;
import info.edutech.smartsurveillance.activity.MainActivity;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.Capture;
import info.edutech.smartsurveillance.util.NotificationUtils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String url = Config.getBaseUrl(getApplicationContext());
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    Realm realm;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");
            String type = data.getString("type");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = url+data.getString("image");
            String timestamp = data.getString("timestamp");



            Log.e(TAG, "type: " + type);
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);



            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray


                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)){
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("message", message);
                    if(type.equals("flame")){
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    }
                } else {
                    Intent resultIntent = new Intent(getApplicationContext(), GalleryActivity.class);
                    resultIntent.putExtra("message", message);
                    // image is present, show notification with image
                    if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                        final Bitmap bitmap = getBitmapFromURL(imageUrl);
                        if (bitmap != null) {
                            if(type.equals("capture")) {
                                Log.e(TAG, "Capturing");
                                JSONObject payload = new JSONObject(data.getString("payload"));
                                final int id = payload.getInt("id");
                                final String url = payload.getString("url");
                                final String imageName = payload.getString("imageName");
                                final String date=payload.getString("date");
                                final String finalImageName = imageName;
                                final String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + finalImageName;
                                new Thread(new Runnable() {
                                    public void run() {
                                        FileOutputStream out;
                                        try {
                                            out = getApplicationContext().openFileOutput(finalImageName, Context.MODE_PRIVATE);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                                            out.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }).start();

                                realm = Realm.getDefaultInstance();
                                Capture newCapture = new Capture(id,url,imageName,toDate(date));
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(newCapture);
                                realm.commitTransaction();
                                Log.d(TAG, "Saved to realm");

                            }
                        }
                        Log.e(TAG, "Sending Notification");
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, bitmap);
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, Bitmap bitmap) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, bitmap);
    }

    public Date toDate(String dateString) {
        Date date = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = formatter.parse(dateString);
            Log.e("Print result: ", String.valueOf(date));

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return date;
    }
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void saveToRealm(JSONObject payload, String type){
        if(type.equals("capture")){
            int id = 0;
            try {
                id = payload.getInt("id");
                String url = payload.getString("url");
                String imageName = payload.getString("imageName");
                String date=payload.getString("date");
                String path=getApplicationContext().getFilesDir().getAbsolutePath() + "/" + imageName;
                realm = Realm.getDefaultInstance();
                RealmResults<Capture> capture = realm.where(Capture.class).equalTo("id", id).findAll();
                if(capture.size()==0){
                    Capture newCapture = new Capture(id,url,imageName,toDate(date));
                    realm.beginTransaction();
                    realm.copyToRealm(newCapture);
                    realm.commitTransaction();
                    Log.d(TAG, "Saved to realm");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}