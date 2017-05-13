package info.edutech.smartsurveillance.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.hardwarestate.Alarm;
import info.edutech.smartsurveillance.hardwarestate.Flame;

/**
 * Created by Baso on 4/25/2017.
 */
public class GasFragment extends Fragment {
    ImageView gasImg, gasStatusImg;
    TextView gasStatus;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gas,container,false);
        gasImg = (ImageView) v.findViewById(R.id.img_gas);
        gasStatusImg = (ImageView) v.findViewById(R.id.img_status);
        gasStatus = (TextView) v.findViewById(R.id.gas_status);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Config.getToken());
        hardwareStateChangeListener();
        return v;
    }
    void hardwareStateChangeListener() {
        mFirebaseDatabase.child("flame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Flame stateFlame = dataSnapshot.getValue(Flame.class);
                if (stateFlame != null) {
                    if(stateFlame.getState()){
                        try {
                            gasImg.setImageResource(R.drawable.gas_safe);
                            gasStatusImg.setImageResource(R.drawable.save);
                            gasStatus.setText(getResources().getText(R.string.gas_safe));
                            gasStatus.setTextColor(Color.parseColor("#00897B"));
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            gasImg.setImageResource(R.drawable.gas_warning);
                            gasStatusImg.setImageResource(R.drawable.warning);
                            gasStatus.setText(getResources().getText(R.string.gas_leak));
                            gasStatus.setTextColor(Color.parseColor("#F1543F"));
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
