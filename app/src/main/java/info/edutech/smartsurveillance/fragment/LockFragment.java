package info.edutech.smartsurveillance.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;


import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.hardwarestate.Lock;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 4/25/2017.
 */
public class LockFragment extends Fragment {
    TextView txtLock, statusLock;
    SwitchButton switchLock;
    ImageView imgLock;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    boolean cekLock = true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(switchLock.isChecked()) {
            txtLock.setText("Locked");
            statusLock.setText(getResources().getText(R.string.lock));
            imgLock.setImageResource(R.drawable.lock);
            txtLock.setTextColor(Color.parseColor("#00897B"));
            statusLock.setTextColor(Color.parseColor("#00897B"));
        }
        else {
            txtLock.setText("Unlocked");
            statusLock.setText(getResources().getText(R.string.unlock));
            imgLock.setImageResource(R.drawable.unlock);
            txtLock.setTextColor(Color.parseColor("#F1543F"));
            statusLock.setTextColor(Color.parseColor("#F1543F"));
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lock,container,false);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Config.getToken());
        txtLock = (TextView) v.findViewById(R.id.txt_lock);
        statusLock = (TextView) v.findViewById(R.id.status_lock);
        switchLock = (SwitchButton) v.findViewById(R.id.switch_lock);
        imgLock = (ImageView)v.findViewById(R.id.img_lock);

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String action;

                if(isChecked) {
                    action="on";
                    try {
                        txtLock.setText("Locked");
                        statusLock.setText(getResources().getText(R.string.lock));
                        imgLock.setImageResource(R.drawable.lock);
                        txtLock.setTextColor(Color.parseColor("#00897B"));
                        statusLock.setTextColor(Color.parseColor("#00897B"));
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }
                else{
                    action="off";
                    try {
                        txtLock.setText("Unlocked");
                        statusLock.setText(getResources().getText(R.string.unlock));
                        imgLock.setImageResource(R.drawable.unlock);
                        txtLock.setTextColor(Color.parseColor("#F1543F"));
                        statusLock.setTextColor(Color.parseColor("#F1543F"));
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                }
                if(cekLock) {
                    switchLock.setEnabled(false);
                    Call<Validation> setLockCall = APIService.service.setLock(Config.getPrivateKey(), action);
                    setLockCall.enqueue(new Callback<Validation>() {
                        @Override
                        public void onResponse(Call<Validation> call, Response<Validation> response) {
                            if (response.isSuccessful()) {
                                Validation data = response.body();
                                if (!data.getStatus().equals("success")) {
                                    Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                    switchLock.setChecked(!isChecked);
                                }
                            } else {
                                Toast.makeText(getActivity(), "Gagal!", Toast.LENGTH_SHORT).show();
                            }
                            switchLock.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            switchLock.setEnabled(true);

                            mFirebaseDatabase.child("lock").child("state").setValue(isChecked, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                }
                            });

                        }
                    });
                }
                cekLock=true;
            }
        });
        hardwareStateChangeListener();
        return v;
    }
    void hardwareStateChangeListener() {
        mFirebaseDatabase.child("lock").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lock stateLock = dataSnapshot.getValue(Lock.class);
                if (stateLock != null) {
                    if(stateLock.getState() != switchLock.isChecked()){
                        cekLock = false;
                        switchLock.setChecked(stateLock.getState());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
