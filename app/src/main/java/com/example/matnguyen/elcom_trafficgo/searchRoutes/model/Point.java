package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Point implements Parcelable {
    private long id;
    private String name;
    private double lat;
    private double lng;
    public Point(){

    }

    public Point(long id){
        this.id = id;
    }

    public Point(long id, String name, double lat, double lng){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public Point(long id, double lat, double lng){
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    protected Point(Parcel in) {
        id = in.readLong();
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

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
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
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);


    }
}
