package info.edutech.smartsurveillance.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.activity.GalleryActivity;
import info.edutech.smartsurveillance.app.Config;
import info.edutech.smartsurveillance.model.Validation;
import info.edutech.smartsurveillance.service.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Baso on 4/25/2017.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera,container,false);
        (view.findViewById(R.id.menu_gallery)).setOnClickListener(this);
        (view.findViewById(R.id.menu_capture)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.menu_gallery:
                startActivity(new Intent(getActivity(), GalleryActivity.class));
                break;
            case R.id.menu_capture:
                (view.findViewById(R.id.menu_capture)).setEnabled(false);
                (view.findViewById(R.id.layout_capture)).setBackgroundColor(Color.parseColor("#AAECECEC"));
                Call<Validation> captureCall = APIService.service.askImage(Config.getPrivateKey(),"1");
                captureCall.enqueue(new Callback<Validation>() {
                    @Override
                    public void onResponse(Call<Validation> call, Response<Validation> response) {
                        if(response.isSuccessful()){
                            Validation data = response.body();
                            if(data.getStatus().equals("success"))
                                Toast.makeText(getActivity(), "Succesfully take picture, open gallery!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), data.getError(), Toast.LENGTH_SHORT).show();
                        }
                        (view.findViewById(R.id.menu_capture)).setEnabled(true);
                        (view.findViewById(R.id.layout_capture)).setBackgroundColor(Color.WHITE);
                    }

                    @Override
                    public void onFailure(Call<Validation> call, Throwable t) {
                        //Toast.makeText(getActivity(), "Failed to call server!", Toast.LENGTH_SHORT).show();
                        (view.findViewById(R.id.menu_capture)).setEnabled(true);
                        (view.findViewById(R.id.layout_capture)).setBackgroundColor(Color.WHITE);
                    }
                });
                break;
        }
    }
}
