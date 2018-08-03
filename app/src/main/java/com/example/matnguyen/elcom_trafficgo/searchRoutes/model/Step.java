package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

import java.util.HashMap;

public class Step {
    private HashMap<String,String> Point;
    private String Time;
    private String Distance;
    private String Intructions;
    private String Vehicle;

    public Step() {
    }

    public Step(HashMap<String, String> point, String time, String distance, String intructions, String vehicle) {
        Point = point;
        Time = time;
        Distance = distance;
        Intructions = intructions;
        Vehicle = vehicle;
    }

    public String getVehicle() {
        return Vehicle;
    }

    public void setVehicle(String vehicle) {
        Vehicle = vehicle;
    }

    public HashMap<String, String> getPoint() {
        return Point;
    }

    public void setPoint(HashMap<String, String> point) {
        Point = point;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getIntructions() {
        return Intructions;
    }

    public void setIntructions(String intructions) {
        Intructions = intructions;
    }
}
