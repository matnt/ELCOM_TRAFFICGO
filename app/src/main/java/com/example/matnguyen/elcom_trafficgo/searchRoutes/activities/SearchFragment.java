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

    public static LatLng mLastlocation;

    ///
    private ImageButton btnExchange, btnBack;
    public static TextView edtOrigin, edtDest;
    private ImageView btnMyLocation, btnLocation;
    private ImageButton btnCar, btnBike, btnBus, btnWalk;

    ///
    private boolean check_exchange = true;
    public static iMap imap;
    public static ArrayList<Point> arr = new ArrayList<>(2);
    private static String titleAction = "";

    public static SearchFragment newInstance(String title, Point point) {
        SearchFragment searchFragment = new SearchFragment();
        Log.e(TAG, "GO TO NEW INSTANCE");

        if(title.equals("Origin")){
            edtOrigin.setText(point.getName());
            MapsActivity.arrayList.add(0, new LatLng(point.getLat(), point.getLng()));
            Log.e(TAG, "EDIT ORIGIN: " + edtOrigin.getText());

        } else if(title.equals("Destination")){
            edtDest.setText(point.getName());
            MapsActivity.arrayList.add(1, new LatLng(point.getLat(), point.getLng()));
            Log.e(TAG, "EDIT DESTINATION: " + edtDest.getText());
        } else if(title.equals("NONE")) {
            Log.e(TAG, "NONE");

            edtOrigin.setText("Vị trí của bạn");
            edtDest.setText("Vị trí đến");
        }
        titleAction = title;
        return searchFragment;
    }

    public Bundle saveInstance(String title){
        Log.e(TAG, "size arr: " + arr.size());
        Bundle outState = null;
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
        savedInstanceState = saveInstance(titleAction);
        Log.e(TAG, "onActivityCreated");
        if(savedInstanceState != null) {
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

            //arr = new ArrayList<>();
        }
        initWidget(view);
        solve();
        search();

        return view;
    }


    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.search_route_layout);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
//        mapFragment.getMapAsync(this);
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SearchFragment.this);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        buildGoogleApiClient();
//        initWidget();
//
//        //intent after enter place name in select location
////        Intent intent = getIntent();
////        if (intent != null) {
////            String s = null;
////            if ((s = intent.getStringExtra("origin")) != null) {
////                Log.e(TAG, s+"");
////                edtOrigin.setText(s);
////            } else {
////                Log.e(TAG, s+"");
////                edtDest.setText(s);
////            }
////        }
////        Log.e(TAG, "not intent");
//        solve();
//        search();
//
//    }
//    public void buildGoogleApiClient( ) {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .build();
//    }

    // get intent
//    public void getIntentFromFragment(){
//        if (getArguments().getParcelable("Point") != null){
//            Point point= getArguments().getParcelable("Point");
//            arr.add(point);
//        }
//
//        if(getArguments().getParcelable("Point origin") != null){
//            Point p1 = getArguments().getParcelable("Point origin");
//            arr.add(0, p1);
//        }
//
//        if(getArguments().getParcelable("Point destination") != null){
//            Point p2 = getArguments().getParcelable("Point destination");
//            arr.add(1, p2);
//        }
//
//
//    }
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

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMapsearch = googleMap;
//        mMapsearch.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMapsearch.getUiSettings().setZoomControlsEnabled(false);
//        mMapsearch.getUiSettings().setMyLocationButtonEnabled(false);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mMapsearch.setMyLocationEnabled(true);
//        }
//        // move camera to curent location
//        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if(task.isSuccessful()){
//                    Location location = task.getResult();
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    mLastlocation = latLng;
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//                    mMapsearch.moveCamera(cameraUpdate);
//                }
//            }
//        });

        // select point
//        mMapsearch.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                if(arr.size() == 2){
//                    arr.clear();
//                    mMapsearch.clear();
//                }
//                arr.add(latLng);
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                if(arr.size() == 1){
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                    // set text for edtOrigin about point address
//                    //edtOrigin.setText(arr.get(0).);
//                } else {
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    // set text for edtOrigin about point address
//                }
//                mMapsearch.addMarker(markerOptions);
//                if(arr.size() == 2){
//                    String url = GoogleService.getRequestUrl(arr.get(0), arr.get(1), vehicle);
//                    GoogleService.TaskRequestDirection taskRequestDirection = new GoogleService.TaskRequestDirection();
//                    taskRequestDirection.execute(url);
//                }
//
//            }
//        });

//        // select route
//        mMapsearch.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
//            @Override
//            public void onPolylineClick(Polyline polyline) {
//
//            }
//        });
//
//
//    }

    public void search(){
        if(arr.size() == 2){
            // add 2 marker and draw routes
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("arraylistPoint", arr);

            MapsActivity.arrPoints.add(0, arr.get(0));
            MapsActivity.arrPoints.add(1, arr.get(1));
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
