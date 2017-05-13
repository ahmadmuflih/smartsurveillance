package info.edutech.smartsurveillance.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.adapter.SMSKontakAdapter;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.User;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 4/25/2017.
 */
public class SMSFragment extends Fragment implements View.OnClickListener {
    AlertDialog selectContactDialog;
    EditText phoneNumbers, smsContent;
    Button btnSend;
    ArrayList<String> numbers = new ArrayList<>();
    Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sms,container,false);
        (v.findViewById(R.id.select_contact)).setOnClickListener(this);
        btnSend = (Button)v.findViewById(R.id.send_sms);
        phoneNumbers = (EditText)v.findViewById(R.id.input_hp);
        smsContent = (EditText)v.findViewById(R.id.input_sms);

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

                    btnSend.setEnabled(false);
                    btnSend.setText("Sending..");
                    ArrayList<String> jsonNumbers = new ArrayList<>(numbers);
                    for(int i = 0; i < jsonNumbers.size(); i++){
                        jsonNumbers.set(i, "\""+jsonNumbers.get(i)+"\"");
                    }
                    Toast.makeText(getActivity(), jsonNumbers.toString(), Toast.LENGTH_SHORT).show();
                    Call<Validation> sendSMSCall = APIService.service.sendSMS(Config.getPrivateKey(),jsonNumbers.toString(),content);
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
                                btnSend.setEnabled(true);
                                btnSend.setText("SEND");
                            }
                            else{
                                Toast.makeText(getActivity(),"Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Validation> call, Throwable t) {
                            Toast.makeText(getActivity(), "Periksa koneksi internet anda!", Toast.LENGTH_SHORT).show();
                            btnSend.setEnabled(true);
                            btnSend.setText("SEND");

                        }
                    });
                }
            }
        });
        return v;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.select_contact) {
            selectContactDialog.show();
        }

    }
}
