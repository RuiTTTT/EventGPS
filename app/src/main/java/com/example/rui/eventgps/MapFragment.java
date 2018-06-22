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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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
 * Created by ray on 2018/6/9.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleApiClient.OnConnectionFailedListener{
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private AutoCompleteTextView mStartText;
    private AutoCompleteTextView mDesText;
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
    private List<LatLng> routeResult = new ArrayList<>();
    private List<EventItem> eventResult = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_map, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        currentTime = df.format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "onCreateView: " + currentDate + ' ' + currentTime);

        mSearchData.put("mStartLat", "");
        mSearchData.put("mStartLng", "");
        mSearchData.put("mDesLat", "");
        mSearchData.put("mDesLng", "");

        mStartText.setSingleLine();
        mDesText.setSingleLine();

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

        clearStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartText.setText("");
                clearStart.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

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
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker in Dublin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));

//        mMap.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) getContext());
//        try {
//            if (mLocationPermission) {
//                mMap.setMyLocationEnabled(true);
//            }
//        } catch (SecurityException e) {
//            Log.e(TAG, "getDeviceLacation: SecurityException: " + e.getMessage());
//        }

        CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getContext());
        mMap.setInfoWindowAdapter(customInfoWindow);


    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }


    private void initSearch() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);

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
        mStartText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                    //search method
//                    searchLocation(mStartText);
                }
                return false;
            }
        });

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
        mDesText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                    //search method
//                    searchLocation(mDesText);
//                    drawPolyLineOnMap(mList);
                }
                return false;
            }
        });
        getDeviceLocation();

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

    public LatLng searchLocation(String searchTerm) {
        hideKeyboard();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> returnList = new ArrayList<>();
        try {
            returnList = geocoder.getFromLocationName(searchTerm, 1);
        } catch (IOException e) {
            Log.e(TAG, "Location IOException");
        }

        if (!returnList.isEmpty()) {
            Address mAddress = returnList.get(0);
            LatLng mLoc = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLoc, DEFAULT_ZOOM));
//            MarkerOptions options = new MarkerOptions().position(mLoc).title(mAddress.getAddressLine(0));
//            mMap.addMarker(options);
            return mLoc;

        }
        return null;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mStartText.getWindowToken(), 0);
    }

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
                        }
                    }
                });
    }

    private void postData() throws InterruptedException {
        Log.d(TAG, "post once");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                routeResult = RouteConnectUtils.sendRouteRequest(mSearchData);
                Log.i(TAG, "run: "+ routeResult.size());
                eventResult = EventConnectUtils.sendEventRequest(currentDate, currentTime);
//                Log.i(TAG, "run: "+ eventResult.size());
            }
        });
        thread.start();
        thread.join();
        mMap.clear();
        drawPolyLineOnMap(routeResult);
        if(!eventResult.isEmpty()) {
            addEventOnMap(eventResult);
//            Log.d(TAG, "Add event on map: "+eventResult.size());
            Snackbar.make(myView, eventResult.size() + " Event is found along the route", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(myView, "Currently no event", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    // Draw polyline on map
    public void drawPolyLineOnMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(12);
        polyOptions.addAll(list);

        //mMap.clear();
        mMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, mMapView.getWidth(), mMapView.getHeight(),100);
        mMap.animateCamera(cu);
    }

    private void addEventOnMap(List<EventItem> list) {
        for (EventItem item : list) {
            Marker m = mMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getVenue()));
            m.setTag(item);
        }

    }
}
