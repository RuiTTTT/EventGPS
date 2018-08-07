package com.example.rui.eventgps;

import com.google.android.gms.maps.model.LatLng;

/**
 * The constructor class for storing event details in a suitable object type.
 * Created by rui on 2018/6/12.
 */

public class EventItem {
    private String venue;
    private String title;
    private String startTime;
    private String endTIme;
    private Double radius;
    private LatLng latLng;

    public EventItem(String venue, String title, String startTime, String endTime, Double radius, LatLng latLng) {
        this.venue = venue;
        this.title = title;
        this.startTime = startTime;
        this.endTIme = endTime;
        this.radius = radius;
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getVenue() {
        return venue;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTIme() { return endTIme; }

    public Double getRadius() { return radius; }

    public String getInfo() {
        return ("Event: " + title + " Start Time: " + startTime + " End Time" + endTIme);
    }
}
