package com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.matnguyen.elcom_trafficgo.MapsActivity;
import com.example.matnguyen.elcom_trafficgo.R;

import java.util.ArrayList;

public class Fragment_info_polyline extends Fragment {
    private static final String TAG = "FRAGMENT INFO POLYLINE";
    public static ArrayList<String> arr;
    public static String text = "";

    private ListView listView;
    private ImageButton btnBackInfo;
    private TextView tv;

    public static Fragment_info_polyline newInstance(){
        Fragment_info_polyline fragment_info_polyline = new Fragment_info_polyline();

        return fragment_info_polyline;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_info_polyline, container, false);
        listView = view.findViewById(R.id.info_polyline);
        btnBackInfo = view.findViewById(R.id.btn_backInfo);
        tv = view.findViewById(R.id.text_info);

        text = MapsActivity.vehicle;
        tv.setText("Infomation " + text + " route");
        arr = new ArrayList<>();
        arr = MapsActivity.arrStepBundle;
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < arr.size(); i++){
            String b = "font-size:0.9em";
            String a = "<div style=" + '"' + b + '"' +">";
            //Log.e(TAG, "a: " + a);
            String s = arr.get(i);
            s = s.replace(a, " ");
            s = s.replace("</div>", " ");
            s = s.replace("<b>", " ");
            s = s.replace("</b>", " ");
            Log.e(TAG, "s: " + s);
            String[] sub = s.split("[<b>\\</b>]\\ ' '");

            s = "";
            for(int j = 0; j < sub.length; j++){
                s += "" + sub[j];
            }
            result.add(s);
        }
        Log.e(TAG, "NUMBER STEPS: " + arr.size());
        Log.e(TAG, "NUMBER SIZE RESULT: " + result.size());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, result);
        listView.setAdapter(adapter);

        btnBackInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);

            }
        });

        return view;
    }
}
