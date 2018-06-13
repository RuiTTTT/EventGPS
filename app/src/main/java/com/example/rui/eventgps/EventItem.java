package com.example.rui.eventgps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ray on 2018/6/12.
 */

public class EventItem {
    private String venue;
    private String title;
    private String time;
    private LatLng latLng;

    public EventItem(String venue, String title, String time, LatLng latLng) {
        this.venue = venue;
        this.title = title;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return ("Event: " + title + " Start Time: " + time);
    }
}
