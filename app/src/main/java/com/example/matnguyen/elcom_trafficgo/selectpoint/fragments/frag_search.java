package com.example.matnguyen.elcom_trafficgo.selectpoint.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matnguyen.elcom_trafficgo.R;

public class frag_search extends Fragment {
    private RecyclerView rcv_my_places,rcv_history;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.seach_fragment, container, false);

        initWidget(view);
        return view;
    }

    private void initWidget(View view) {
        rcv_my_places=view.findViewById(R.id.rcv_my_places);
        rcv_history=view.findViewById(R.id.rcv_history);
    }
}
