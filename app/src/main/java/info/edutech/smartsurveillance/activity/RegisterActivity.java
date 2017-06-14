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
import android.widget.Toast;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    CoordinatorLayout coorLayout;
    EditText txtPhone, txtPassword, txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        txtName = (EditText)findViewById(R.id.txtName);
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
        Drawable drawable3;

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            drawable3 = getResources().getDrawable(R.drawable.ic_account, getTheme());
        } else {
            drawable3 = VectorDrawableCompat.create(getResources(), R.drawable.ic_account, getTheme());
        }

        txtName.setCompoundDrawablesWithIntrinsicBounds(drawable3, null, null, null);
        */
    }

    public void register(final View view){
        String phone = txtPhone.getText().toString().trim();
        String name = txtName.getText().toString().trim();
        String password = txtPassword.getText().toString();
        if(phone.equals("") || name.equals("") || password.equals("")){
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Phone number or name or password can not be empty!", Snackbar.LENGTH_LONG);
            snackbar.show();
            if(phone.equals(""))
                txtPhone.requestFocus();
            else if(name.equals(""))
                txtName.requestFocus();
            else if(password.equals(""))
                txtPassword.requestFocus();
        }
        else if(phone.length()<10){
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Phone number is not valid!", Snackbar.LENGTH_LONG);
            snackbar.show();
            txtPhone.requestFocus();
        }
        else if(password.length()<6){
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Password should contain at least 6 characters", Snackbar.LENGTH_LONG);
            snackbar.show();
            txtPassword.requestFocus();
        }
        else{
            view.setEnabled(false);
            Call<Validation> registerCall = APIService.service.register(name,phone,password);
            registerCall.enqueue(new Callback<Validation>() {
                @Override
                public void onResponse(Call<Validation> call, Response<Validation> response) {
                    if(response.isSuccessful()){
                        Validation serverResponse = response.body();
                        if(serverResponse.getStatus().equals("success")){
                            Toast.makeText(RegisterActivity.this, "Register is successful!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            setResult(0001,intent);
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
                public void onFailure(Call<Validation> call, Throwable t) {

                }
            });

        }
    }
}
