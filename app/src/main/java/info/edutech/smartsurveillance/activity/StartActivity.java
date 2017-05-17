package info.edutech.smartsurveillance.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.util.Preferences;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (Build.VERSION.SDK_INT < 16)//before Jelly Bean Versions
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else // Jelly Bean and up
        {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int ui = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(ui);

            //Hide actionbar
            ActionBar actionBar = getActionBar();
            if(actionBar!=null)
            actionBar.hide();
        }

        String token = Preferences.getStringPreferences("token",null,getApplicationContext());
        final Intent intent;
        if(token.equals("")) {

            intent = new Intent(this, TokenActivity.class);
        }
        else {
            Boolean login = Preferences.getBooleanPreferences("login",false,getApplicationContext());
            if(login)
                intent = new Intent(this, MainActivity.class);
            else
                intent = new Intent(this, LoginActivity.class);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000 );


    }
}
