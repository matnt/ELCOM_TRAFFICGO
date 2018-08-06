package com.example.matnguyen.elcom_trafficgo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class GoogleServices implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //private Context context;
    public  GoogleApiClient mGoogleAPIClient;
    private static GoogleServices sInstance = null;

    public GoogleServices() {
        super();
    }

    public static synchronized GoogleServices getInstance() {
        if (sInstance == null) {
            sInstance = new GoogleServices();
        }
        return sInstance;
    }

    public GoogleApiClient buildGoogleApiClient(Context context) {
        mGoogleAPIClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        return mGoogleAPIClient;
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
