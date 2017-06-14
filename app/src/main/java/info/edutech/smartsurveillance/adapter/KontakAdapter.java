package info.edutech.smartsurveillance.adapter;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


/**
 * Created by Baso on 10/23/2016.
 */
public class KontakAdapter extends RealmRecyclerViewAdapter<User,KontakAdapter.Holder> {

    private OnKontakSelectedListener onKontakSelectedListener;
    public User selectedContact;
    public KontakAdapter(OrderedRealmCollection<User> data) {
        super(data,true);
        //this.onKontakSelectedListener = onKontakSelectedListener;
        setHasStableIds(true);
    }

    public void setOnKontakSelectedListener(OnKontakSelectedListener onKontakSelectedListener) {
        this.onKontakSelectedListener = onKontakSelectedListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.kontak_list, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        final User kontak = getItem(position);
        holder.nama.setText(kontak.getName());
        holder.no_hp.setText(kontak.getPhoneNumber());
        String type="";
        if(kontak.getType()==1){
            type="Owner";
        }
        else{
            type="Non Owner";
        }

        holder.type.setText(type);

        if(selectedContact!= null && selectedContact.getId() == kontak.getId()){
            holder.itemView.setBackgroundColor(Color.GRAY);
        }
        else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onKontakSelectedListener != null) {
                    onKontakSelectedListener.onSelected(kontak);
                    selectedContact = kontak;
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }




    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        TextView nama;
        TextView no_hp;
        TextView type;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nama = (TextView) itemView.findViewById(R.id.nama_kontak);
            no_hp = (TextView) itemView.findViewById(R.id.no_hp_kontak);
            type = (TextView) itemView.findViewById(R.id.type_kontak);
        }
    }
    public interface OnKontakSelectedListener{
        void onSelected(User kontak);
    }
}
