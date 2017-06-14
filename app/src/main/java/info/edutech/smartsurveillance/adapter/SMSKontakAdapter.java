package info.edutech.smartsurveillance.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Baso on 4/4/2017.
 */
public class SMSKontakAdapter extends RealmBaseAdapter<User> {
    private ArrayList<String> numbers;
    private ArrayList<String> names;
    public SMSKontakAdapter(@Nullable OrderedRealmCollection<User> data) {
        super(data);
        numbers = new ArrayList<>();
        names = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.smskontak_list,parent,false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }

        if(adapterData!=null) {
            final User user = adapterData.get(position);
            holder.nama.setText(user.getName());
            holder.no_hp.setText(user.getPhoneNumber());
            //holder.checkBox.setChecked(numbers.contains(user.getPhoneNumber()));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        numbers.add(user.getPhoneNumber());
                        names.add(user.getName());
                    }
                    else {
                        numbers.remove(user.getPhoneNumber());
                        names.remove(user.getName());
                    }
                }
            });
        }
        return convertView;
    }

    public int getNumbersCount(){
        return numbers.size();
    }

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void resetNumbers() {
        this.numbers = new ArrayList<>();
        this.names = new ArrayList<>();
        notifyDataSetChanged();
    }

    public String getSelectedContacts() {
        String selectedNames = "";
        for(int i = 0; i < names.size(); i++){
            String firstWord = names.get(i);
            if(firstWord.contains(" ")){
                firstWord= firstWord.substring(0, firstWord.indexOf(" "));
            }
            selectedNames += firstWord;
            if(i<(names.size()-1))
                selectedNames+= ", ";
        }
        return selectedNames;
    }

    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        CheckBox checkBox;
        TextView nama;
        TextView no_hp;
        TextView type;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            checkBox = (CheckBox)itemView.findViewById(R.id.check);
            nama = (TextView) itemView.findViewById(R.id.nama_kontak);
            no_hp = (TextView) itemView.findViewById(R.id.no_hp_kontak);
        }
    }
}
