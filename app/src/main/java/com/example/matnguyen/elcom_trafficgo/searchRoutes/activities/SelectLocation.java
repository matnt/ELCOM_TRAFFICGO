package com.example.matnguyen.elcom_trafficgo.searchRoutes.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matnguyen.elcom_trafficgo.MapsActivity;
import com.example.matnguyen.elcom_trafficgo.R;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.adapters.ListLocationAdapter;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.data.DatabasehistoryHelper;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.ItemList;
import com.example.matnguyen.elcom_trafficgo.searchRoutes.model.Point;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.PlaceAutoCompleteAdapter;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.RecyclerItemClickListener;
import com.example.matnguyen.elcom_trafficgo.selectpoint.fragments.frag_search;
import com.example.matnguyen.elcom_trafficgo.selectpoint.utils.TConfigs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectLocation extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SELECT LOCATION";
    private ImageButton btnBack, btnVoice;
    private EditText edtLocation;
    private ListView listSpecial, listHistory;
    private String receive;
    private FragmentManager fragmentManager;


    ///
    private int count_voice = 1;
    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;
    public GoogleApiClient mGoogleApiClient;
    private LinearLayoutManager mLinearLayout;
    private RecyclerView rcv_result_search;
    private boolean check_search = false;
    private LinearLayout linearLayout_search;
    private LinearLayout linearlayout_specialplace;
    private ArrayList<LatLng> arrLatlng;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location_layout);

        //buildGoogleApiClient();

        initWidget();
        getIntentFromSearchActivity();
        solve();
        setContentList();
        getIntentFromHistory();
        handsearch();

    }

    public void getIntentFromSearchActivity(){
        receive = getIntent().getStringExtra("Location");
        if(receive.equals("Origin")){
            edtLocation.setHint("Chọn điểm khởi hành");

        } else if(receive.equals("Destination")){

            edtLocation.setHint("Chọn điểm đến");
        }
        // hide key broad
        edtLocation.setFocusableInTouchMode(false);
        edtLocation.setFocusable(false);
        edtLocation.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtLocation.getWindowToken(), 0);
    }

    public void getIntentFromHistory(){
        ArrayList<ItemList> arrayList = new ArrayList<>();
        ArrayList<Point> arr = DatabasehistoryHelper.getInstance(this).getPoints();
        Log.e(TAG, "SIZE: " + arr.size());
        Log.e(TAG, arr.get(arr.size() - 1).getLat() + " - " + arr.get(arr.size() - 1).getLng());
        Log.e(TAG, arr.get(arr.size() - 1).getName());
        //String name = getAddress(arr.get(arr.size() - 1).getLat() , arr.get(arr.size() - 1).getLng());
        //Log.e(TAG, name);
        for(Point p : arr){

            String name = p.getName();
            ItemList item = new ItemList(name, R.drawable.ic_location);
            arrayList.add(item);
        }
        ListLocationAdapter adapter = new ListLocationAdapter(this, arrayList);
        listHistory.setAdapter(adapter);
    }

    public void setContentList(){
        ArrayList<ItemList> arrItem = new ArrayList<>();
        ItemList item1 = new ItemList("Vị trí hiện tại", R.drawable.ic_location);
        ItemList item2 = new ItemList("Chọn trên bản đồ", R.drawable.ic_my_location);
        arrItem.add(item1);
        arrItem.add(item2);

        ListLocationAdapter adapter = new ListLocationAdapter(this, arrItem);
        listSpecial.setAdapter(adapter);
    }

    public void initWidget(){
        btnBack = findViewById(R.id.btn_back);
        btnVoice = findViewById(R.id.btn_voice);
        edtLocation = findViewById(R.id.edt_location);
        listSpecial = findViewById(R.id.list_special_search);
        listHistory = findViewById(R.id.list_history);
        rcv_result_search = findViewById(R.id.rcv_result_search);
        linearLayout_search = findViewById(R.id.linear_search);
        linearlayout_specialplace = findViewById(R.id.layout_special_places);

        fragmentManager = getSupportFragmentManager();
        mGoogleApiClient = MapsActivity.mGoogleApiClient;

        linearLayout_search.setVisibility(View.GONE);
        linearlayout_specialplace.setVisibility(View.VISIBLE);
        arrLatlng = new ArrayList<>();
    }

    public void solve(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectLocation.this, SearchActivity.class);
                startActivity(i);
            }
        });
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count_voice == 1) {
                    startVoiceRecognition();
                    count_voice++;
                }
                else {
                    edtLocation.setText(null);
                    startVoiceRecognition();
                    count_voice++;
                }
            }
        });
        edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check_search){
                    linearLayout_search.setVisibility(View.VISIBLE);
                    linearlayout_specialplace.setVisibility(View.GONE);
                    check_search = !check_search;
                    Log.e(TAG, "boolean check search: " + check_search);
                }
                edtLocation.setFocusableInTouchMode(true);
                edtLocation.setFocusable(true);
                edtLocation.requestFocus();
                //Show bàn phím
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                FragmentTransaction ft_add = fragmentManager.beginTransaction();
                ft_add.add(R.id.content_search, new frag_search());
                ft_add.commit();
            }
        });
        listSpecial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    // vị trí của bạn
                    Intent intent = new Intent(SelectLocation.this, SearchActivity.class);
                    LatLng mCurr = new LatLng(MapsActivity.mLastLocation.getLatitude(), MapsActivity.mLastLocation.getLongitude());
                    if(receive.equals("Origin")){
                        String ss = getAddress(MapsActivity.mLastLocation.getLatitude(), MapsActivity.mLastLocation.getLongitude());
                        arrLatlng.add(0, mCurr);
                        intent.putExtra("origin", ss);
                    } else {
                        // receive = "Destination"
                        String ss = getAddress(MapsActivity.mLastLocation.getLatitude(), MapsActivity.mLastLocation.getLongitude());
                        intent.putExtra("destination", ss);
                        arrLatlng.add(1, mCurr);
                    }
                    startActivity(intent);

                } else {
                    // chọn trên bản đồ
                    Intent intent  = new Intent(SelectLocation.this, MapsActivity.class);
                    startActivity(intent);






                }
            }
        });
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(SelectLocation.this, Locale.getDefault());
        String add = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(SelectLocation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return add;
    }

    // search by voice
    public void startVoiceRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching ....");

        startActivityForResult(intent, MapsActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MapsActivity.REQUEST_CODE && resultCode == RESULT_OK){
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(!matches.isEmpty()){
                String query = matches.get(0);
                edtLocation.setText(query);
                final int position = edtLocation.length();
                //Log.e(TAG, "POSITION: "+ position);

                edtLocation.post(new Runnable() {
                    @Override
                    public void run() {
                        edtLocation.setSelection(edtLocation.length());
                    }
                });


            }
        }

    }

    public void handsearch(){
        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, TConfigs.BOUNDS_DEF, null);
        mLinearLayout = new LinearLayoutManager(this);
        rcv_result_search.setLayoutManager(mLinearLayout);
        rcv_result_search.setAdapter(mAutoCompleteAdapter);


        edtLocation.addTextChangedListener(new TextWatcher() {
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

                        Log.i("TAG", "Autocomplete item selected: " + item.placeName);
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

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title(places.get(0).getName().toString());
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    MapsActivity.mCurrLocationMarker = MapsActivity.mMap.addMarker(markerOptions);

                                    //move map camera
                                    MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
