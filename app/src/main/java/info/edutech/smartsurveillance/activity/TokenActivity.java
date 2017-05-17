package info.edutech.smartsurveillance.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.model.DataServer;
import info.edutech.smartsurveillance.model.ValidationServer;
import info.edutech.smartsurveillance.service.ServerService;
import info.edutech.smartsurveillance.util.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenActivity extends AppCompatActivity {
    CoordinatorLayout coorLayout;
    EditText txtToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
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
        coorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        txtToken = (EditText)findViewById(R.id.txtToken);
    }

    public void verify(final View view){
        String token = txtToken.getText().toString().trim();

        if(token.equals("")) {
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Please insert token!", Snackbar.LENGTH_LONG);
            snackbar.show();
            txtToken.requestFocus();
        }
        else{
            view.setEnabled(false);
            Call<ValidationServer> callServer = ServerService.service.verify(token);
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
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                        else{
                            Snackbar snackbar = Snackbar
                                    .make(coorLayout, serverResponse.getError(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            view.setEnabled(true);
                        }
                    }
                    else{
                        Snackbar snackbar = Snackbar
                                .make(coorLayout, "Response failed!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        view.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ValidationServer> call, Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(coorLayout, "Failed to connect to server!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    view.setEnabled(true);
                }
            });

        }
    }
}
