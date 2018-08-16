package com.example.rui.eventgps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * Created by rui on 2018/6/9.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleApiClient.OnConnectionFailedListener{
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private AutoCompleteTextView mStartText;
    private AutoCompleteTextView mDesText;
    private Circle mCircle;
    private Button clearStart;
    private Button clearDes;
    private Button navButton;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String currentDate;
    private String currentTime;
    private static final String TAG = "MapFragment";
    private static final float DEFAULT_ZOOM = 10f;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private Map<String,String> mSearchData = new HashMap<>();
    private List<List> routeResult = new ArrayList<>();
    private List<EventItem> eventResult = new ArrayList<>();
    private List<Integer> polyColor = new ArrayList<>();
    private Map<Integer, String> polyTimeSet = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) myView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        mStartText = (AutoCompleteTextView) myView.findViewById(R.id.startText);
        mDesText = (AutoCompleteTextView) myView.findViewById(R.id.desText);
        clearStart = (Button) myView.findViewById(R.id.clearStart);
        clearDes = (Button) myView.findViewById(R.id.clearDes);
        clearStart.setVisibility(View.INVISIBLE);
        clearDes.setVisibility(View.INVISIBLE);
        navButton = (Button) myView.findViewById(R.id.go);

        //Get the system date and time for making route and event check.
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        currentTime = df.format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "onCreateView: " + currentDate + ' ' + currentTime);

        //Set the default search data as empty, so the connection is not made with empty search term.
        mSearchData.put("mStartLat", "");
        mSearchData.put("mStartLng", "");
        mSearchData.put("mDesLat", "");
        mSearchData.put("mDesLng", "");

        //set the source and destination search box in a single line.
        mStartText.setSingleLine();
        mDesText.setSingleLine();

        //The preset colour for route display.
        polyColor.add(Color.RED);
        polyColor.add(Color.BLUE);
        polyColor.add(Color.GREEN);

        //A listener to detect text change. If so, show the clear button to clear the search box.
        mStartText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    clearStart.setVisibility(View.VISIBLE);
                } else {
                    clearStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //A listener to detect text change. If so, show the clear button to clear the search box.
        mDesText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    clearDes.setVisibility(View.VISIBLE);
                } else {
                    clearDes.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //The clear button listener to clear the search box and hide the button afterward.
        clearStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartText.setText("");
                clearStart.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        //The clear button listener to clear the search box and hide the button afterward.
        clearDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesText.setText("");
                clearDes.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        initMap();
        initSearch();

        return myView;
    }

    /**
     * The method handle pause action when our app is not in the front.
     * Error may occur for the autocomplete search box using google api client.
     * Disconnect the connection first to fix it.
     */
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    private void initMap() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Load Map", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        //Show some google map built-in map tools.
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);

        //Get device location as the current location for the default source.
        getDeviceLocation();

        //Listener handling click event on route polylines. Show estimated travel time after clicking.
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                int mPolyColor = polyline.getColor();
                String routeEstimateTime = polyTimeSet.get(mPolyColor);
                //Red polyline is the primary route.
                if(mPolyColor == Color.RED) {
                    Snackbar.make(myView, "Estimated Time: " + routeEstimateTime + " (primary)", 3000)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(myView, "Estimated Time: " + routeEstimateTime + " (alternate)", 3000)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    /**
     * The method for initializing two search box and connect to google place api using google
     * api client for search term autocompletion.
     */
    private void initSearch() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);

        //The click listener for start search box.
        //After user click the autocomplete item, the lat/lng information of source will be updated
        //as value in mSearchData which will then be passed to the server.
        mStartText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
                String sta = mStartText.getText().toString();
                Log.d(TAG, "onItemClick: "+ sta);
                LatLng mStartResult = searchLocation(sta);
                String mStartLat = Double.toString(mStartResult.latitude);
                String mStartLng = Double.toString(mStartResult.longitude);
                mSearchData.put("mStartLat", mStartLat);
                mSearchData.put("mStartLng", mStartLng);
                Log.d(TAG, "startLat: "+mStartLat);
                Log.d(TAG, "startLng: "+mStartLng);
            }
        });
        mStartText.setAdapter(mPlaceAutocompleteAdapter);
        //Handle the user enter event for clicking key done, down, enter, search
        mStartText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                }
                return false;
            }
        });

        //The click listener for destination search box.
        //After user click the autocomplete item, the lat/lng information of destination will be
        //updated as value in mSearchData which will then be passed to the server.
        mDesText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard();
                String des = mDesText.getText().toString();
                Log.d(TAG, "onItemClick: "+ des);
                LatLng mDesResult = searchLocation(des);
                String mDesLat = Double.toString(mDesResult.latitude);
                String mDesLng = Double.toString(mDesResult.longitude);
                mSearchData.put("mDesLat", mDesLat);
                mSearchData.put("mDesLng", mDesLng);
                Log.d(TAG, "desLat: "+mDesLat);
                Log.d(TAG, "desLng: "+mDesLng);
            }
        });
        mDesText.setAdapter(mPlaceAutocompleteAdapter);
        //Handle the user enter event for clicking key done, down, enter, search
        mDesText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                }
                return false;
            }
        });

        //The button to start route search.
        //Call method for posting the source/destination data to the server unless it is empty.
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchData.get("mStartLat") != "" && mSearchData.get("mStartLng") != "" && mSearchData.get("mDesLat") != "" && mSearchData.get("mDesLng") != "")
                    try {
                        postData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Method for decoding the autocomplete return result from google server.
     * For a search location, it first decode the top result. Then get the latitude and longitude
     * of the location if it's not empty.
     * @param searchTerm The String tpye search term user enters in
     * @return A LatLng type geolocation of the search term.
     */
    public LatLng searchLocation(String searchTerm) {
        hideKeyboard();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> returnList = new ArrayList<>();
        try {
            //Set the max number of result return as 1.
            returnList = geocoder.getFromLocationName(searchTerm, 1);
        } catch (IOException e) {
            Log.e(TAG, "Location IOException");
        }

        //The return list is empty if a invalid search term entered.
        if (!returnList.isEmpty()) {
            Address mAddress = returnList.get(0);
            LatLng mLoc = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
            return mLoc;

        }
        return null;
    }

    /**
     * The method for hiding virtual keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mStartText.getWindowToken(), 0);
    }

    /**
     * Method for getting the device current location as the default source in the search box.
     */
    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15f));
                            mSearchData.put("mStartLat", Double.toString(location.getLatitude()));
                            mSearchData.put("mStartLng", Double.toString(location.getLongitude()));
                        }
                    }
                });
    }

    /**
     * Method for posting search data to the server using the two Utils class.
     * The task is running on a new thread to avoid stuck the main thread to enhance user experience
     * and speed.
     * The result get back from web server contains 2-3 encoded route path and their corresponding
     * estimated travel time. Use drawPolyLineOnMap method to draw the route and show information
     * at the bottom using Snackbar.
     * @throws InterruptedException
     */
    private void postData() throws InterruptedException {
        Log.d(TAG, "post once");
        //Create a new thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Call two web api
                routeResult = RouteConnectUtils.sendRouteRequest(mSearchData);
//                Log.i(TAG, "run: "+ routeResult.size());
                eventResult = EventConnectUtils.sendEventRequest(currentDate, currentTime);
//                Log.i(TAG, "run: "+ eventResult.size());
            }
        });
        thread.start();
        //Join the thread back to main thread
        thread.join();
        mMap.clear();
        for (int i = 0; i < routeResult.size(); i++) {
            List<String> routeInfo = routeResult.get(i);
            String route = routeInfo.get(0);
            //Decode the route path into a list
            List<LatLng> decodedPath = PolyUtil.decode(route);
            //Draw route on the map
            drawPolyLineOnMap(decodedPath, polyColor.get(i));
            String routeTime = routeInfo.get(1);
            polyTimeSet.put(polyColor.get(i), routeTime);
        }

        //Show result information at the bottom
        if(!eventResult.isEmpty()) {
            addEventOnMap(eventResult);
//            Log.d(TAG, "Add event on map: "+eventResult.size());
            Snackbar.make(myView, eventResult.size() + " Event(s) found along the route", 5000)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(myView, "Currently no event", 5000)
                    .setAction("Action", null).show();
        }

    }

    /**
     * Method for drawing polyline to display route path on the map.
     * @param list List of LatLng type points along the route.
     * @param color The colour of polyline, red, blue and green.
     */
    public void drawPolyLineOnMap(List<LatLng> list, int color) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(color);
        polyOptions.width(12);
        polyOptions.addAll(list);
        polyOptions.clickable(true);

        mMap.addPolyline(polyOptions);

        if(! list.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : list) {
                builder.include(latLng);
            }

            final LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, mMapView.getWidth(), mMapView.getHeight(),120);
            mMap.animateCamera(cu);
        }

    }

    /**
     * Method adding events on the map using markers.
     * By clicking the marker, a information box containing details will be displayed.
     * A radius around event venue is also drawn on the map.
     * @param list A list of event in EventItem type.
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
}
