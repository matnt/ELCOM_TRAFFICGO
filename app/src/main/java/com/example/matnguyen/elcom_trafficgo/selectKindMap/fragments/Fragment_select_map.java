package com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.alarm.ui.AlarmActivity;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;


public class Fragment_select_map extends DialogFragment {
    private static final String TAG = "FragmentChooseMap";

    // widget
    private ImageButton img_define;
    private ImageButton img_satelite;
    private ImageButton img_traffic;
    private ImageButton img_density;

    private Button btnDistance;
    private Button btnClock;

    public static iMap imap;
    private int s;
    private int a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_map, container, false);
        initWidget(view);

        choose_kind_map();

        henGio();

        return view;
    }
    private void initWidget(View view){
        img_define = view.findViewById(R.id.img_define);
        img_density = view.findViewById(R.id.img_density);
        img_satelite = view.findViewById(R.id.img_satelite);
        img_traffic = view.findViewById(R.id.img_traffic);

        btnClock = view.findViewById(R.id.btnClock);
        btnDistance = view.findViewById(R.id.btnDistance);

    }

    public void choose_kind_map(){

        img_define.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = 1;
                getDialog().cancel();
                imap.selectTypeMap(s);
            }
        });
        // density map will be designed by dev with density of traffic
        img_density.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = 2;
                getDialog().cancel();
                imap.selectTypeMap(s);
            }
        });
        img_satelite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = 3;
                getDialog().cancel();
                imap.selectTypeMap(s);
            }
        });
        // traffic map will be designed by dev with signpost
        img_traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = 4;
                getDialog().cancel();
                imap.selectTypeMap(s);
            }
        });
    }

    public void henGio(){
        btnClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
                Intent i= new Intent(getContext(),AlarmActivity.class);
                startActivity(i);

            }
        });
    }
}
