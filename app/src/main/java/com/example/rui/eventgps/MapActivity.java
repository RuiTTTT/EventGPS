package com.example.rui.eventgps;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationClickListener {

    private GoogleMap mMap;
    private Boolean mLocationPermission;
    private static final String TAG = "MapActivity";
    private static final int LOC_PERMISSION_REQUEST_CODE = 9002;
    private static final float DEFAULT_ZOOM = 10f;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        checkGoogleService();
        getLoactionPermission();
    }

    private boolean checkGoogleService() {
        Log.d(TAG, "Check google services version");
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(isAvailable == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            //an error occured but we can resolve it
            Log.d(TAG, "UserResolvableError");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, isAvailable, 9001);
            dialog.show();
        }else{
            Toast.makeText(this, "Google Play Service Unavailable", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLoactionPermission() {
        String[] locationPermission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermission = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,locationPermission, LOC_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, locationPermission, LOC_PERMISSION_REQUEST_CODE);
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
                }
            }
        }
    }

    private void initMap() {
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Load Map", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.addMarker(new MarkerOptions().position(DUBLIN).title("Marker in Dublin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));

        mMap.setOnMyLocationClickListener(this);
        try{
            if(mLocationPermission) {
                mMap.setMyLocationEnabled(true);
            }
        }catch(SecurityException e) {
            Log.e(TAG, "getDeviceLacation: SecurityException: " + e.getMessage());
        }

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
