package info.edutech.smartsurveillance.fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.activity.GalleryActivity;
import info.edutech.smartsurveillance.adapter.SMSKontakAdapter;
import info.edutech.smartsurveillance.model.User;
import info.edutech.smartsurveillance.model.UserService;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    ImageView btnLock, btnCamera, btnGallery, btnFire, btnAlarm, btnSMS;
    LinearLayout layoutMenu, layoutLock, layoutCamera, layoutAlarm, layoutSMS, layoutApi;
    TextView judul;

    ImageView iconLock, iconAlarm1, iconAlarm2;
    Switch switchLock, switchAlarm1,switchAlarm2;

    Button buttonCamera;

    Realm realm;
    AlertDialog selectContactDialog;
    EditText phoneNumbers, smsContent;
    ImageButton btnSend;
    ProgressBar progress;

    ArrayList<String> numbers = new ArrayList<>();
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
        btnLock = (ImageButton) view.findViewById(R.id.btn_lock);
        btnCamera = (ImageButton) view.findViewById(R.id.btn_camera);
        btnGallery = (ImageButton) view.findViewById(R.id.btn_galeri);
        btnFire = (ImageButton) view.findViewById(R.id.btn_fire);
        btnAlarm = (ImageButton) view.findViewById(R.id.btn_alarm);
        btnSMS = (ImageButton) view.findViewById(R.id.btn_sms);
        buttonCamera = (Button) view.findViewById(R.id.button_gambar);

        phoneNumbers = (EditText)view.findViewById(R.id.input_hp);
        smsContent = (EditText)view.findViewById(R.id.input_sms);
        btnSend = (ImageButton)view.findViewById(R.id.send_sms);
        progress = (ProgressBar)view.findViewById(R.id.progress);

        layoutMenu = (LinearLayout)view.findViewById(R.id.layout_menu);
        layoutLock = (LinearLayout)view.findViewById(R.id.layout_lock);
        layoutCamera = (LinearLayout)view.findViewById(R.id.layout_camera);
        layoutAlarm = (LinearLayout)view.findViewById(R.id.layout_alarm);
        layoutSMS = (LinearLayout)view.findViewById(R.id.layout_sms);
        layoutApi = (LinearLayout)view.findViewById(R.id.layout_api);

        judul = (TextView)view.findViewById(R.id.judul);

        btnGallery.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnFire.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);
        btnSMS.setOnClickListener(this);
        (view.findViewById(R.id.select_contact)).setOnClickListener(this);

        switchLock = (Switch) view.findViewById(R.id.switch_lock);
        switchAlarm1 = (Switch) view.findViewById(R.id.switch_alarm_1);
        switchAlarm2 = (Switch) view.findViewById(R.id.switch_alarm_2);

        iconLock = (ImageView)view.findViewById(R.id.icon_lock);
        iconAlarm1 = (ImageView)view.findViewById(R.id.icon_alarm_1);
        iconAlarm2 = (ImageView)view.findViewById(R.id.icon_alarm_2);

        syncUser();

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    iconLock.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock_open, null));
                    switchLock.setText("Pintu dibuka");
                }
                else{
                    iconLock.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_lock, null));
                    switchLock.setText("Pintu dikunci");
                }
            }
        });

        switchAlarm1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    iconAlarm1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alarm, null));
                    switchAlarm1.setText("Alarm 1 Bunyi");
                }
                else{
                    iconAlarm1.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alarm_off, null));
                    switchAlarm1.setText("Alarm 1 Mati");
                }
            }
        });
        switchAlarm2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Bitmap b;
                if(isChecked) {
                    iconAlarm2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alarm, null));
                    switchAlarm2.setText("Alarm 2 Bunyi");
                }
                else{
                    iconAlarm2.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alarm_off, null));
                    switchAlarm2.setText("Alarm 2 Mati");
                }


            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCamera.setText("Mengambil Gambar..");
                buttonCamera.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_button2, null));
                buttonCamera.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonCamera.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_button, null));
                        buttonCamera.setEnabled(true);
                        buttonCamera.setText("Ambil Gambar");
                        Toast.makeText(getActivity(), "Gambar berhasil diambil! silakan cek galeri.", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });
        View selectContactLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_contact,null);
        RealmResults<User> users = realm.where(User.class).findAll();
        final SMSKontakAdapter adapter = new SMSKontakAdapter(users);
        ListView listView = (ListView) selectContactLayout.findViewById(R.id.list_kontak);
        listView.setAdapter(adapter);

        selectContactDialog = new AlertDialog.Builder(getContext())
                .setTitle("Piih Kontak")
                .setView(selectContactLayout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        numbers = adapter.getNumbers();

                    }
                })
                .create();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phoneNumbers.getText().toString();
                String content = smsContent.getText().toString();

                if(!phone.equals("") && !numbers.contains(phone))
                    numbers.add(phone);
                if(numbers.isEmpty())
                    Toast.makeText(getActivity(), "Pilih kontak atau nomor telepon!", Toast.LENGTH_SHORT).show();
                else if(smsContent.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(), "Tuliskan isi SMS terlebih dahulu!", Toast.LENGTH_SHORT).show();
                else{

                    btnSend.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    ArrayList<String> jsonNumbers = new ArrayList<>(numbers);
                    for(int i = 0; i < jsonNumbers.size(); i++){
                        jsonNumbers.set(i, "\""+jsonNumbers.get(i)+"\"");
                    }
                    Toast.makeText(getActivity(), jsonNumbers.toString(), Toast.LENGTH_SHORT).show();
                    Call<Validation> sendSMSCall = APIService.service.sendSMS("testing",jsonNumbers.toString(),content);
                    sendSMSCall.enqueue(new Callback<Validation>() {
                        @Override
                        public void onResponse(Call<Validation> call, Response<Validation> response) {
                            if(response.isSuccessful()){
                                Validation data = response.body();
                                if(data.getStatus().equals("success")){
                                    phoneNumbers.setText("");
                                    smsContent.setText("");
                                    adapter.resetNumbers();
                                    numbers.clear();
                                    Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                }
                                btnSend.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(getActivity(),"Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            Toast.makeText(getActivity(), "Periksa koneksi internet anda!", Toast.LENGTH_SHORT).show();
                            btnSend.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    });
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.select_contact) {
            selectContactDialog.show();
            id = R.id.btn_sms;
        }
        displayMenu(id);

    }
    private void syncUser(){
        Call<UserService> userServiceCall = APIService.service.getUsers("testing");
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
                Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void displayMenu(int id){
        layoutMenu.setVisibility(View.VISIBLE);
        layoutLock.setVisibility(View.GONE);
        layoutCamera.setVisibility(View.GONE);
        layoutAlarm.setVisibility(View.GONE);
        layoutSMS.setVisibility(View.GONE);
        layoutApi.setVisibility(View.GONE);
        switch (id){
            case R.id.btn_lock:
                judul.setText("Smart Lock");
                layoutLock.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_camera:
                judul.setText("Camera");
                layoutCamera.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_galeri:

                layoutMenu.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(),GalleryActivity.class));
                break;
            case R.id.btn_fire:
                judul.setText("Flame Detector");
                layoutApi.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_alarm:
                judul.setText("Alarm");
                layoutAlarm.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_sms:
                judul.setText("SMS");
                layoutSMS.setVisibility(View.VISIBLE);
                break;

            default:
                layoutMenu.setVisibility(View.GONE);
                break;
        }
    }
}