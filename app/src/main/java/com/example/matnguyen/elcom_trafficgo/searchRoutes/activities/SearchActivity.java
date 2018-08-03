package com.example.matnguyen.elcom_trafficgo.searchRoutes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matnguyen.elcom_trafficgo.MapsActivity;
import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;

public class SearchActivity extends Activity {

    private ImageButton btnExchange, btnBack;
    private TextView edtOrigin, edtDest;
    private ImageView btnMyLocation, btnLocation;
    private ImageButton btnCar, btnBike, btnBus, btnWalk;
    private boolean check_exchange = true;
    public static iMap imap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_route_layout);
        initWidget();
        Intent intent = getIntent();
        if(intent != null){
            String s = null;
            if( (s = intent.getStringExtra("origin")) != null){
                edtOrigin.setText(s);
            } else {
                edtDest.setText(s);
            }
        }
        solve();

    }

    public void initWidget(){
        btnBack = findViewById(R.id.btn_back);
        btnExchange = findViewById(R.id.btn_exchange);
        edtOrigin = findViewById(R.id.edt_origin);
        edtDest = findViewById(R.id.edt_dest);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnLocation = findViewById(R.id.btnLocation);

        btnCar = findViewById(R.id.ibt_car);
        btnBike = findViewById(R.id.ibt_bike);
        btnWalk = findViewById(R.id.ibt_walk);
        btnBus = findViewById(R.id.ibt_bus);

    }

    public void solve(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_exchange = !check_exchange;
                if(!check_exchange) {
                    btnMyLocation.setImageResource(R.drawable.ic_location);
                    btnLocation.setImageResource(R.drawable.ic_my_location);
                    float edtDx = edtDest.getX();
                    float edtDy = edtDest.getY();
                    float edtOx = edtOrigin.getX();
                    float edtOy = edtOrigin.getY();

                    edtDest.setX(edtOx);
                    edtDest.setY(edtOy);
                    edtOrigin.setX(edtDx);
                    edtOrigin.setY(edtDy);

                } else {
                    btnLocation.setImageResource(R.drawable.ic_location);
                    btnMyLocation.setImageResource(R.drawable.ic_my_location);
                    float edtDx = edtDest.getX();
                    float edtDy = edtDest.getY();
                    float edtOx = edtOrigin.getX();
                    float edtOy = edtOrigin.getY();

                    edtDest.setX(edtOx);
                    edtDest.setY(edtOy);
                    edtOrigin.setX(edtDx);
                    edtOrigin.setY(edtDy);

                }
            }
        });
        btnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imap.selectTraffic(4);
            }
        });
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imap.selectTraffic(1);
            }
        });
        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imap.selectTraffic(3);
            }
        });
        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imap.selectTraffic(2);
            }
        });
        edtOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SearchActivity.this, SelectLocation.class);
                intent1.putExtra("Location", "Origin");
                startActivity(intent1);

            }
        });
        edtDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SelectLocation.class);
                intent.putExtra("Location", "Destination");
                startActivity(intent);
            }
        });

    }
}
