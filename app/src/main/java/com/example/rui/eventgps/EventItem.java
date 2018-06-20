package com.example.rui.eventgps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ray on 2018/6/12.
 */

public class EventItem {
    private String venue;
    private String title;
    private String startTime;
    private String endTIme;
    private LatLng latLng;

    public EventItem(String venue, String title, String startTime, String endTime, LatLng latLng) {
        this.venue = venue;
        this.title = title;
        this.startTime = startTime;
        this.endTIme = endTime;
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

    public String getInfo() {
        return ("Event: " + title + " Start Time: " + startTime + " End Time" + endTIme);
    }
}
