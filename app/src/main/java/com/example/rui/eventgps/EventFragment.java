package com.example.rui.eventgps;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * The class for event page. Showing all the events on the date selected through date picker.
 * The event data is get by making request using EventConnectUtils class on a new thread.
 * Created by rui on 2018/6/9.
 */

public class EventFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private Circle mCircle;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private String currentDate;
    private Calendar mCalendar;
    private int year, month, day;
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

        mCalendar = Calendar.getInstance();
        year = mCalendar.get(Calendar.YEAR);
        month = mCalendar.get(Calendar.MONTH);
        day = mCalendar.get(Calendar.DAY_OF_MONTH);

        //A floating action button at the bottom right of the screen.
        //Showing the date picker after clicking the button
        FloatingActionButton fab = myView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //The default date on the date picker is the current date
                        currentDate = Integer.toString(year) + '-' + Integer.toString(month+1) + '-' + Integer.toString(dayOfMonth);
                        try {
                            //Call method for making connection request
                            postData();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Set some map UI settings as true to activate some tools and gestures
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        //Display custom info window by clicking
        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.setOnInfoWindowClickListener(this);

        //Set the default camera location as Dublin with a proper default zoom as 12f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));
    }

    /**
     * Method for adding event marker and showing radius on the map.
     * @param list The list containing event data in EventItem type.
     */
    private void addEventOnMap(List<EventItem> list) {
        for (EventItem item : list) {
            Marker m = mMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getVenue()));
            m.setTag(item);
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(item.getLatLng())
                    .radius(item.getRadius() * 1000)
                    .strokeWidth(0)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.argb(180, 155, 191, 244))
                    .clickable(false));
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "click info window", Toast.LENGTH_SHORT).show();
        View v = getView();
        //showPopup(v);
    }

    /**
     * The method for sending http request to the server using method in EventConnectUtils.
     * The connection is handled in a new thread to avoid stuck the main thread.
     * Add markers of all the events returned.
     * @throws InterruptedException
     */
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
        //Clear map before adding new markers.
        mMap.clear();
        if(!eventList.isEmpty()) {
            addEventOnMap(eventList);
//            Log.d(TAG, "Add event on map: "+eventResult.size());
            Snackbar.make(myView, eventList.size() + " Event(s) found today", 5000)
                    .setAction("Action", null).show();
        } else {
            //No event is found on that date
            Snackbar.make(myView, "Currently no event", 5000)
                    .setAction("Action", null).show();
        }

    }

    /**
     * Method for displaying more detail information on a popup window after clicked.
     * The method has been deprecated because it may nor display properly under different
     * device resolution.
     * @param anchorView
     */
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
