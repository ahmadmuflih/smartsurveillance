package info.edutech.smartsurveillance.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;


import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.fragment.AboutFragment;
import info.edutech.smartsurveillance.fragment.HomeFragment;
import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.fragment.SettingsFragment;
import info.edutech.smartsurveillance.model.DataServer;
import info.edutech.smartsurveillance.model.ValidationServer;
import info.edutech.smartsurveillance.service.ServerService;
import info.edutech.smartsurveillance.util.NotificationUtils;
import info.edutech.smartsurveillance.util.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String fragmentStatus;
    TextView textNama, textNoHP;
    Fragment fragment;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);



                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        Call<ValidationServer> callServer = ServerService.service.verify(Preferences.getStringPreferences("token","",getApplicationContext()));
        callServer.enqueue(new Callback<ValidationServer>() {
            @Override
            public void onResponse(Call<ValidationServer> call, Response<ValidationServer> response) {
                if(response.isSuccessful()){
                    ValidationServer serverResponse = response.body();
                    if(serverResponse.getStatus().equals("success")){
                        DataServer data = serverResponse.getData();
                        Preferences.setStringPreferences("token",data.getToken(),getApplicationContext());
                        Preferences.setStringPreferences("server",data.getIpAddress(),getApplicationContext());
                        Preferences.setStringPreferences("domain",data.getDomain(),getApplicationContext());
                        Preferences.setStringPreferences("server_name",data.getNama(),getApplicationContext());
                    }
                    else{
                    }
                }
                else{
                }
            }

            @Override
            public void onFailure(Call<ValidationServer> call, Throwable t) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        textNama = (TextView)hView.findViewById(R.id.text_nama);
        textNoHP = (TextView)hView.findViewById(R.id.text_no_hp);
        navigationView.setNavigationItemSelectedListener(this);
        displayFragment(R.id.nav_home);

        String nama = Preferences.getStringPreferences("nama","",getApplicationContext());
        String no_hp = Preferences.getStringPreferences("no_hp","",getApplicationContext());
        setProfil(nama,no_hp);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayFragment(id);


        return true;
    }
    public void displayFragment(int id){
        fragment = null;
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_settings) {
            getSupportActionBar().setTitle("Settings");
            fragment = new SettingsFragment();
        } else if (id == R.id.nav_about) {
            getSupportActionBar().setTitle("About");
            fragment = new AboutFragment();
        }
        else if (id == R.id.nav_logut) {
            Preferences.setBooleanPreferences("login",false,getApplicationContext());
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }


        if (fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    public void setProfil(String nama, String noHp){
        textNama.setText(nama);
        textNoHP.setText(noHp);
    }

}
