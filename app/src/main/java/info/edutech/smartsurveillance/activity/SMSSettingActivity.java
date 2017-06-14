package info.edutech.smartsurveillance.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.adapter.KontakAdapter;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.User;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import info.edutech.smartsurveillance.util.Preferences;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SMSSettingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CheckBox checkSMSApi, checkSMSManusia;
    EditText textSMSApi, textSMSManusia, textNama,textHP,textNamaEdit,textHPEdit;

    boolean SMSApi, SMSManusia;
    String isiSMSApi, isiSMSManusia;
    KontakAdapter kontakAdapter;
    Button btnSimpan;
    AlertDialog addContactDialog,deleteContactDialog,editContactDialog;
    LinearLayout layoutKontak, layoutSMS, settingSMS;
    Menu menu;
    User selectedContact;
    private static final int RESULT_PICK_CONTACT = 001;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Pengaturan SMS");

        layoutKontak = (LinearLayout)findViewById(R.id.layout_kontak);
        layoutSMS = (LinearLayout)findViewById(R.id.layout_sms);
        settingSMS = (LinearLayout)findViewById(R.id.setting_sms);

        layoutKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerView.getVisibility()==View.VISIBLE)
                    recyclerView.setVisibility(View.GONE);
                else
                    recyclerView.setVisibility(View.VISIBLE);
            }
        });
        layoutSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingSMS.getVisibility() == View.VISIBLE)
                    settingSMS.setVisibility(View.GONE);
                else
                    settingSMS.setVisibility(View.VISIBLE);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.list_user);
        checkSMSApi = (CheckBox) findViewById(R.id.check_sms_api);
        checkSMSManusia = (CheckBox) findViewById(R.id.check_sms_manusia);
        textSMSApi = (EditText) findViewById(R.id.input_sms_api);
        textSMSManusia = (EditText) findViewById(R.id.input_sms_manusia);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);

        setSMS();

        checkSMSApi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textSMSApi.setEnabled(isChecked);
            }
        });

        checkSMSManusia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                textSMSManusia.setEnabled(isChecked);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSMS();
            }
        });



        showContact();

        /*------------ General -------------*/
        final View addContactLayout = getLayoutInflater().inflate(R.layout.dialog_add_contact,null);
        textNama = (EditText) addContactLayout.findViewById(R.id.input_nama);
        textHP = (EditText) addContactLayout.findViewById(R.id.input_hp);
        LinearLayout pickContact = (LinearLayout)addContactLayout.findViewById(R.id.select_contact);

        addContactLayout.findViewById(R.id.btn_add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });
        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });

        addContactDialog = new AlertDialog.Builder(this)
                .setTitle("Tambah Kontak")
                .setView(addContactLayout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addContactDialog.dismiss();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nama = textNama.getText().toString().trim();
                        String no_hp= textHP.getText().toString().trim();
                        if(nama.equals("") || no_hp.equals("")){
                            Toast.makeText(SMSSettingActivity.this, "Nama atau no hp tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            addContactToRealm(nama,no_hp);
                        }
                    }
                })
                .create();
        /*---- */
        final View editContactLayout = getLayoutInflater().inflate(R.layout.dialog_edit_contact,null);
        textNamaEdit = (EditText) editContactLayout.findViewById(R.id.input_nama_edit);
        textHPEdit = (EditText) editContactLayout.findViewById(R.id.input_hp_edit);



        editContactDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Kontak")
                .setView(editContactLayout)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editContactDialog.dismiss();
                    }
                })
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String nama = textNamaEdit.getText().toString().trim();
                        final String no_hp = textHPEdit.getText().toString().trim();
                        if(nama.equals("") || no_hp.equals("")){
                            Toast.makeText(SMSSettingActivity.this, "Nama atau no hp tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Call<Validation> editCall = APIService.service.editUser(Config.getPrivateKey(),selectedContact.getId(),nama,no_hp);
                            editCall.enqueue(new Callback<Validation>() {
                                @Override
                                public void onResponse(Call<Validation> call, Response<Validation> response) {
                                    if(response.isSuccessful()){
                                        Validation data = response.body();
                                        if(data.getStatus().equals("success")){
                                            User newUser = new User(selectedContact.getId(),nama,no_hp,2);
                                            realm.beginTransaction();
                                            realm.copyToRealmOrUpdate(newUser);
                                            realm.commitTransaction();
                                            Toast.makeText(SMSSettingActivity.this, "Kontak berhasil diubah!", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(SMSSettingActivity.this, data.getError(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Validation> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .create();
        deleteContactDialog = new AlertDialog.Builder(this)
                .setTitle("Hapus Kontak")
                .setMessage("Are you sure want to delete this contact?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContactDialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Validation> deleteCall = APIService.service.deleteUser(Config.getPrivateKey(),selectedContact.getId());
                        deleteCall.enqueue(new Callback<Validation>() {
                            @Override
                            public void onResponse(Call<Validation> call, Response<Validation> response) {
                                if(response.isSuccessful()){
                                    Validation data = response.body();
                                    if(data.getStatus().equals("success")){
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmResults<User> result = realm.where(User.class).equalTo("id",selectedContact.getId()).findAll();
                                                result.deleteAllFromRealm();
                                                kontakAdapter.selectedContact=null;
                                                kontakAdapter.notifyDataSetChanged();
                                            }
                                        });
                                        Toast.makeText(SMSSettingActivity.this, "Kontak berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(SMSSettingActivity.this, data.getError(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Validation> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .create();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_sms_setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }
        else if(id == R.id.edit){
            editContactDialog.show();
        }
        else if(id == R.id.delete){
            deleteContactDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            textNama.setText(name);
            textHP.setText(phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContact(){
        realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();
        /*
        RealmList<User> results = new RealmList<>();
        results.addAll(users.subList(0, users.size()));
        Log.e("REALM","SIZE : "+results.size());
        */
        kontakAdapter = new KontakAdapter(users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(kontakAdapter);
        kontakAdapter.setOnKontakSelectedListener(new KontakAdapter.OnKontakSelectedListener() {
            @Override
            public void onSelected(User kontak) {
                selectedContact = kontak;
                textHPEdit.setText(kontak.getPhoneNumber());
                textNamaEdit.setText(kontak.getName());
                if(kontak.getType()==1) {
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.edit).setVisible(false);
                }
                else{
                    menu.findItem(R.id.delete).setVisible(true);
                    menu.findItem(R.id.edit).setVisible(true);
                }
            }
        });
    }

    private void saveSMS(){
        final Boolean sendWhenFire = checkSMSApi.isChecked(), sendWhenMotion = checkSMSManusia.isChecked();
        final String smsFire = textSMSApi.getText().toString(),smsMotion=textSMSManusia.getText().toString();
        Call<Validation> setSmsCall = APIService.service.setSMSConfig(Config.getPrivateKey(),sendWhenFire,sendWhenMotion,smsFire,smsMotion);
        setSmsCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()) {
                    Validation data = response.body();
                    if (data.getStatus().equals("success")) {
                        Toast.makeText(SMSSettingActivity.this, "Perubahan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        Preferences.setBooleanPreferences("sms_api",sendWhenFire,getApplicationContext());
                        Preferences.setBooleanPreferences("sms_manusia",sendWhenMotion,getApplicationContext());
                        Preferences.setStringPreferences("isi_sms_api",smsFire,getApplicationContext());
                        Preferences.setStringPreferences("isi_sms_manusia",smsMotion,getApplicationContext());
                    } else {
                        Toast.makeText(getApplicationContext(), data.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed! Check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSMS(){
        SMSApi = Preferences.getBooleanPreferences("sms_api",true,getApplicationContext());
        SMSManusia = Preferences.getBooleanPreferences("sms_manusia",true,getApplicationContext());

        isiSMSApi = Preferences.getStringPreferences("isi_sms_api","",getApplicationContext());
        isiSMSManusia = Preferences.getStringPreferences("isi_sms_manusia","",getApplicationContext());
        checkSMSApi.setChecked(SMSApi);
        textSMSApi.setEnabled(SMSApi);
        checkSMSManusia.setChecked(SMSManusia);
        textSMSManusia.setEnabled(SMSManusia);
        textSMSApi.setText(isiSMSApi);
        textSMSManusia.setText(isiSMSManusia);
    }

    public void addContact(View v){
        textNama.setText("");
        textHP.setText("");
        addContactDialog.show();
    }
    private void addContactToRealm(final String name, final String phoneNumber){
        Call<Validation> addUserCall = APIService.service.addUser(Config.getPrivateKey(),name,phoneNumber);
        addUserCall.enqueue(new Callback<Validation>() {
            @Override
            public void onResponse(Call<Validation> call, Response<Validation> response) {
                if(response.isSuccessful()){
                    Validation data = response.body();
                    if(data.getStatus().equals("success")){
                        User newUser = new User(data.getData(),name,phoneNumber,2);
                        realm.beginTransaction();
                        realm.copyToRealm(newUser);
                        realm.commitTransaction();
                        Toast.makeText(SMSSettingActivity.this, "Kontak berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), data.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<Validation> call, Throwable t) {

            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
    }
}