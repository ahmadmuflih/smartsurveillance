package info.edutech.smartsurveillance.fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.List;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.hardwarestate.Flame;
import info.edutech.smartsurveillance.model.User;
import info.edutech.smartsurveillance.model.UserService;
import info.edutech.smartsurveillance.service.APIService;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class HomeFragment extends Fragment {
    BottomBar mBottomBar;
    Fragment lockFragment, smsFragment, gasFragment, alarmFragment, cameraFragment;
    Realm realm;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    TextView txtStatus;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,new LockFragment()).commit();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference(Config.getToken());

        syncUser();
        hardwareStateChangeListener();
        txtStatus = (TextView)view.findViewById(R.id.txtStatus);
        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);
        mBottomBar.setDefaultTab(R.id.button3);
        final BottomBarTab tab = mBottomBar.getTabWithId(R.id.button4);
        tab.setBadgeCount(3);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Fragment fragment = null;
                switch (tabId){

                    case R.id.button1:
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Gas Status");
                        txtStatus.setText(getResources().getText(R.string.status_gas));
                        if(gasFragment==null)
                            gasFragment = new GasFragment();
                        fragment=gasFragment;
                        break;
                    case R.id.button2:
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Send SMS");
                        txtStatus.setText(getResources().getText(R.string.status_sms));
                        if(smsFragment==null)
                            smsFragment = new SMSFragment();
                        fragment=smsFragment;
                        break;
                    case R.id.button3:
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Lock");
                        txtStatus.setText(getResources().getText(R.string.status_lock));
                        if(lockFragment==null)
                            lockFragment = new LockFragment();
                        fragment=lockFragment;
                        break;
                    case R.id.button4:
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Camera");
                        txtStatus.setText(getResources().getText(R.string.status_camera));
                        if(cameraFragment==null)
                            cameraFragment = new CameraFragment();
                        fragment=cameraFragment;
                        tab.removeBadge();
                        break;
                    case R.id.button5:
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Alarm");
                        txtStatus.setText(getResources().getText(R.string.status_alarm));
                        if(alarmFragment==null)
                            alarmFragment = new AlarmFragment();
                        fragment=alarmFragment;
                        break;
                }
                if(fragment!=null){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
                }
            }
        });
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    private void syncUser(){
        Call<UserService> userServiceCall = APIService.service.getUsers(Config.getPrivateKey());
        userServiceCall.enqueue(new Callback<UserService>() {
            @Override
            public void onResponse(Call<UserService> call, Response<UserService> response) {
                if(response.isSuccessful()){
                    UserService userResponse = response.body();
                    if(userResponse.getStatus().equals("success")){
                        final List<User> users = userResponse.getData();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(users);
                            }
                        }, new Realm.Transaction.OnSuccess(){

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(), userResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Gagal!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserService> call, Throwable t) {
                //Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void hardwareStateChangeListener() {
        mFirebaseDatabase.child("flame").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Flame stateFlame = dataSnapshot.getValue(Flame.class);
                if (stateFlame != null) {
                    if(!stateFlame.getState()){
                        try {
                            BottomBarTab tab = mBottomBar.getTabWithId(R.id.button1);
                            tab.setBadgeCount(1);
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            BottomBarTab tab = mBottomBar.getTabWithId(R.id.button1);
                            tab.removeBadge();
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
