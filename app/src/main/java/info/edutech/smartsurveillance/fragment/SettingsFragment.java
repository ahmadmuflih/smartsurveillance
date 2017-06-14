package info.edutech.smartsurveillance.fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.activity.MainActivity;
import info.edutech.smartsurveillance.activity.SMSSettingActivity;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.DataAlarm;
import info.edutech.smartsurveillance.model.DataLock;
import info.edutech.smartsurveillance.model.DataSMS;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.model.ValidationAlarm;
import info.edutech.smartsurveillance.model.ValidationLock;
import info.edutech.smartsurveillance.model.ValidationSMS;
import info.edutech.smartsurveillance.service.APIService;
import info.edutech.smartsurveillance.util.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 10/8/2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    CardView btnGeneral, btnPassword,btnNotif, btnKamera, btnLock, btnSms, btnAlarm;
    TextView textSeekbar;
    EditText textNama, textHP,textToken,textPassword,textOldPassword,textNewPassword;
    SeekBar seekBar;

    Switch alarmSwitch,notifSwitch,lockSwitch;
    CheckBox apiCheck, manusiaCheck,notifApi,notifManusia;
    private int alarmValue=3;
    private int notifValue=3;

    View editLock,editCamera, editAlarm, editNotif, editGeneral,confirmPassword,editPassword;

    private RadioGroup radioGroup;
    private RadioButton radioHigh, radioStandart, radioLow;


    AlertDialog lockDialog,cameraDialog,alarmDialog,notifDialog, generalDialog,confirmPasswordDialog,passwordDialog;

    String nama, no_telp;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnGeneral = (CardView) view.findViewById(R.id.btn_general);
        btnPassword = (CardView)view.findViewById(R.id.btn_password);
        btnNotif = (CardView) view.findViewById(R.id.btn_notif);
        btnKamera = (CardView) view.findViewById(R.id.btn_kamera);
        btnLock = (CardView) view.findViewById(R.id.btn_lock);
        btnSms = (CardView) view.findViewById(R.id.btn_sms);
        btnAlarm = (CardView) view.findViewById(R.id.btn_alarm);

        btnGeneral.setOnClickListener(this);
        btnPassword.setOnClickListener(this);
        btnNotif.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        btnAlarm.setOnClickListener(this);
        btnKamera.setOnClickListener(this);

        /*------------ Sync -------------*/

        Call<Validation> getCameraCall = APIService.service.getCameraConfig(Config.getPrivateKey());
        getCameraCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    Validation data = response.body();
                    if(data.getStatus().equals("success")){
                        Preferences.setIntPreferences("camera",data.getData(),getActivity());
                    }
                    else {
                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //Toast.makeText(getActivity(), "Gagal1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                //Toast.makeText(getActivity(), "Gagal2", Toast.LENGTH_SHORT).show();
            }
        });
        Call<ValidationLock> getLockCall = APIService.service.getLockConfig(Config.getPrivateKey());
        getLockCall.enqueue(new Callback<ValidationLock>() {
            @Override
            public void onResponse(Call<ValidationLock> call, Response<ValidationLock> response) {
                if(response.isSuccessful()){
                    ValidationLock dataLock = response.body();
                    if(dataLock.getStatus().equals("success")){
                        DataLock data = dataLock.getData();
                        int lockVal=0;
                        if(data.getDuration()==10)
                            lockVal=1;
                        else if(data.getDuration()==20)
                            lockVal=2;
                        else if(data.getDuration()==30)
                            lockVal=3;
                        else if(data.getDuration()==60)
                            lockVal=4;
                        Preferences.setIntPreferences("lock",lockVal,getActivity());
                        Preferences.setBooleanPreferences("auto_lock",data.getAutoLock(),getActivity());
                    }
                    else {
                        Toast.makeText(getActivity(), dataLock.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    //Toast.makeText(getActivity(), "Gagal1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValidationLock> call, Throwable t) {
                //Toast.makeText(getActivity(), "Gagal2", Toast.LENGTH_SHORT).show();
            }
        });


        Call<ValidationAlarm> getAlarmCall = APIService.service.getAlarmConfig(Config.getPrivateKey());
        getAlarmCall.enqueue(new Callback<ValidationAlarm>() {
            @Override
            public void onResponse(Call<ValidationAlarm> call, Response<ValidationAlarm> response) {
                if(response.isSuccessful()){
                    ValidationAlarm data = response.body();
                    if(data.getStatus().equals("success")){
                        int alarmVal = 0;
                        DataAlarm dataAlarm = data.getData();
                        if(dataAlarm.getAlarmEnable())
                            if(dataAlarm.getBeepFire() && dataAlarm.getBeepMotion())
                                alarmVal = 3;
                            else if(dataAlarm.getBeepFire())
                                alarmVal = 1;
                            else if(dataAlarm.getBeepMotion())
                                alarmVal = 2;

                        Preferences.setIntPreferences("alarm",alarmVal,getActivity());
                    }
                    else {
                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidationAlarm> call, Throwable t) {

            }
        });

        Call<ValidationSMS> getSMSCall = APIService.service.getSMSConfig(Config.getPrivateKey());
        getSMSCall.enqueue(new Callback<ValidationSMS>() {
            @Override
            public void onResponse(Call<ValidationSMS> call, Response<ValidationSMS> response) {
                if(response.isSuccessful()){
                    ValidationSMS data = response.body();
                    if(data.getStatus().equals("success")){
                        DataSMS dataSms = data.getData();
                        Preferences.setBooleanPreferences("sms_api",dataSms.getSendWhenFire(),getActivity());
                        Preferences.setBooleanPreferences("sms_manusia",dataSms.getSendWhenMotion(),getActivity());
                        Preferences.setStringPreferences("isi_sms_api",dataSms.getSmsFire(),getActivity());
                        Preferences.setStringPreferences("isi_sms_manusia",dataSms.getSmsMotion(),getActivity());
                    }
                    else {
                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ValidationSMS> call, Throwable t) {

            }
        });
        /*------------ Lock -------------*/
        editLock = inflater.inflate(R.layout.dialog_lock,null);
        textSeekbar = (TextView) editLock.findViewById(R.id.text_seekbar);
        lockSwitch = (Switch)editLock.findViewById(R.id.switch_lock);
        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lockSwitch.setText("Mengunci saat ada gerakan");
                }
                else{
                    lockSwitch.setText("Tidak mengunci saat ada gerakan");
                }
            }
        });
        seekBar = (SeekBar) editLock.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTextSeekbar(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lockDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Atur Lock Otomatis")
                .setView(editLock)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lockDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int seekVal = seekBar.getProgress();
                        int lockVal =0;
                        switch (seekVal){
                            case 1:lockVal=10;break;
                            case 2:lockVal=20;break;
                            case 3:lockVal=30;break;
                            case 4:lockVal=60;break;
                        }
                        final boolean autoLock = lockSwitch.isChecked();
                        Call<Validation> setLockCall = APIService.service.setLockConfig(Config.getPrivateKey(),lockVal,autoLock);
                        setLockCall.enqueue(new Callback<Validation>() {
                            @Override
                            public void onResponse(Call<Validation> call, Response<Validation> response) {
                                if(response.isSuccessful()) {
                                    Validation data = response.body();
                                    if (data.getStatus().equals("success")) {
                                        Preferences.setIntPreferences("lock", seekVal, getActivity());
                                        Preferences.setBooleanPreferences("auto_lock",autoLock,getActivity());
                                    } else {
                                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{

                                }
                            }

                            @Override
                            public void onFailure(Call<Validation> call, Throwable t) {
                                Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .create();

        /*------------ Camera -------------*/
        editCamera = inflater.inflate(R.layout.dialog_camera,null);
        radioGroup = (RadioGroup) editCamera.findViewById(R.id.radio_group);


        radioHigh = (RadioButton) editCamera.findViewById(R.id.camera_high);
        radioStandart = (RadioButton) editCamera.findViewById(R.id.camera_standart);
        radioLow = (RadioButton) editCamera.findViewById(R.id.camera_low);

        cameraDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Atur Kualitas Gambar")
                .setView(editCamera)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cameraDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int quality = radioGroup.indexOfChild(editCamera.findViewById(radioGroup.getCheckedRadioButtonId()))+1;
                        Call<Validation> setCameraCall = APIService.service.setCameraConfig(Config.getPrivateKey(),quality);
                        setCameraCall.enqueue(new Callback<Validation>() {
                            @Override
                            public void onResponse(Call<Validation> call, Response<Validation> response) {
                                if(response.isSuccessful()) {
                                    Validation data = response.body();
                                    if (data.getStatus().equals("success")) {
                                        Preferences.setIntPreferences("camera",quality,getActivity());
                                    } else {
                                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{

                                }
                            }

                            @Override
                            public void onFailure(Call<Validation> call, Throwable t) {
                                Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .create();
         /*------------ Alarm -------------*/
        editAlarm = inflater.inflate(R.layout.dialog_alarm,null);
        alarmSwitch = (Switch) editAlarm.findViewById(R.id.switch_alarm);
        apiCheck = (CheckBox)editAlarm.findViewById(R.id.check_api);
        manusiaCheck = (CheckBox)editAlarm.findViewById(R.id.check_manusia);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    alarmSwitch.setText("Alarm dinyalakan");
                    apiCheck.setEnabled(true);
                    manusiaCheck.setEnabled(true);
                    apiCheck.setChecked(true);
                    manusiaCheck.setChecked(true);
                }
                else{
                    alarmSwitch.setText("Alarm dimatikan");
                    apiCheck.setEnabled(false);
                    manusiaCheck.setEnabled(false);
                    apiCheck.setChecked(false);
                    manusiaCheck.setChecked(false);
                }
                cekAlarmValue();
            }
        });
        apiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekAlarmValue();
            }
        });
        manusiaCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekAlarmValue();
            }
        });

        alarmDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Atur Alarm Otomatis")
                .setView(editAlarm)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarmDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean alarmEnabled = false, beepFire = false, beepMotion=false;
                        switch (alarmValue){
                            case 1: alarmEnabled=true;beepFire=true;beepMotion=false;break;
                            case 2: alarmEnabled=true;beepFire=false;beepMotion=true;break;
                            case 3: alarmEnabled=true;beepFire=true;beepMotion=true;break;
                        }
                        Call<Validation> setAlarmCall = APIService.service.setAlarmConfig(Config.getPrivateKey(),alarmEnabled,beepMotion,beepFire);
                        setAlarmCall.enqueue(new Callback<Validation>() {
                            @Override
                            public void onResponse(Call<Validation> call, Response<Validation> response) {
                                if(response.isSuccessful()) {
                                    Validation data = response.body();
                                    if (data.getStatus().equals("success")) {
                                        Preferences.setIntPreferences("alarm",alarmValue,getActivity());
                                    } else {
                                        Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{

                                }
                            }

                            @Override
                            public void onFailure(Call<Validation> call, Throwable t) {
                                Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                })
                .create();
        /*------------ Notif -------------*/
        editNotif = inflater.inflate(R.layout.dialog_notif,null);
        notifSwitch = (Switch) editNotif.findViewById(R.id.switch_notif);
        notifApi = (CheckBox)editNotif.findViewById(R.id.notif_api);
        notifManusia = (CheckBox)editNotif.findViewById(R.id.notif_manusia);

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    notifSwitch.setText("Notif dinyalakan");
                    notifApi.setEnabled(true);
                    notifManusia.setEnabled(true);
                    notifApi.setChecked(true);
                    notifManusia.setChecked(true);
                }
                else{
                    notifSwitch.setText("Notif dimatikan");
                    notifApi.setEnabled(false);
                    notifManusia.setEnabled(false);
                    notifApi.setChecked(false);
                    notifManusia.setChecked(false);
                }
                cekNotifValue();
            }
        });
        notifApi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekNotifValue();
            }
        });
        notifManusia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cekNotifValue();
            }
        });

        notifDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Atur Push Notifikasi")
                .setView(editNotif)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preferences.setIntPreferences("notif",notifValue,getActivity());
                    }
                })
                .create();
        /*------------ General -------------*/
        editGeneral = inflater.inflate(R.layout.dialog_general,null);
        textNama = (EditText) editGeneral.findViewById(R.id.input_nama);
        textHP = (EditText) editGeneral.findViewById(R.id.input_hp);
        textToken = (EditText) editGeneral.findViewById(R.id.input_token);

        generalDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Atur Informasi Pribadi")
                .setView(editGeneral)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generalDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", null)
                .create();
        generalDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button bSave = generalDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nama = textNama.getText().toString().trim();
                        no_telp = textHP.getText().toString().trim();
                        if(nama.equals("") || no_telp.equals(""))
                            Toast.makeText(getActivity(), "Name or phone number can not be empty!", Toast.LENGTH_SHORT).show();
                        else if(no_telp.length()<10)
                            Toast.makeText(getActivity(), "Your phone number is invalid", Toast.LENGTH_SHORT).show();
                        else {
                            generalDialog.dismiss();
                            confirmPasswordDialog.show();
                        }
                    }
                });
            }
        });
        /*------------ Confirm -------------*/
        confirmPassword = inflater.inflate(R.layout.dialog_confirm_password,null);
        textPassword = (EditText) confirmPassword.findViewById(R.id.input_password);

        confirmPasswordDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Konfirmasi Password")
                .setView(confirmPassword)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generalDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", null)
                .create();
        confirmPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                textPassword.setText("");
                Button bSave = confirmPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = Config.getPrivateKey();
                        int id = Preferences.getIntPreferences("id","",getActivity());
                        String password = textPassword.getText().toString();
                        if(password.equals("")){
                            Toast.makeText(getActivity(), "Password can not be empty!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Call<Validation> updateUserCall = APIService.service.updateUser(key, id, password, nama, no_telp, null);
                            updateUserCall.enqueue(new Callback<Validation>() {
                                @Override
                                public void onResponse(Call<Validation> call, Response<Validation> response) {
                                    if (response.isSuccessful()) {
                                        Validation data = response.body();
                                        if (data.getStatus().equals("success")) {

                                            Preferences.setStringPreferences("nama",textNama.getText().toString(),getActivity());
                                            Preferences.setStringPreferences("no_hp",textHP.getText().toString(),getActivity());
                                            ((MainActivity)getActivity()).setProfil(textNama.getText().toString(),textHP.getText().toString());
                                            Toast.makeText(getActivity(), "Account Information has successfully changed", Toast.LENGTH_SHORT).show();
                                            confirmPasswordDialog.dismiss();
                                        } else {
                                            Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {

                                    }
                                }

                                @Override
                                public void onFailure(Call<Validation> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        /*------------ Password -------------*/
        editPassword = inflater.inflate(R.layout.dialog_password,null);
        textOldPassword = (EditText) editPassword.findViewById(R.id.input_old_password);
        textNewPassword = (EditText) editPassword.findViewById(R.id.input_new_password);

        passwordDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Ganti Password")
                .setView(editPassword)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        generalDialog.dismiss();
                    }
                })
                .setPositiveButton("Save", null)
                .create();
        passwordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button bSave = passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                bSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = Config.getPrivateKey();
                        int id = Preferences.getIntPreferences("id","",getActivity());
                        String password = textOldPassword.getText().toString();
                        String newPassword = textNewPassword.getText().toString();
                        if(password.equals("") || newPassword.equals("")){
                            Toast.makeText(getActivity(), "Password can not be empty!", Toast.LENGTH_SHORT).show();
                        }
                        else if(newPassword.length()<6){
                            Toast.makeText(getActivity(), "New password should contain at least 6 characters!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Call<Validation> updateUserCall = APIService.service.updateUser(key, id, password, null, null, newPassword);
                            updateUserCall.enqueue(new Callback<Validation>() {
                                @Override
                                public void onResponse(Call<Validation> call, Response<Validation> response) {
                                    if (response.isSuccessful()) {
                                        Validation data = response.body();
                                        if (data.getStatus().equals("success")) {
                                            Toast.makeText(getActivity(), "Password has successfully changed", Toast.LENGTH_SHORT).show();
                                            passwordDialog.dismiss();
                                        } else {
                                            Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {

                                    }
                                }

                                @Override
                                public void onFailure(Call<Validation> call, Throwable t) {
                                    Toast.makeText(getActivity(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }

    private void setTextSeekbar(int value){
        switch (value){
            case 0:
                textSeekbar.setText("Matikan fitur lock otomatis");
                break;
            case 1:
                textSeekbar.setText("Interval : 10 detik");
                break;
            case 2:
                textSeekbar.setText("Interval : 20 detik");
                break;
            case 3:
                textSeekbar.setText("Interval : 30 detik");
                break;
            case 4:
                textSeekbar.setText("Interval : 1 menit");
                break;

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_general:
                String nama = Preferences.getStringPreferences("nama","",getActivity());
                String no_hp = Preferences.getStringPreferences("no_hp","",getActivity());
                String token = Preferences.getStringPreferences("token","",getActivity());
                textNama.setText(nama);
                textHP.setText(no_hp);
                textToken.setText(token);
                generalDialog.show();

                break;
            case R.id.btn_password:
                textOldPassword.setText("");
                textNewPassword.setText("");
                passwordDialog.show();
                break;
            case R.id.btn_notif:
                notifValue = Preferences.getIntPreferences("notif","setting",getActivity());
                setNotif(notifValue);
                notifDialog.show();
                break;
            case R.id.btn_kamera:
                int radioValue = Preferences.getIntPreferences("camera","setting",getActivity());
                ((RadioButton)radioGroup.getChildAt((radioValue-1))).setChecked(true);

                cameraDialog.show();
                break;
            case R.id.btn_sms:
                startActivity(new Intent(getActivity(), SMSSettingActivity.class));
                break;
            case R.id.btn_alarm:
                alarmValue = Preferences.getIntPreferences("alarm","setting",getActivity());
                setAlarm(alarmValue);
                alarmDialog.show();
                break;
            case R.id.btn_lock:
                int seekbarValue = Preferences.getIntPreferences("lock","setting",getActivity());
                seekBar.setProgress(seekbarValue);
                setTextSeekbar(seekbarValue);
                boolean autoLock = Preferences.getBooleanPreferences("auto_lock",true,getActivity());
                lockSwitch.setChecked(autoLock);
                lockDialog.show();
                break;
        }
    }
    private void cekAlarmValue(){
        if(!alarmSwitch.isChecked()){
            alarmValue=0;
        }
        else if(apiCheck.isChecked() && manusiaCheck.isChecked()){
            alarmValue=3;
        }
        else if(apiCheck.isChecked()){
            alarmValue=1;
        }
        else if(manusiaCheck.isChecked()){
            alarmValue=2;
        }
        else{
            alarmValue=0;
        }
    }
    private void setAlarm(int value){
        switch (value){
            case 0:
                alarmSwitch.setText("Alarm dimatikan");
                alarmSwitch.setChecked(false);
                apiCheck.setEnabled(false);
                apiCheck.setChecked(false);
                manusiaCheck.setEnabled(false);
                manusiaCheck.setChecked(false);
                break;
            case 1:
                alarmSwitch.setText("Alarm dinyalakan");
                alarmSwitch.setChecked(true);
                apiCheck.setEnabled(true);
                manusiaCheck.setEnabled(true);
                apiCheck.setChecked(true);
                manusiaCheck.setChecked(false);
                break;
            case 2:
                alarmSwitch.setText("Alarm dinyalakan");
                alarmSwitch.setChecked(true);
                apiCheck.setEnabled(true);
                manusiaCheck.setEnabled(true);
                apiCheck.setChecked(false);
                manusiaCheck.setChecked(true);
                break;
            case 3:
                alarmSwitch.setText("Alarm dinyalakan");
                alarmSwitch.setChecked(true);
                apiCheck.setEnabled(true);
                manusiaCheck.setEnabled(true);
                apiCheck.setChecked(true);
                manusiaCheck.setChecked(true);
                break;
        }
    }
    private void cekNotifValue(){
        if(!notifSwitch.isChecked()){
            notifValue=0;
        }
        else if(notifApi.isChecked() && notifManusia.isChecked()){
            notifValue=3;
        }
        else if(notifApi.isChecked()){
            notifValue=1;
        }
        else if(notifManusia.isChecked()){
            notifValue=2;
        }
        else{
            notifValue=0;
        }
    }
    private void setNotif(int value){
        switch (value){
            case 0:
                notifSwitch.setText("Notif dimatikan");
                notifSwitch.setChecked(false);
                notifApi.setEnabled(false);
                notifApi.setChecked(false);
                notifManusia.setEnabled(false);
                notifManusia.setChecked(false);
                break;
            case 1:
                notifSwitch.setText("Notif dinyalakan");
                notifSwitch.setChecked(true);
                notifApi.setEnabled(true);
                notifApi.setChecked(true);
                notifManusia.setEnabled(true);
                notifManusia.setChecked(false);
                break;
            case 2:
                notifSwitch.setText("Notif dinyalakan");
                notifSwitch.setChecked(true);
                notifApi.setEnabled(true);
                notifApi.setChecked(false);
                notifManusia.setEnabled(true);
                notifManusia.setChecked(true);
                break;
            case 3:
                notifSwitch.setText("Notif dinyalakan");
                notifSwitch.setChecked(true);
                notifApi.setEnabled(true);
                notifApi.setChecked(true);
                notifManusia.setEnabled(true);
                notifManusia.setChecked(true);
                break;
        }
    }
}
