package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

public class ItemList {
    private String name;
    private int image;

    public ItemList(String name, int image) {
        this.name = name;
        this.image = image;
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
}
