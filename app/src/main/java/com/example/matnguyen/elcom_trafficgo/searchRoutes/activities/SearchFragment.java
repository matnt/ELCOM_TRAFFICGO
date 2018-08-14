package com.example.matnguyen.elcom_trafficgo.searchRoutes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matnguyen.elcom_trafficgo.MapsActivity;
import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class SearchFragment extends Fragment  {
    private static final String TAG = " SEARCH ACTIVITY";


    ///
    private ImageButton btnExchange, btnBack;
    public static TextView edtOrigin, edtDest;
    private ImageView btnMyLocation, btnLocation;
    private ImageButton btnCar, btnBike, btnBus, btnWalk;

    ///
    private boolean check_exchange = true;
    public static iMap iVehicle;
    public static ArrayList<Point> arr = new ArrayList<>(2);
    private static String titleAction = "";
    private static String key = "";

    public static SearchFragment newInstance(String title, String key1) {
        SearchFragment searchFragment = new SearchFragment();
        Log.e(TAG, "GO TO NEW INSTANCE");

//        if(title.equals("Origin")){
//            if(key1 != null){
//                //Log.e(TAG, "KEY: " + key1);
//                //edtOrigin.setText(key);
//                //Log.e(TAG, "KEY: " + edtOrigin.getText());
//            } else {
//                if(point != null){
//                    //edtOrigin.setText(point.getName());
//
//                    //Log.e(TAG, "EDIT ORIGIN: " + edtOrigin.getText());
//                } else {
//                    // point == null
//                    //MapsActivity mapsActivity = MapsActivity.newInstance();
//
//
//                }
//            }
//
//        } else if(title.equals("Destination")){
//            if(key1 != null){
//                //edtDest.setText(key);
//                //Log.e(TAG, "KEY: " + key1);
//            } else {
//                 if (point != null) {
//                     //edtDest.setText(point.getName());
//                     //Log.e(TAG, "EDIT DESTINATION: " + edtDest.getText());
//                 } else {
//                     /// point == null
//
//                 }
//
//            }
//        } else if(title.equals("NONE")) {
//            //Log.e(TAG, "NONE");
//
////            edtOrigin.setText("Vị trí của bạn");
////            edtDest.setText("Vị trí đến");
//        }
        titleAction = title;
        key = key1;
        return searchFragment;
    }

    public Bundle saveInstance(String title, String key1){
        Log.e(TAG, "size arr: " + arr.size());
        Log.e(TAG, "size arr of main activity: " + MapsActivity.arrayList.size());
        Bundle outState = null;

        if(key1 != null){
            outState = new Bundle();
            if(titleAction.equals("Origin")){
                outState.putString("Current_origin", key1);
            } else if(titleAction.equals("Destination")){
                outState.putString("Current_destination", key1);
            }
        }


        if(arr.size() == 1) {
            outState = new Bundle();
            if (title.equals("Origin")) {
                outState.putParcelable("Point_origin", arr.get(0));

            } else if (title.equals("Destination")) {
                outState.putParcelable("Point_destination", arr.get(1));

            }
        }
         else if(arr.size() == 2){
            if(title.equals("NONE")){
                ///
                arr.clear();
                Log.e(TAG, "arr.size = " + arr.size());

            } else {
                outState = new Bundle();
                outState.putParcelable("Point_origin", arr.get(0));
                outState.putParcelable("Point_destination", arr.get(1));
            }
        }
        return outState;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        savedInstanceState = saveInstance(titleAction, key);
        Log.e(TAG, "onActivityCreated");

        if(savedInstanceState != null) {
            String curr_or = savedInstanceState.getString("Current_origin");
            String curr_des = savedInstanceState.getString("Current_destination");
            if(curr_or != null){
                edtOrigin.setText(curr_or);
            }
            if (curr_des != null){
                edtDest.setText(curr_des);
            }

            Point p1 = savedInstanceState.getParcelable("Point_origin");
            Point p2 = savedInstanceState.getParcelable("Point_destination");
            if(p1 != null) {
                edtOrigin.setText(p1.getName());
            }
            if(p2 != null) {
                edtDest.setText(p2.getName());
            }
        } else {
            Log.e(TAG, "save null");
        }
    }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_route_layout, container, false);
        Log.e(TAG, "onCreateView");


        if(savedInstanceState != null) {
            Log.e(TAG, "GO TO OLD FRAGMENT");
        } else {
            Log.e(TAG, "GO TO NEW FRAGMENT");
            if(titleAction.equals("NONE")){
                arr = new ArrayList<>();
            }
        }
        initWidget(view);
        solve();
        search();

        return view;
    }


    public void initWidget(View view) {

        btnBack = view.findViewById(R.id.btn_back);
        btnExchange = view.findViewById(R.id.btn_exchange);
        edtOrigin = view.findViewById(R.id.edt_origin);
        edtDest = view.findViewById(R.id.edt_dest);
        btnMyLocation = view.findViewById(R.id.btnMyLocation);
        btnLocation = view.findViewById(R.id.btnLocation);

        btnCar = view.findViewById(R.id.ibt_car);
        btnBike = view.findViewById(R.id.ibt_bike);
        btnWalk = view.findViewById(R.id.ibt_walk);
        btnBus = view.findViewById(R.id.ibt_bus);

    }

    public void solve() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
            }
        });
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_exchange = !check_exchange;
                if (!check_exchange) {
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
                iVehicle.selectTraffic(4);
            }
        });
        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iVehicle.selectTraffic(1);
            }
        });
        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iVehicle.selectTraffic(3);
            }
        });
        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iVehicle.selectTraffic(2);
            }
        });
        edtOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Location", "Origin");
                SelectLocation frag_select = new SelectLocation();
                frag_select.setArguments(bundle);

                getChildFragmentManager().beginTransaction().replace(R.id.frame_search, frag_select).addToBackStack(null).commit();
            }
        });
        edtDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Location", "Destination");
                SelectLocation frag_select = new SelectLocation();
                frag_select.setArguments(bundle);

                getChildFragmentManager().beginTransaction().replace(R.id.frame_search, frag_select).addToBackStack(null).commit();

            }
        });

    }

    public void search(){
        if(arr.size() == 2){
            // add 2 marker and draw routes

            MapsActivity.arrayList.add(0, new LatLng(arr.get(0).getLat(), arr.get(0).getLng()));
            MapsActivity.arrayList.add(1, new LatLng(arr.get(1).getLat(), arr.get(1).getLng()));
            Intent i = new Intent(getActivity(), MapsActivity.class);
            startActivity(i);

        }
    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
}
