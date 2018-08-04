package com.example.rui.eventgps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Class for custom info window in event view. Displaying venue name, event title, start time and
 * end time by clicking the marker on the map. Info is stored as EventItem object type.
 * Created by rui on 2018/6/13.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.popup_layout, null);

        TextView mTitle = view.findViewById(R.id.venue);
        TextView mEvent = view.findViewById(R.id.event);
        TextView mStart = view.findViewById(R.id.startTime);
        TextView mEnd = view.findViewById(R.id.endTime);

        mTitle.setText(marker.getTitle());
        EventItem eventItem = (EventItem) marker.getTag();
        mEvent.setText("Event: " + eventItem.getTitle());
        mStart.setText("Start Time: " + eventItem.getStartTime());
        mEnd.setText("End Time: " + eventItem.getEndTIme());
        return view;
    }
}
