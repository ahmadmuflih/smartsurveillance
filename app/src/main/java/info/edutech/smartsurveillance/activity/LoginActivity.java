package info.edutech.smartsurveillance.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.DataUser;
import info.edutech.smartsurveillance.model.ValidationUser;
import info.edutech.smartsurveillance.service.APIService;
import info.edutech.smartsurveillance.util.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private int REGISTER_RESULT=0001;
    CoordinatorLayout coorLayout;
    EditText txtPhone, txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        txtPhone = (EditText)findViewById(R.id.txtPhone);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        /*
        Drawable drawable;

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            drawable = getResources().getDrawable(R.drawable.ic_phone, getTheme());
        } else {
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_phone, getTheme());
        }

        txtPhone.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        Drawable drawable2;

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            drawable2 = getResources().getDrawable(R.drawable.ic_lock, getTheme());
        } else {
            drawable2 = VectorDrawableCompat.create(getResources(), R.drawable.ic_lock, getTheme());
        }

        txtPassword.setCompoundDrawablesWithIntrinsicBounds(drawable2, null, null, null);
        */

        String token = Config.getToken();
        if(token!=null)
            FirebaseMessaging.getInstance().subscribeToTopic(token);
    }
    public void register(View view){
        startActivityForResult(new Intent(this,RegisterActivity.class),REGISTER_RESULT);
    }
    public void login(final View view){
        String phone = txtPhone.getText().toString().trim();
        String password = txtPassword.getText().toString();
        if(phone.equals("") || password.equals("")){
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Phone number or password can not be empty!", Snackbar.LENGTH_LONG);
            snackbar.show();
            if(phone.equals(""))
                txtPhone.requestFocus();
            else
                txtPassword.requestFocus();
        }
        else {
            Call<ValidationUser> loginCall = APIService.service.login(phone,password);
            loginCall.enqueue(new Callback<ValidationUser>() {
                @Override
                public void onResponse(Call<ValidationUser> call, Response<ValidationUser> response) {
                    if(response.isSuccessful()){
                        ValidationUser serverResponse = response.body();
                        if(serverResponse.getStatus().equals("success")){
                            DataUser data = serverResponse.getData();
                            Preferences.setStringPreferences("nama",data.getName(),getApplicationContext());
                            Preferences.setStringPreferences("no_hp",data.getPhoneNumber(),getApplicationContext());
                            Preferences.setIntPreferences("id",Integer.parseInt(data.getId()),getApplicationContext());
                            Preferences.setStringPreferences("private_key",data.getPrivateKey(),getApplicationContext());
                            Preferences.setBooleanPreferences("login",true,getApplicationContext());
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                public void onFailure(Call<ValidationUser> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Login failed, check your internet connectionb ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                Toast.makeText(LoginActivity.this, "Berhasil register", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(LoginActivity.this, "Gagal register", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
