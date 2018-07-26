package com.example.matnguyen.elcom_trafficgo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments.Fragment_select_map;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.AT_Adapter;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.RecyclerItemClickListener;
import com.example.matnguyen.elcom_trafficgo.selectpoint.fragments.frag_search;
import com.example.matnguyen.elcom_trafficgo.utils.TConfigs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, iMap {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    private FloatingActionButton imgKindMap;

    private LocationManager mLocationManager;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    ////
    private FloatingActionButton fab_my_location, fab_go;
    private LinearLayout rl_search;
    private ImageButton ibtn_voice, ibtn_seach;
    private EditText edt_search;
    private LinearLayout content_search;

    ////

    private int flag_search = -1;
    private FragmentManager fragmentManager;


    ////

    private RecyclerView rcv_result_search;
    private LinearLayoutManager mLinearLayoutManager;
    private AT_Adapter mAutoCompleteAdapter;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.e(TAG,Environment.getDataDirectory().toString());
        fullScreen();
        buildGoogleApiClient();
        checkPermission();
        initWidget();
        getMyLocation();
        handleSearch();
        Fragment_select_map.imap = this;
    }

    private void handleSearch() {
        mAutoCompleteAdapter = new AT_Adapter(this, mGoogleApiClient, TConfigs.BOUNDS_DEF, null);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rcv_result_search.setLayoutManager(mLinearLayoutManager);
        rcv_result_search.setAdapter(mAutoCompleteAdapter);


        edt_search.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getApplicationContext(), TConfigs.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e(TConfigs.PlacesTag, TConfigs.API_NOT_CONNECTED);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        rcv_result_search.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final AT_Adapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.i("TAG", "Autocomplete item selected: " + item.placeName);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getCount() == 1) {
                                    //Do the things here on Click.....
                                    LatLng latLng = places.get(0).getLatLng();

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title(places.get(0).getName().toString());
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    mCurrLocationMarker = mMap.addMarker(markerOptions);

                                    //move map camera
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                    if (flag_search == 1) {
                                        flag_search = flag_search * -1;
                                        ibtn_seach.setImageResource(R.drawable.ic_home_tab);
                                        rl_search.setVisibility(View.GONE);

                                        Fragment fragment = fragmentManager.findFragmentById(R.id.content_search);
                                        android.support.v4.app.FragmentTransaction ft_add = fragmentManager.beginTransaction();
                                        ft_add.remove(fragment);
                                        ft_add.commit();

                                        //Ẩn bàn phím
                                        edt_search.setFocusableInTouchMode(false);
                                        edt_search.setFocusable(false);
                                        edt_search.requestFocus();

                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                                        edt_search.setText("");
                                        mAutoCompleteAdapter.ClearData();
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), TConfigs.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.i("TAG", "Clicked: " + item.placeName);
                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                    }
                }));

    }

    private void initWidget() {
        rcv_result_search = findViewById(R.id.rcv_result_search);
        fragmentManager = getSupportFragmentManager();

        fab_go = findViewById(R.id.fab_go);
        fab_my_location = findViewById(R.id.fab_my_location);

        //Nút về vị trí hiện tại
        fab_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()), 15));
            }
        });


        rl_search = findViewById(R.id.rl_search);
        rl_search.setVisibility(View.GONE);

        edt_search = findViewById(R.id.edt_search);
        ibtn_voice = findViewById(R.id.ibtn_voice);
        ibtn_seach = findViewById(R.id.ibtn_seach);
        content_search = findViewById(R.id.content_search);


        edt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_search == -1) {
                    rl_search.setVisibility(View.VISIBLE);
                    flag_search = flag_search * -1;
                }

                edt_search.setFocusableInTouchMode(true);
                edt_search.setFocusable(true);
                edt_search.requestFocus();
                //Show bàn phím
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                ibtn_seach.setImageResource(R.mipmap.ic_back);

                android.support.v4.app.FragmentTransaction ft_add = fragmentManager.beginTransaction();
                ft_add.add(R.id.content_search, new frag_search());
                ft_add.commit();

            }
        });

        ibtn_seach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_search == 1) {
                    flag_search = flag_search * -1;
                    ibtn_seach.setImageResource(R.drawable.ic_home_tab);
                    rl_search.setVisibility(View.GONE);

                    Fragment fragment = fragmentManager.findFragmentById(R.id.content_search);
                    android.support.v4.app.FragmentTransaction ft_add = fragmentManager.beginTransaction();
                    ft_add.remove(fragment);
                    ft_add.commit();

                    //Ẩn bàn phím
                    edt_search.setFocusableInTouchMode(false);
                    edt_search.setFocusable(false);
                    edt_search.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                    edt_search.setText("");
                    mAutoCompleteAdapter.ClearData();
                }
            }
        });

        imgKindMap = findViewById(R.id.img_kind_map);

        imgKindMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment_select_map choose_map = new Fragment_select_map();
                choose_map.show(getSupportFragmentManager(), "FragmentChooseMap");

//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                Fragment_choose_map choose_map = new Fragment_choose_map();
//                transaction.add(R.id.rlt, choose_map);
//                transaction.commit();

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        /*
        -doc: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#PRIORITY_NO_POWER
        */
        mLocationRequest.setInterval(TConfigs.TIME_INTERVAL);
        mLocationRequest.setFastestInterval(TConfigs.TIME_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                checkPermission();
            }
        } else {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (flag_search == 1) {
            flag_search = flag_search * -1;
            ibtn_seach.setImageResource(R.drawable.ic_home_tab);
            rl_search.setVisibility(View.GONE);

            Fragment fragment = fragmentManager.findFragmentById(R.id.content_search);
            android.support.v4.app.FragmentTransaction ft_add = fragmentManager.beginTransaction();
            ft_add.remove(fragment);
            ft_add.commit();

            //Ẩn bàn phím
            edt_search.setFocusableInTouchMode(false);
            edt_search.setFocusable(false);
            edt_search.requestFocus();

            mAutoCompleteAdapter.ClearData();
            edt_search.setText("");
        } else {
            super.onBackPressed();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }

        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected( Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void fullScreen() {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private void getMyLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;// Lưu vị trí cuối cùng
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    };

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }
    private void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Ckeck Per", "1");
            } else {
                Log.e("Ckeck Per", "2");
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, TConfigs.REQUEST_CODE_PERMISSION);
            }
        }
    }

    @Override
    public void selectTypeMap(int s) {
        switch (s) {
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

        }
    }
}
