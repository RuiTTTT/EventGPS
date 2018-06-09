package com.example.rui.eventgps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2018/6/9.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleApiClient.OnConnectionFailedListener{
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private Boolean mLocationPermission;
    private AutoCompleteTextView mStartText;
    private AutoCompleteTextView mDesText;
    private Button clearStart;
    private Button clearDes;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "MapActivity";
    private static final int LOC_PERMISSION_REQUEST_CODE = 9002;
    private static final float DEFAULT_ZOOM = 10f;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

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

        checkGoogleService();
        getLoactionPermission();

        return myView;
    }


    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    private boolean checkGoogleService() {
        Log.d(TAG, "Check google services version");
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (isAvailable == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)) {
            //an error occured but we can resolve it
            Log.d(TAG, "UserResolvableError");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), isAvailable, 9001);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Google Play Service Unavailable", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLoactionPermission() {
        String[] locationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermission = true;
                //initMap();
                initSearch();
            } else {
                ActivityCompat.requestPermissions(getActivity(), locationPermission, LOC_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), locationPermission, LOC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermission = false;

        switch (requestCode) {
            case LOC_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermission = false;
                            return;
                        }
                    }
                    mLocationPermission = true;
                    initMap();
                    initSearch();
                }
            }
        }
    }

    private void initMap() {
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mMapView = (MapView) myView.findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.getMapAsync((OnMapReadyCallback) getContext());
        //mapFragment.getMapAsync((OnMapReadyCallback) getContext());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Load Map", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker in Dublin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));

//        mMap.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) getContext());
//        try {
//            if (mLocationPermission) {
//                mMap.setMyLocationEnabled(true);
//            }
//        } catch (SecurityException e) {
//            Log.e(TAG, "getDeviceLacation: SecurityException: " + e.getMessage());
//        }

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


        mStartText.setAdapter(mPlaceAutocompleteAdapter);
        mStartText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == KeyEvent.ACTION_DOWN
                        || actionId == KeyEvent.KEYCODE_ENTER) {
                    //search method
                    searchLocation(mStartText);
                }
                return false;
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
                    searchLocation(mDesText);
                }
                return false;
            }
        });
        getDeviceLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void searchLocation(AutoCompleteTextView searchView) {
        String searchTerm = searchView.getText().toString();
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLoc, DEFAULT_ZOOM));
            MarkerOptions options = new MarkerOptions().position(mLoc).title(mAddress.getAddressLine(0));
            mMap.addMarker(options);

        }
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
}
