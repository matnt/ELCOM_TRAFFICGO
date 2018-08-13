package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

public class ItemList {
    private String name;
    private int image;
    private double lat;
    private double lng;

    public ItemList(String name, int image) {
        this.name = name;
        this.image = image;
    }
    public ItemList(String name, int image, double lat, double lng){
        this.name = name;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
