package com.example.rui.eventgps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.CrashUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by ray on 2018/6/9.
 */

public class EventFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private String currentDate;
    private List<EventItem> eventList = new ArrayList<>();
    private static final float DEFAULT_ZOOM = 12f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_event, container, false);

        mMapView = (MapView) myView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        FloatingActionButton fab = myView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Show recent events", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Log.d(TAG, "onCreateView: "+currentDate);
                try {
                    postData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(getContext(), "Load Map", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);

        mMap.setOnInfoWindowClickListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));


    }


    private void addEventOnMap(List<EventItem> list) {
        for (EventItem item : list) {
            Marker m = mMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getVenue()));
            m.setTag(item);
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "click info window", Toast.LENGTH_SHORT).show();
        View v = getView();
        //showPopup(v);
    }

    private void postData() throws InterruptedException {
        Log.d(TAG, "post once");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventList = EventConnectUtils.sendDailyEventRequest(currentDate);
            }
        });
        thread.start();
        thread.join();
        mMap.clear();
        if(!eventList.isEmpty()) {
            addEventOnMap(eventList);
//            Log.d(TAG, "Add event on map: "+eventResult.size());
            Snackbar.make(myView, eventList.size() + " Event found today", 5000)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(myView, "Currently no event", 5000)
                    .setAction("Action", null).show();
        }

    }

    public void showPopup(View anchorView) {

        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Example: If you have a TextView inside `popup_layout.xml`
        TextView tv = (TextView) popupView.findViewById(R.id.venue);

        tv.setText("aaa");


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());



//        int location[] = new int[2];
//
//        // Get the View's(the one that was clicked in the Fragment) location
//        anchorView.getLocationOnScreen(location);
//
//        // Using location, the PopupWindow will be displayed right under anchorView
//        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,
//                location[0], location[1]);
//        Log.d(TAG, Integer.toString(location[0]));
        int mWidth= this.getResources().getDisplayMetrics().widthPixels;
        int mHeight= this.getResources().getDisplayMetrics().heightPixels;
        Log.d(TAG,  Integer.toString(mWidth)+Integer.toString(mHeight));
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, mWidth/4, mHeight/4);
    }
}
