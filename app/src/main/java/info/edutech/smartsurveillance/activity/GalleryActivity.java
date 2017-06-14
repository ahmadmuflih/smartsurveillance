package info.edutech.smartsurveillance.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.messaging.FirebaseMessaging;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.adapter.ImageAdapter;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.Capture;
import info.edutech.smartsurveillance.model.CaptureService;
import info.edutech.smartsurveillance.service.APIService;
import info.edutech.smartsurveillance.util.NotificationUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ImageAdapter.OnPhotoSelectedListener {

    private static final String TAG = GalleryActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId,txtDate;
    private PhotoView imageView;
    private RecyclerView recyclerView;
    private final String BASE_URL=Config.getBaseUrl(MyApplication.getAppContext());
    Realm realm;
    ImageAdapter imageAdapter;
    RealmResults<Capture> capture;
    Capture selectedItem;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Galeri");

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtDate = (TextView) findViewById(R.id.tanggal);
        imageView = (PhotoView) findViewById(R.id.foto);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        realm = Realm.getDefaultInstance();
        capture = realm.where(Capture.class).findAllSorted("id", Sort.DESCENDING);

        // Display
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        //

        imageAdapter = new ImageAdapter(capture,getApplicationContext(),this,width);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setFocusable(false);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = Config.getToken();
                    if(token!=null)
                        FirebaseMessaging.getInstance().subscribeToTopic(token);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
        if(capture.size()!=0) {
            setImageView(capture.get(0));
        }
        displayFirebaseRegId();
        syncCapture();
    }
    
    private void syncCapture(){
        Call<CaptureService> captureServiceCall = APIService.service.getCaptures(Config.getPrivateKey());
        captureServiceCall.enqueue(new Callback<CaptureService>() {
            @Override
            public void onResponse(Call<CaptureService> call, Response<CaptureService> response) {
                if(response.isSuccessful()){
                    CaptureService captureResponse = response.body();
                    if(captureResponse.getStatus().equals("success")){
                        final List<Capture> captures = captureResponse.getData();

                        if(captures.size()!=0) {
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(captures);

                                }
                            }, new Realm.Transaction.OnSuccess() {

                                @Override
                                public void onSuccess() {
                                    capture = realm.where(Capture.class).findAllSorted("id", Sort.DESCENDING);
                                    if(capture!=null&capture.size()>0){
                                        menu.findItem(R.id.share).setVisible(true);
                                    }
                                    else{
                                        menu.findItem(R.id.share).setVisible(false);
                                    }
                                    setImageView(captures.get(0));
                                    imageAdapter.notifyDataSetChanged();
                                    imageAdapter.setDisplays(0);
                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), captureResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CaptureService> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed! Check yourr internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setImageView(Capture capture){
        selectedItem = capture;
        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + capture.getImageName());
        if(file.exists()){
            Picasso.with(getApplicationContext())
                    .load(file)
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(imageView);
        }
        else{
            Picasso.with(getApplicationContext())
                    .load(BASE_URL+capture.getUrl())
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(imageView);
        }
        txtDate.setText(capture.getDate().toString());
    }
    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId) && regId!=null)
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        if(capture!=null && capture.size()!=0) {
            setImageView(capture.get(0));
        }
        else{
            menu.findItem(R.id.share).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        if (id == R.id.share) {
            Toast.makeText(getApplicationContext(), "Share : "+selectedItem.getImageName(), Toast.LENGTH_SHORT).show();
            try {
                String path = getFilesDir().getAbsolutePath() + "/" + selectedItem.getImageName();
                File f=new File(path);
                if(f.exists()) {
                    Bitmap bitmapToShare = BitmapFactory.decodeStream(new FileInputStream(f));
                    File pictureStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File noMedia = new File(pictureStorage, ".nomedia");
                    if (!noMedia.exists())
                        noMedia.mkdirs();
                    File file = new File(noMedia, "shared_image.png");
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmapToShare.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Gambar pada "+selectedItem.getDate().toString());
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, "Share to.."));
                }
                else{
                    Toast.makeText(GalleryActivity.this, "Wait.. image is being downloaded", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onSelected(Capture capture) {

        setImageView(capture);
    }

}