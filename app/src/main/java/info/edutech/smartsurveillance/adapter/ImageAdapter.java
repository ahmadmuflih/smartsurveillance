package info.edutech.smartsurveillance.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import info.edutech.smartsurveillance.MyApplication;
import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.model.Capture;
import io.realm.RealmList;

/**
 * Created by Baso on 10/20/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {
    private Context mContext;
    private RealmList<Capture> captures;
    private ArrayList<Boolean> displays;
    private OnPhotoSelectedListener onPhotoSelectedListener;
    private String BASE_URL;
    public ImageAdapter(Context mContext, RealmList<Capture> captures, String BASE_URL, OnPhotoSelectedListener onPhotoSelectedListener) {
        this.mContext = mContext;
        this.captures = captures;
        this.onPhotoSelectedListener = onPhotoSelectedListener;
        this.BASE_URL=BASE_URL;
        setDisplays(0);
    }
    private void setDisplays(int position){
        displays = new ArrayList<>();
        for(int i = 0; i < captures.size(); i++){
            if(i==position)
                displays.add(true);
            else
                displays.add(false);
        }
    }
    public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener) {
        this.onPhotoSelectedListener = onPhotoSelectedListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        final Capture photo = captures.get(position);
        final String path = mContext.getFilesDir().getAbsolutePath() + "/" + photo.getImageName();
        File file = new File(path);
        Log.d("File",file.exists()+" "+path);
        if(displays.get(position)==true)
            holder.background.setVisibility(View.VISIBLE);
        else
            holder.background.setVisibility(View.INVISIBLE);
        if(file.exists()){
            Picasso.with(mContext)
                    .load(file)
                    .placeholder(R.drawable.loading)
                    .noFade()
                    .into(holder.imageView);
        }
        else{
            Picasso.with(mContext)
                    .load(BASE_URL+photo.getUrl())
                    .placeholder(R.drawable.loading)
                    .noFade()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.imageView.setImageBitmap(bitmap);
                            new Thread(new Runnable() {
                                public void run() {
                                    File file = new File(path);
                                    FileOutputStream fos;
                                    try {
                                        if(!file.exists())
                                            file.createNewFile();
                                        fos = new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                        fos.flush();
                                        fos.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }).start();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }
        holder.textView.setText(getDate(photo.getDate())+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPhotoSelectedListener != null) {
                    onPhotoSelectedListener.onSelected(photo);
                    int index=getSelectedIndex();
                    if(index!=position) {
                        setDisplays(position);
                        notifyItemChanged(position);
                        notifyItemChanged(index);
                    }
                }
            }
        });
    }
    public String getDate(Date date){
        Date newDate = new Date();
        //in milliseconds
        long diff = newDate.getTime() - date.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if(diffDays>1){
            return diffDays+" days ago";
        }
        else if(diffDays==1){
            return "Yesterday";
        }
        else if(diffHours>=1){
            return diffHours+" hours ago";
        }
        else if(diffMinutes>=1){
            return diffMinutes+" minutes ago";
        }
        else{
            return "Just now";
        }
    }
    @Override
    public long getItemId(int position) {
        return captures.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return captures.size();
    }

    private int getSelectedIndex(){
        for(int i = 0; i<displays.size();i++){
            if(displays.get(i))
                return i;
        }
        return -1;
    }

    public class Holder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        ImageView background;
        TextView textView;
        LinearLayout layout;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            background = (ImageView) itemView.findViewById(R.id.background);
            textView = (TextView) itemView.findViewById(R.id.keterangan);
            layout = (LinearLayout)itemView.findViewById(R.id.nama_layout);
        }
    }
    public interface OnPhotoSelectedListener{
        public void onSelected(Capture capture);
    }

}
