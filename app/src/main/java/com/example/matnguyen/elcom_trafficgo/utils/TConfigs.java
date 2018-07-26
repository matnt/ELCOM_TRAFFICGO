package com.example.matnguyen.elcom_trafficgo.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class TConfigs {
    public static final int TIME_INTERVAL=5;
    public static final int TIME_FASTEST_INTERVAL=5;
    public static final int REQUEST_CODE_PERMISSION=1000;

    public static final String API_NOT_CONNECTED = "Google API not connected";
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static String PlacesTag = "Google Places Auto Complete";

    public static final LatLngBounds BOUNDS_DEF = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
}
