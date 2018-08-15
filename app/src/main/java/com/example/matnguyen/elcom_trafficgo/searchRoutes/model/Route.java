package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

import java.util.List;

public class Route {
    private int id;
    private String start;
    private String end;
    private String time;
    private String distance;
    List<Step> steps;
    public Route(){

    }

    public Route(String start, String end, String time, String distance, List<Step> steps) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.distance = distance;
        this.steps = steps;
    }

    public Route(int id, String start, String end, String time, String distance, List<Step> steps) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.time = time;
        this.distance = distance;
        this.steps = steps;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
