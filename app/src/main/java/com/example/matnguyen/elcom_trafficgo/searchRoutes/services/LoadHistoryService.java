package com.example.matnguyen.elcom_trafficgo.searchRoutes.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;

import java.util.ArrayList;

public class LoadHistoryService extends IntentService {
    private static final String TAG = "LOAD HISTORY SERVICE";
    public static final String ACTION_COMPLETE = TAG + ".ACTION_COMPLETE";
    public static final String HISTORY_EXTRA = "history_extra";
    public LoadHistoryService(){
        this(TAG);
    }

    public LoadHistoryService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final ArrayList<Point> points = DatabasehistoryHelper.getInstance(this).getPoints();

        final Intent i = new Intent(ACTION_COMPLETE);
        i.putParcelableArrayListExtra(HISTORY_EXTRA,points);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public static void launchLoadHistoryService(Context context) {
        final Intent launchLoadHistoryServiceIntent = new Intent(context, LoadHistoryService.class);
        context.startService(launchLoadHistoryServiceIntent);
    }
}
