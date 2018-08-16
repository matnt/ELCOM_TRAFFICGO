package com.example.matnguyen.elcom_trafficgo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.matnguyen.elcom_trafficgo.searchRoutes.activities.SearchFragment;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Route;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Step;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.parsers.DirectionsParser;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.services.GoogleService;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.services.LoadHistoryService;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments.Fragment_info_polyline;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments.Fragment_select_map;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.PlaceAutoCompleteAdapter;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.RecyclerItemClickListener;
import com.example.matnguyen.elcom_trafficgo.selectpoint.utils.TConfigs;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, iMap {

    // final
    private static final String TAG = "MapActivity";
    public static final int REQUEST_CODE = 123;
    private static final int LOCATION_REQUEST = 500;

    ///google map
    public static GoogleMap mMap;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    public static Location mLastLocation;
    public static Marker mCurrLocationMarker;
    public static GoogleApiClient mGoogleApiClient;

    ////init
    private FloatingActionButton imgKindMap;
    private FloatingActionButton fab_my_location, fab_go;
    private LinearLayout rl_search, linear_vehicle;
    private ImageButton ibtn_voice, ibtn_search;
    private EditText edt_search;
    private LinearLayout content_search;
    private CardView card;
    private RecyclerView rcv_result_search;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageButton btn_car, btn_transit, btn_walk, btn_bike;
    private ImageButton btn_info;


    ////
    private static boolean flag_search = false;
    private FragmentManager fragmentManager;
    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;

    ///
    private boolean count_voice = false;
    public static ArrayList<LatLng> arrayList = new ArrayList<>();
    private static List<Polyline> mPolylines;
    public static ArrayList<String> mSteps;
    public static ArrayList<String> arrStepBundle;

    private static HashMap<String, ArrayList<String>> mInfoPolyline = new HashMap<>();
    public static String vehicle = "driving";

    public static MapsActivity newInstance(){
        MapsActivity mapsActivity = new MapsActivity();

        return mapsActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get auto my location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

        // delete database
        //MapsActivity.this.deleteDatabase(DatabasehistoryHelper.DATABASE_NAME);

        buildGoogleApiClient();
        checkPermission();
        initWidget();
        solve();

        handleSearch();
        searchAuto();
        Fragment_select_map.imapKind = this;
        //SearchFragment.iVehicle = this;
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

    public void buildGoogleApiClient( ) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    private void initWidget() {
        mPolylines = new ArrayList<>();

        card = findViewById(R.id.cardview);

        rcv_result_search = findViewById(R.id.rcv_result_search);
        fragmentManager = getSupportFragmentManager();

        fab_go = findViewById(R.id.fab_go);
        fab_my_location = findViewById(R.id.fab_my_location);

        rl_search = findViewById(R.id.rl_search);
        rl_search.setVisibility(View.GONE);

        edt_search = findViewById(R.id.edt_search);
        ibtn_voice = findViewById(R.id.ibtn_voice);
        ibtn_search = findViewById(R.id.ibtn_seach);
        content_search = findViewById(R.id.content_search);

        imgKindMap = findViewById(R.id.img_kind_map);

        btn_car = findViewById(R.id.img_car);
        btn_transit = findViewById(R.id.img_transit);
        btn_bike = findViewById(R.id.img_bike);
        btn_walk = findViewById(R.id.img_walk);
        linear_vehicle = findViewById(R.id.linear_vehicle);
        linear_vehicle.setVisibility(View.GONE);

        btn_info = findViewById(R.id.infoRoute);

    }
    public void solve(){

        // Nút về vị trí hiện tại
        fab_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) ,17.0f));
            }
        });

        edt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag_search) {
                    rl_search.setVisibility(View.VISIBLE);
                    flag_search = !flag_search;
                }

                edt_search.setFocusableInTouchMode(true);
                edt_search.setFocusable(true);
                edt_search.requestFocus();

                //Show bàn phím
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                ibtn_search.setImageResource(R.drawable.ic_back);
            }
        });

        ibtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_search){
                    flag_search = !flag_search;
                    ibtn_search.setImageResource(R.mipmap.ic_search);
                    rl_search.setVisibility(View.GONE);

                    MapsActivity.newInstance();

                    //Ẩn bàn phím
                    edt_search.setFocusableInTouchMode(false);
                    edt_search.setFocusable(false);
                    edt_search.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                    edt_search.setText(null);
                    //AutoCompleteAdapter.ClearData();
                }
            }
        });

        PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if(activities.size() == 0){
            ibtn_voice.setEnabled(false);
            Toast.makeText(this, "THIS DEVICE IS NOT SUPPORTED MICROPHONE", Toast.LENGTH_SHORT).show();
        }
        ibtn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!count_voice) {
                    startVoiceRecognition();
                    count_voice = !count_voice;
                }
                else {
                    edt_search.setText(null);
                    startVoiceRecognition();
                    count_voice = !count_voice;
                }
            }
        });

        imgKindMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_select_map choose_map = new Fragment_select_map();
                choose_map.show(getSupportFragmentManager(), "FragmentChooseMap");

            }
        });
        fab_go.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Click fab go");
                Log.e(TAG, "arraylist size: " + arrayList.size());

                fab_go.setVisibility(View.GONE);
                fab_my_location.setVisibility(View.GONE);
                card.setVisibility(View.GONE);
                imgKindMap.setVisibility(View.GONE);
                // set visiable map
                if(arrayList.size() > 0){
                    if(arrayList.size() == 2){
                        arrayList = new ArrayList<>();
                    }
                    SearchFragment searchFragment = SearchFragment.newInstance("NONE", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.rlt, searchFragment).addToBackStack(null).commit();
                } else {
                    Log.e(TAG, "SEARCH FRAGMENT");
                    SearchFragment searchFragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.rlt, searchFragment).addToBackStack(null).commit();
                }

            }
        });
        if(arrayList.size() == 2) {
            linear_vehicle.setVisibility(View.VISIBLE);

            btn_car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicle = "driving";
                    Log.e(TAG, "CAR");
                    sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);

                }
            });

            btn_transit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicle = "transit";
                    Log.e(TAG, "TRANSIT");
                    sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                }
            });

            btn_bike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicle = "bike";
                    Log.e(TAG, "BIKE");
                    sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                }
            });

            btn_walk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicle = "walk";
                    Log.e(TAG, "WALK");
                    sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                }
            });
            btn_info.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View view) {
                    fab_go.setVisibility(View.GONE);
                    fab_my_location.setVisibility(View.GONE);
                    card.setVisibility(View.GONE);
                    imgKindMap.setVisibility(View.GONE);
                    linear_vehicle.setVisibility(View.GONE);

                    Fragment_info_polyline fragment_info = Fragment_info_polyline.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.rlt, fragment_info).addToBackStack(null).commit();

                }
            });

        }


    }

    public void handleSearch( ) {
        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, TConfigs.BOUNDS_DEF, null);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rcv_result_search.setLayoutManager(mLinearLayoutManager);
        rcv_result_search.setAdapter(mAutoCompleteAdapter);


        edt_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());

                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getApplicationContext(), TConfigs.API_NOT_CONNECTED, Toast.LENGTH_LONG).show();
                    Log.e(TConfigs.PlacesTag, TConfigs.API_NOT_CONNECTED);
                    Log.e(TAG, "API  NOT CONNECTED");
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
                public void onItemClick(View view, final int position) {
                    final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);
                    Log.e("TAG", "Autocomplete item selected: " + item.placeName);

                    //saveToDatabase();
                    Point p = new Point();
                    p.setId(String.valueOf(item.placeId));
                    p.setName(String.valueOf(item.placeName));

                    DatabasehistoryHelper.getInstance(getApplicationContext()).addNewPoint(p);
                    LoadHistoryService.launchLoadHistoryService(getApplicationContext());
                    final Point point = new Point();
                    //Log.e(TAG, "PLACE ID: " + placeId + " ID: " + id);
                    point.setId(placeId);

                    /*
                         Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                     */

                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getCount() == 1) {
                                //Do the things here on Click.....
                                LatLng latLng = places.get(0).getLatLng();
                                point.setId(item.placeId + "");
                                point.setName(item.placeName + "");
                                point.setLat(latLng.latitude);
                                point.setLng(latLng.longitude);
                                final int rowsUpdated = DatabasehistoryHelper.getInstance(getApplicationContext()).updateHistory(point);
                                Log.e(TAG, "ROW UPDATED: " + rowsUpdated);
                                if(rowsUpdated == 1){
                                    Toast.makeText(getApplicationContext(), "Update success!", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Update success!");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Update fail!", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "Update fail!");
                                }
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(places.get(0).getName().toString());
                                if(arrayList.size() == 2){
                                    arrayList.clear();
                                    mMap.clear();
                                    arrayList.add(latLng);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                } else if(arrayList.size() == 1){
                                    arrayList.add(latLng);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                    String url = GoogleService.getRequestUrl(arrayList.get(0), arrayList.get(1), vehicle);
                                    GoogleService.TaskRequestDirection taskRequestDirection = new GoogleService.TaskRequestDirection();
                                    taskRequestDirection.execute(url);
                                } else {
                                    arrayList.add(latLng);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }



                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                mCurrLocationMarker = mMap.addMarker(markerOptions);

                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                //fragmentManager.beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.content_search)).commit();

                                if (flag_search) {
                                    flag_search = !flag_search;
                                    ibtn_search.setImageResource(R.mipmap.ic_search);
                                    rl_search.setVisibility(View.GONE);

                                    MapsActivity.newInstance();
                                    Log.e(TAG, "go here");

                                    //Ẩn bàn phím
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);

                                    edt_search.setText("");
                                    //mAutoCompleteAdapter.ClearData();
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), TConfigs.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Log.e("TAG", "Clicked: " + item.placeName);
                    Log.e("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                }
            }));

    }


    // search location by voice
    public void startVoiceRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching ....");

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(!matches.isEmpty()){
                String query = matches.get(0);
                edt_search.setText(query);
                final int position = edt_search.length();
                Log.e(TAG, "POSITION: "+ position);

                edt_search.post(new Runnable() {
                    @Override
                    public void run() {
                        edt_search.setSelection(edt_search.length());
                    }
                });


            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mLocationRequest = new LocationRequest();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

        // choose a location in map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(arrayList.size() == 2){
                    arrayList.clear();
                    mMap.clear();
                }
                arrayList.add(latLng);
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                if(arrayList.size() == 1){
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                mMap.addMarker(marker);
                if(arrayList.size() == 2){
                    String url = GoogleService.getRequestUrl(arrayList.get(0), arrayList.get(1), vehicle);
                    GoogleService.TaskRequestDirection taskRequestDirection = new GoogleService.TaskRequestDirection();
                    taskRequestDirection.execute(url);

                }
            }
        });

        // click polyline in maps
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

                for(Polyline p : mPolylines){
                    if(p.equals(polyline)){
                        polyline.setColor(Color.BLUE);
                        String id = p.getId();
                        // bundle to fragment info polyline
                        arrStepBundle = new ArrayList<>();
                         arrStepBundle = mInfoPolyline.get(id);
                         Log.e(TAG, "SIZE ARRSTEP BUNDLE: " + arrStepBundle.size());

                    } else {
                        p.setColor(Color.LTGRAY);
                    }

                }
            }
        });

    }

    public void searchAuto(){
        Log.e(TAG, "AUTO SEARCH");
        Log.e(TAG, "size: " + arrayList.size());

            // add start marker
            if(arrayList.size() == 2) {
                LatLng latLng1 = new LatLng(arrayList.get(0).latitude, arrayList.get(0).longitude);
                LatLng latlng2 = new LatLng(arrayList.get(1).latitude, arrayList.get(1).longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng1);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
            // add end marker
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latlng2);
            markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions1);
            // draw route
            String url = GoogleService.getRequestUrl(latLng1, latlng2, vehicle);
            Log.e(TAG, url);
            GoogleService.TaskRequestDirection taskRequestDirection = new GoogleService.TaskRequestDirection();
            taskRequestDirection.execute(url);
        }

    }

    @Override
    public void onBackPressed() {
        if (flag_search) {
            flag_search = !flag_search;
            ibtn_search.setImageResource(R.mipmap.ic_search);
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;// Lưu vị trí cuối cùng
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        }
    };


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

//    @Override
//    public void selectTraffic(int a) {
//        switch (a) {
//            case 1:
//                vehicle = "walking";
//                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
//                break;
//            case 2:
//                vehicle = "driving";
//                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
//                break;
//            case 3:
//                vehicle = "bicycling";
//                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
//                break;
//            case 4:
//                vehicle = "transit";
//                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
//                break;
//            default:
//                vehicle = "driving";
//                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
//                break;
//        }
//    }

    private void sharpRoute (LatLng latLng1, LatLng latLng2, String vehicle){
        Log.e(TAG, "VEHICLE: " + vehicle);
        for(Polyline polyline : mPolylines) {
            polyline.remove();
        }
        mPolylines.clear();
        String strurl = GoogleService.getRequestUrl(latLng1, latLng2, vehicle);
        GoogleService.TaskRequestDirection taskRequestDirection = new GoogleService.TaskRequestDirection();
        taskRequestDirection.execute(strurl);
    }


    // get direction between 2 point using map api
    public static class TaskParser extends AsyncTask<String, Void, List<Route>> {

        @Override
        protected List<Route> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<Route> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parseListRoute(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<Route> routes) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            Log.e(TAG, "ROUTE SIZE " + routes.size());
            //Route route;
            String id = "";
            for(int i = 0; i < routes.size(); i++){
                //routes.get(i).setId(i);
                mSteps = new ArrayList<>();
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for(Step step : routes.get(i).getSteps()){
                    double lat = Double.parseDouble(step.getPoint().get("lat"));
                    double lon = Double.parseDouble(step.getPoint().get("lon"));
                    points.add(new LatLng(lat, lon));
                    String mRoute = step.getIntructions();

                    if(mSteps.size() == 0){
                        mSteps.add(mRoute);
                    } else if(mSteps.size() > 0){

                        // compare mRoute with the last element of mSteps
                        String lastElement = mSteps.get(mSteps.size() - 1);
                        if(!mRoute.equals(lastElement)){
                            mSteps.add(mRoute);
                        }
                    }

                }

                polylineOptions.addAll(points);
                polylineOptions.width(18);
                if (i == 0){
                    if (vehicle.equals("driving")){
                        polylineOptions.color(Color.BLUE);
                    } else if (vehicle.equals("walking")){
                        polylineOptions.color(Color.YELLOW);
                    } else if (vehicle.equals("transit")){
                        polylineOptions.color(Color.GREEN);
                    } else if(vehicle.equals("bicycling")){
                        polylineOptions.color(Color.RED);
                    }
                } else {
                    polylineOptions.color(Color.LTGRAY);

                }
                polylineOptions.geodesic(true);
                Polyline polyline = mMap.addPolyline(polylineOptions);
                if(i == 0){
                    id = polyline.getId();
                }

                mInfoPolyline.put(polyline.getId(), mSteps);
                polyline.setClickable(true);

                mPolylines.add(polyline);
                Log.e(TAG, "SO STEP = " + mSteps.size());
            }

            arrStepBundle = new ArrayList<>();
            arrStepBundle = mInfoPolyline.get(id);
        }
    }


}
