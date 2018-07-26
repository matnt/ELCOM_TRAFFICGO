package com.example.matnguyen.elcom_trafficgo.alarm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.matnguyen.elcom_trafficgo.R;

public final class AlarmLandingPageFragment extends Fragment implements View.OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false);

        final Button launchMainActivityBtn = (Button) v.findViewById(R.id.load_main_activity_btn);
        final Button dismiss = (Button) v.findViewById(R.id.dismiss_btn);

        launchMainActivityBtn.setOnClickListener(this);
        dismiss.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.load_main_activity_btn:
                startActivity(new Intent(getContext(), AlarmActivity.class));
                getActivity().finish();
                break;
            case R.id.dismiss_btn:
                getActivity().finish();
                break;
        }

    }
}
