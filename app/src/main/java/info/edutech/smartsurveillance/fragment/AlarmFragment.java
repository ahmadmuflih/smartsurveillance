package info.edutech.smartsurveillance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.hardwarestate.Alarm;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 4/25/2017.
 */
public class AlarmFragment extends Fragment {
    SwitchButton switchAlarm1, switchAlarm2;
    ImageView imgAlarm1, imgAlarm2;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    boolean cekAlarm1=true,cekAlarm2=true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(switchAlarm1.isChecked())
            imgAlarm1.setImageResource(R.drawable.alarm_on);
        if(switchAlarm2.isChecked())
            imgAlarm2.setImageResource(R.drawable.alarm_on);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm,container,false);
        switchAlarm1 = (SwitchButton) v.findViewById(R.id.switch_alarm1);
        switchAlarm2 = (SwitchButton) v.findViewById(R.id.switch_alarm2);
        imgAlarm1 = (ImageView)v.findViewById(R.id.img_alarm1);
        imgAlarm2 = (ImageView)v.findViewById(R.id.img_alarm2);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Config.getToken());


        switchAlarm1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String action;
                if(isChecked) {
                    action="on";
                    try {
                        imgAlarm1.setImageResource(R.drawable.alarm_on);
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                }
                else{
                    action="off";
                    try {
                        imgAlarm1.setImageResource(R.drawable.alarm_off);
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }
                if(cekAlarm1) {
                    switchAlarm1.setEnabled(false);
                    Call<Validation> alarm1Call = APIService.service.setAlarm(Config.getPrivateKey(), "1", action);
                    alarm1Call.enqueue(new Callback<Validation>() {
                        @Override
                        public void onResponse(Call<Validation> call, Response<Validation> response) {
                            if (response.isSuccessful()) {
                                Validation data = response.body();
                                if (!data.getStatus().equals("success")) {
                                    Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Gagal!", Toast.LENGTH_SHORT).show();
                            }
                            switchAlarm1.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            switchAlarm1.setEnabled(true);
                            mFirebaseDatabase.child("alarm").child("alarm1").setValue(isChecked, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        switchAlarm1.setChecked(!isChecked);
                                    }
                                }
                            });
                        }
                    });
                }
                cekAlarm1=true;
            }
        });

        switchAlarm2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String action;
                if(isChecked) {
                    action="on";
                    try {
                        imgAlarm2.setImageResource(R.drawable.alarm_on);
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                }
                else{
                    action="off";
                    try {
                        imgAlarm2.setImageResource(R.drawable.alarm_off);
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }
                if(cekAlarm2) {
                    switchAlarm2.setEnabled(false);
                    Call<Validation> alarm2Call = APIService.service.setAlarm(Config.getPrivateKey(), "2", action);
                    alarm2Call.enqueue(new Callback<Validation>() {
                        @Override
                        public void onResponse(Call<Validation> call, Response<Validation> response) {
                            if (response.isSuccessful()) {
                                Validation data = response.body();
                                if (!data.getStatus().equals("success")) {
                                    Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Gagal!", Toast.LENGTH_SHORT).show();
                            }
                            switchAlarm2.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            switchAlarm2.setEnabled(true);
                            mFirebaseDatabase.child("alarm").child("alarm2").setValue(isChecked, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        switchAlarm2.setChecked(!isChecked);
                                    }
                                }
                            });
                        }
                    });
                }
                cekAlarm2=true;
            }
        });
        hardwareStateChangeListener();
        return v;
    }
    void hardwareStateChangeListener() {
        mFirebaseDatabase.child("alarm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alarm stateAlarm = dataSnapshot.getValue(Alarm.class);
                if (stateAlarm != null) {
                    if(switchAlarm1.isChecked()!=stateAlarm.getAlarm1()) {
                        cekAlarm1 = false;
                        switchAlarm1.setChecked(stateAlarm.getAlarm1());
                    }
                    if(switchAlarm2.isChecked()!=stateAlarm.getAlarm2()) {
                        cekAlarm2=false;
                        switchAlarm2.setChecked(stateAlarm.getAlarm2());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
