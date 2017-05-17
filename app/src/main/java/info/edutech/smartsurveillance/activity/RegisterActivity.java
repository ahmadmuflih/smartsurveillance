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
    }

    public void register(View view){
        String phone = txtPhone.getText().toString().trim();
        String name = txtName.getText().toString().trim();
        String password = txtPassword.getText().toString();
        if(phone.equals("") || name.equals("") || password.equals("")){
            Snackbar snackbar = Snackbar
                    .make(coorLayout, "Phone number or name or password can not be blank!", Snackbar.LENGTH_LONG);
            snackbar.show();
            if(phone.equals(""))
                txtPhone.requestFocus();
            else if(name.equals(""))
                txtName.requestFocus();
            else if(password.equals(""))
                txtPassword.requestFocus();
        }
        else{
            Intent intent=new Intent();
            setResult(0001,intent);
            finish();
        }
    }
}
