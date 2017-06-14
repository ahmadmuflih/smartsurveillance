package info.edutech.smartsurveillance.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.edutech.smartsurveillance.R;
import info.edutech.smartsurveillance.activity.AboutActivity;
import info.edutech.smartsurveillance.activity.DescriptionActivity;

/**
 * Created by Baso on 10/8/2016.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    public AboutFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_about, container, false);
        view.findViewById(R.id.btn_perangkat).setOnClickListener(this);
        view.findViewById(R.id.btn_developer).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_perangkat:
                startActivity(new Intent(getActivity(), DescriptionActivity.class));
                break;
            case R.id.btn_developer:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
        }
    }
}
