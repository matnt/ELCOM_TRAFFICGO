package com.example.matnguyen.elcom_trafficgo.selectpoint.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matnguyen.elcom_trafficgo.R;

public class frag_search extends Fragment {
    private RecyclerView rcv_my_places,rcv_history;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_seach, container, false);

        initWidget(view);
        return view;
    }

    private void initWidget(View view) {
        rcv_my_places=view.findViewById(R.id.rcv_my_places);
        rcv_history=view.findViewById(R.id.rcv_history);
    }
}
