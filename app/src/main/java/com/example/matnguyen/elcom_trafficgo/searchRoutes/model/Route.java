package com.example.matnguyen.elcom_trafficgo.searchRoutes.model;

import java.util.List;

public class Route {
    private String start;
    private String end;
    private String time;
    private String distance;
    List<Step> steps;

    public Route(String start, String end, String time, String distance, List<Step> steps) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.distance = distance;
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
