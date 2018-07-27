package com.example.matnguyen.elcom_trafficgo.alarm.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.matnguyen.elcom_trafficgo.R;

public final class AlarmLandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_landing_page);
    }

}
