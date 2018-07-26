package com.example.matnguyen.elcom_trafficgo.alarm.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.alarm.adapter.AlarmsAdapter;
import com.example.matnguyen.elcom_trafficgo.alarm.model.Alarm;
import com.example.matnguyen.elcom_trafficgo.alarm.service.LoadAlarmsReceiver;
import com.example.matnguyen.elcom_trafficgo.alarm.service.LoadAlarmsService;
import com.example.matnguyen.elcom_trafficgo.alarm.util.AlarmUtils;
import com.example.matnguyen.elcom_trafficgo.alarm.view.DividerItemDecoration;
import com.example.matnguyen.elcom_trafficgo.alarm.view.EmptyRecyclerView;

import java.util.ArrayList;

public final class MainFragment extends Fragment
        implements LoadAlarmsReceiver.OnAlarmsLoadedListener {
    private static final String TAG = "MAIN FRAGMENT";

    private LoadAlarmsReceiver mReceiver;
    private AlarmsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new LoadAlarmsReceiver(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        final EmptyRecyclerView rv =  v.findViewById(R.id.recycler);
        mAdapter = new AlarmsAdapter();
        rv.setEmptyView(v.findViewById(R.id.empty_view));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(getContext()));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        Log.e(TAG, mAdapter.getItemCount() +" ");

        final FloatingActionButton fab =  v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmUtils.checkAlarmPermissions(getActivity());
                final Intent i =
                        AddEditAlarmActivity.buildAddEditAlarmActivityIntent(
                                getContext(), AddEditAlarmActivity.ADD_ALARM
                        );
                startActivity(i);
            }
        });

        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        final IntentFilter filter = new IntentFilter(LoadAlarmsService.ACTION_COMPLETE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
        LoadAlarmsService.launchLoadAlarmsService(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onAlarmsLoaded(ArrayList<Alarm> alar) {
        mAdapter.setAlarms(alar);
    }
}
