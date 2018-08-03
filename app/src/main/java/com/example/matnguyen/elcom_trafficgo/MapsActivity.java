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

import com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Route;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Step;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.parsers.DirectionsParser;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.services.LoadHistoryService;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.fragments.Fragment_select_map;
import com.example.matnguyen.elcom_trafficgo.selectKindMap.interfaces.iMap;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.PlaceAutoCompleteAdapter;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.RecyclerItemClickListener;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.activities.SearchActivity;
import com.example.matnguyen.elcom_trafficgo.selectpoint.fragments.frag_search;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, iMap {

    private static final String TAG = "MapActivity";
    public static final int REQUEST_CODE = 123;
    private static final int LOCATION_REQUEST = 500;
    public static GoogleMap mMap;

    private FloatingActionButton imgKindMap;

    private LocationManager mLocationManager;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    public static Location mLastLocation;
    public static Marker mCurrLocationMarker;

    ////
    private FloatingActionButton fab_my_location, fab_go;
    private  LinearLayout rl_search;
    private ImageButton ibtn_voice, ibtn_search;
    private  EditText edt_search;
    private LinearLayout content_search;

    ////
    private static boolean flag_search = false;
    private  FragmentManager fragmentManager;

    ////
    private  RecyclerView rcv_result_search;
    private  LinearLayoutManager mLinearLayoutManager;
    private  PlaceAutoCompleteAdapter mAutoCompleteAdapter;
    public static GoogleApiClient mGoogleApiClient;

    ///
    private boolean count_voice = false;
    ArrayList<LatLng> arrayList;
    private List<Polyline> polylines;
    private static String vehicle = "driving";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        arrayList = new ArrayList<>();


        buildGoogleApiClient();
        checkPermission();
        //deleteDatabase();
        //checkSize();
        initWidget();
        getMyLocation();
        handleSearch();
        Fragment_select_map.imap = this;
    }

    public void deleteDatabase(){
        ArrayList<Point> arr = DatabasehistoryHelper.getInstance(this).getPoints();
        int size = arr.size();
        while (size != 0){
            DatabasehistoryHelper.getInstance(this).deleteHistory(arr.get(size - 1));
            size--;
        }
        Log.e(TAG, size + "");
    }
    public void checkSize(){
        ArrayList<Point> arrayList = DatabasehistoryHelper.getInstance(this).getPoints();
        Log.e(TAG, "size: " + arrayList.size());
    }



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


    private void initWidget() {
        polylines = new ArrayList<>();

        rcv_result_search = findViewById(R.id.rcv_result_search);
        fragmentManager = getSupportFragmentManager();

        fab_go = findViewById(R.id.fab_go);
        fab_my_location = findViewById(R.id.fab_my_location);

       // Nút về vị trí hiện tại
        fab_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
            }
        });

        rl_search = findViewById(R.id.rl_search);
        rl_search.setVisibility(View.GONE);

        edt_search = findViewById(R.id.edt_search);
        ibtn_voice = findViewById(R.id.ibtn_voice);
        ibtn_search = findViewById(R.id.ibtn_seach);
        content_search = findViewById(R.id.content_search);


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
                FragmentTransaction ft_add = fragmentManager.beginTransaction();
                ft_add.add(R.id.content_search, new frag_search());
                ft_add.commit();

            }
        });

        ibtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_search){
                    flag_search = !flag_search;
                    ibtn_search.setImageResource(R.mipmap.ic_search);
                    rl_search.setVisibility(View.GONE);

                    Fragment fragment = fragmentManager.findFragmentById(R.id.content_search);
                    FragmentTransaction ft_add = fragmentManager.beginTransaction();
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

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
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


        imgKindMap = findViewById(R.id.img_kind_map);

        imgKindMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment_select_map choose_map = new Fragment_select_map();
                choose_map.show(getSupportFragmentManager(), "FragmentChooseMap");

            }
        });
        fab_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                startActivity(intent);

            }
        });

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
//                if(!s.toString().equals("")){
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//
//                }
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
                    final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);
                    Log.e("TAG", "Autocomplete item selected: " + item.placeName);

                    //saveToDatabase();
                    final long id = (long) DatabasehistoryHelper.getInstance(getApplicationContext()).addHistory();
                    LoadHistoryService.launchLoadHistoryService(getApplicationContext());
                    final Point point = new Point();
                    point.setId(id);

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
                                point.setName(item.placeName + "");
                                point.setLat(latLng.latitude);
                                point.setLng(latLng.longitude);
                                final int rowsUpdated = DatabasehistoryHelper.getInstance(getApplicationContext()).updateHistory(point);

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
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                mCurrLocationMarker = mMap.addMarker(markerOptions);

                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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
        mMap.getUiSettings().setZoomControlsEnabled(false);

        /*
        -doc: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#PRIORITY_NO_POWER
        */
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
                    String url = getRequestUrl(arrayList.get(0), arrayList.get(1), vehicle);
                    TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
                    taskRequestDirection.execute(url);

                }
            }
        });

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

    @Override
    public void selectTraffic(int a) {
        switch (a) {
            case 1:
                vehicle = "walking";
                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                break;
            case 2:
                vehicle = "driving";
                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                break;
            case 3:
                vehicle = "bicycling";
                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                break;
            case 4:
                vehicle = "transit";
                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                break;
            default:
                vehicle = "driving";
                sharpRoute(arrayList.get(0), arrayList.get(1), vehicle);
                break;
        }
    }

    private void sharpRoute (LatLng latLng1, LatLng latLng2, String vehicle){
        for(Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
        String strurl = getRequestUrl(latLng1, latLng2, vehicle);
        TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
        taskRequestDirection.execute(strurl);
    }

    // get direction between 2 point using map api

    public String requestDirection(String urlReq) throws IOException {
        String responeString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlReq);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            responeString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responeString;
    }

    public String getRequestUrl(LatLng origin, LatLng des, String vehicle){
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_des = "destination=" + des.latitude + "," + des.longitude;
        String sensor = "sensor=false";
        String mode = "mode=" + vehicle;
        String param = str_org + "&" + str_des + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.e(TAG, url);

        return url;

    }

    public class TaskRequestDirection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responeString = "";
            try {
                responeString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responeString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TaskParser taskParser = new TaskParser();
            TaskParser2 taskParser = new TaskParser2();
            taskParser.execute(s);
        }
    }

    public class TaskParser2 extends AsyncTask<String, Void, List<Route>> {

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
            for(int i = 0; i < routes.size(); i++){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for(Step step : routes.get(i).getSteps()){
                    double lat = Double.parseDouble(step.getPoint().get("lat"));
                    double lon = Double.parseDouble(step.getPoint().get("lon"));
                    points.add(new LatLng(lat, lon));
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
                polylines.add(mMap.addPolyline(polylineOptions));
            }
            if(polylines.size() != 0){
                Log.e("DIRECTIONS PARSER", "SL Polyline: " + polylines.size());
            } else {
                Toast.makeText(MapsActivity.this, "direction not found", Toast.LENGTH_LONG).show();
            }

        }
    }

    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> route = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                route = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return route;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for(List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                for(HashMap<String, String> point : path) {

                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat, lon));

                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            if (polylineOptions != null){
                mMap.addPolyline(polylineOptions);

            } else {
                Toast.makeText(MapsActivity.this, "Direction not found", Toast.LENGTH_LONG).show();
            }

        }
    }






}
