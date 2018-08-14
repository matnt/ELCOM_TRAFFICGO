package com.example.matnguyen.elcom_trafficgo.searchRoutes.activities;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

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
import com.example.matnguyen.elcom_trafficgo.searchRoutes.services.LoadHistoryService;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.PlaceAutoCompleteAdapter;
import com.example.matnguyen.elcom_trafficgo.selectpoint.adapter.RecyclerItemClickListener;

import com.example.matnguyen.elcom_trafficgo.selectpoint.utils.TConfigs;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SelectLocation extends Fragment  {
    private static final String TAG = "SELECT LOCATION";
    public static final int ERROR_DIALOG_REQUET = 9001;

    private ImageButton btnBack, btnVoice;
    private EditText edtLocation;
    private ListView listSpecial, listHistory;
    private String receive;

    ///
    private int count_voice;
    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;

    private LinearLayoutManager mLinearLayout;
    private RecyclerView rcv_result_search;
    private boolean check_search = false;
    private LinearLayout linearLayout_search;
    private LinearLayout linearlayout_specialplace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_location_layout, container, false);

        Log.e(TAG, "go to select location");
        count_voice = 1;
        initWidget(view);
        getIntentFromSearchActivity();
        setContentList();
        getIntentFromHistory();
        solve();
        search();

        return view;
    }

    public void getIntentFromSearchActivity(){
        receive = getArguments().getString("Location");
        //receive = getActivity().getIntent().getStringExtra("Location");
        if(receive.equals("Origin")){
            edtLocation.setHint("Chọn điểm khởi hành");

        } else if(receive.equals("Destination")){

            edtLocation.setHint("Chọn điểm đến");
        }
        // hide key broad
        edtLocation.setFocusableInTouchMode(false);
        edtLocation.setFocusable(false);
        edtLocation.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtLocation.getWindowToken(), 0);
        Log.e(TAG, receive );
    }

    public void getIntentFromHistory(){
        ArrayList<ItemList> arrayList = new ArrayList<>();
        ArrayList<Point> arr = DatabasehistoryHelper.getInstance(getActivity()).getPoints();
        Log.e(TAG, "SIZE: " + arr.size());

        for(Point p : arr){

            String name = p.getName();
            double lat = p.getLat();
            double lng = p.getLng();
            ItemList item = new ItemList(name, R.drawable.ic_location, lat, lng);
            arrayList.add(item);
        }
        ListLocationAdapter adapter = new ListLocationAdapter(getActivity(), arrayList);
        listHistory.setAdapter(adapter);
    }

    public void setContentList(){
        ArrayList<ItemList> arrItem = new ArrayList<>();
        ItemList item1 = new ItemList("Vị trí hiện tại", R.drawable.ic_location);
        ItemList item2 = new ItemList("Chọn trên bản đồ", R.drawable.ic_my_location);
        arrItem.add(item1);
        arrItem.add(item2);


        ListLocationAdapter adapter = new ListLocationAdapter(getActivity(), arrItem);
        listSpecial.setAdapter(adapter);
    }

    public void initWidget(View view){
        btnBack = view.findViewById(R.id.btn_back);
        btnVoice = view.findViewById(R.id.btn_voice);
        edtLocation = view.findViewById(R.id.edt_location);
        listSpecial = view.findViewById(R.id.list_special_search);
        listHistory = view.findViewById(R.id.list_history);
        rcv_result_search = view.findViewById(R.id.rcv_result_search1);
        linearLayout_search = view.findViewById(R.id.linear_search);
        linearlayout_specialplace = view.findViewById(R.id.layout_special_places);

        linearLayout_search.setVisibility(View.GONE);
        linearlayout_specialplace.setVisibility(View.VISIBLE);
        //arrLatlng = new ArrayList<>();
    }

    public void solve(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                    fm.popBackStack();
                }
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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
        listSpecial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    // vị trí của bạn
                    LatLng mCurr = new LatLng(MapsActivity.mLastLocation.getLatitude(), MapsActivity.mLastLocation.getLongitude());
                    Log.e(TAG, "Current location: " + MapsActivity.mLastLocation.getLatitude() + ", " + MapsActivity.mLastLocation.getLongitude());
                    String ss = getAddress(mCurr);
                    Point point = new Point(ss, mCurr.latitude, mCurr.longitude);
                    Log.e(TAG, ss);

                    if(receive.equals("Origin")){
                        SearchFragment.arr.add(0, point);

                    } else {
                        SearchFragment.arr.add(1, point);

                    }
                    SearchFragment search = SearchFragment.newInstance(receive,  ss);
                    getChildFragmentManager().beginTransaction().replace(R.id.select, search).addToBackStack(null).commit();


                } else {
                    // chọn trên bản đồ
                    Intent intent  = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);

//                    SearchFragment search = SearchFragment.newInstance(receive, null, null);
//                    getChildFragmentManager().beginTransaction().replace(R.id.select, search).addToBackStack(null).commit();




                }
            }
        });

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemList item = (ItemList) listHistory.getItemAtPosition(i);
                String name = item.getName();
                double lat = item.getLat();
                double lng = item.getLng();
                Log.e(TAG, "NAME: " + name);
                Point p = new Point(name, lat, lng);

                // sent data to search fragment
                Log.e(TAG, "receive: " + receive);
                if(receive.equals("Origin")) {
                    SearchFragment.arr.add(0, p);

                } else if(receive.equals("Destination")){
                    SearchFragment.arr.add(1, p);

                }

                SearchFragment searchFragment = SearchFragment.newInstance(receive,  null);
                getChildFragmentManager().beginTransaction().replace(R.id.select, searchFragment).addToBackStack(null).commit();
            }
        });
    }

    public void search(){
        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(getActivity(), MapsActivity.mGoogleApiClient, TConfigs.BOUNDS_DEF, null);
        mLinearLayout = new LinearLayoutManager(getActivity());

        rcv_result_search.setLayoutManager(mLinearLayout);
        rcv_result_search.setAdapter(mAutoCompleteAdapter);

        edtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")  ) {
                    mAutoCompleteAdapter.getFilter().filter(charSequence.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                mAutoCompleteAdapter.getFilter().filter(text);
            }
        });

        rcv_result_search.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.e("TAG", "Autocomplete item selected: " + item.placeName);

                        //saveToDatabase();
                        Point p = new Point();
                        p.setId(String.valueOf(item.placeId));
                        p.setName(String.valueOf(item.placeName));
                        p.setLat(item.latitude);
                        p.setLng(item.longitude);

                        // add new point into database
                        DatabasehistoryHelper.getInstance(getActivity()).addNewPoint(p);
                        LoadHistoryService.launchLoadHistoryService(getActivity());
                        final Point point = new Point();
                        //Log.e(TAG, "PLACE ID: " + placeId + " ID: " + id);
                        //point.setId(placeId);

                        // update point in database
                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(MapsActivity.mGoogleApiClient, placeId);
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
                                    final int rowsUpdated = DatabasehistoryHelper.getInstance(getActivity()).updateHistory(point);
                                    Log.e(TAG, "ROW UPDATED: " + rowsUpdated);
                                    if (rowsUpdated == 1) {
                                        Toast.makeText(getActivity(), "Update success!", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "Update success!");
                                    } else {
                                        Toast.makeText(getActivity(), "Update fail!", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "Update fail!");
                                    }

//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(latLng);
//                                    markerOptions.title(places.get(0).getName().toString());
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

//                                    mCurrLocationMarker = mMap.addMarker(markerOptions);
//
//                                    //move map camera
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//
//                                    if (flag_search) {
//                                        flag_search = !flag_search;
//                                        ibtn_search.setImageResource(R.mipmap.ic_search);
//                                        rl_search.setVisibility(View.GONE);
//
//                                        Fragment fragment = fragmentManager.findFragmentById(R.id.content_search);
//                                        android.support.v4.app.FragmentTransaction ft_add = fragmentManager.beginTransaction();
//                                        ft_add.remove(fragment);
//                                        ft_add.commit();
//
//                                        //Ẩn bàn phím
//                                        edt_search.setFocusableInTouchMode(false);
//                                        edt_search.setFocusable(false);
//                                        edt_search.requestFocus();
//
//                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), 0);
//
//                                        edt_search.setText("");
//                                        mAutoCompleteAdapter.ClearData();
//                                    }
                                }

                            }
                        });

                        //bundle to searchActivity and MapsFragment
                        if(receive.equals("Origin")){
                            SearchFragment.arr.add(0, p);
                        } else {
                            SearchFragment.arr.add(1, p);
                        }

                        // go to SearchFragment
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("Point", p);
                        SearchFragment search = new SearchFragment();
                        search.setArguments(bundle);

                        getChildFragmentManager().beginTransaction().replace(R.id.select, new SearchFragment()).commit();

                    }
                })
        );

    }

    public String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        String add = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

//    public void handsearch(){
//        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(getActivity(), mGoogleApiClient, TConfigs.BOUNDS_DEF, null);
//        mLinearLayout = new LinearLayoutManager(getActivity());
//        rcv_result_search.setLayoutManager(mLinearLayout);
//        rcv_result_search.setAdapter(mAutoCompleteAdapter);
//
//
//        edtLocation.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!s.toString().equals(""))
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getActivity(), TConfigs.API_NOT_CONNECTED, Toast.LENGTH_LONG).show();
//                    Log.e(TConfigs.PlacesTag, TConfigs.API_NOT_CONNECTED);
//                    Log.e(TAG, "API  NOT CONNECTED");
//                }
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//
//        rcv_result_search.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
//                        final String placeId = String.valueOf(item.placeId);
//
//                        Log.i("TAG", "Autocomplete item selected: " + item.placeName);
//                    /*
//                         Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
//                     */
//
//                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
//                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                            @Override
//                            public void onResult(PlaceBuffer places) {
//                                if (places.getCount() == 1) {
//                                    //Do the things here on Click.....
//                                    LatLng latLng = places.get(0).getLatLng();
//
//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(latLng);
//                                    markerOptions.title(places.get(0).getName().toString());
//                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//                                    MapsActivity.mCurrLocationMarker = MapsActivity.mMap.addMarker(markerOptions);
//
//                                    if(receive.equals("Origin")){
//                                        //arrLatlng.add(0, latLng);
//                                        MapsActivity.arrayList.add(0, latLng);
//
//                                    } else if(receive.equals("Destination")){
//                                        //arrLatlng.add(1, latLng);
//                                        MapsActivity.arrayList.add(1, latLng);
//                                    }
//
//                                    //move map camera
//                                    MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//
//                                } else {
//                                    Toast.makeText(getActivity(), TConfigs.SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//                        Log.i("TAG", "Clicked: " + item.placeName);
//                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
//                    }
//                }));
//
//    }

}
