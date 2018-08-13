package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {
    private String id;
    private String name;
    private double lat;
    private double lng;
    public Point(){

    }

    public Point(String id){
        this.id = id;
    }

    public Point(String id, String name, double lat, double lng){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }


    public Point(String name, double lat, double lng){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    protected Point(Parcel in) {
        id = in.readString();
        name = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);


    }
}
