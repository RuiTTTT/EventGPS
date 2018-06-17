package com.example.rui.eventgps;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.util.HttpUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Button go;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 10f;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    private List<LatLng> mList = new ArrayList<>();
    private Map<String,String> mSearchData = new HashMap<>();

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
        go = (Button) myView.findViewById(R.id.go);

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

        mList.add(new LatLng(53.35070589999999, -6.2605315));
        mList.add(new LatLng(53.3503754, -6.260378800000001));
        mList.add(new LatLng(53.35053749999999, -6.2593267));
        mList.add(new LatLng(53.352143, -6.2600234));
        mList.add(new LatLng(53.3527318, -6.257536399999999));
        mList.add(new LatLng(53.3530294, -6.2560465));
        mList.add(new LatLng(53.3589865, -6.261892599999999));
        mList.add(new LatLng(53.3950778, -6.2406381));
        mList.add(new LatLng(53.4066723, -6.2297261));
        mList.add(new LatLng(53.4245086, -6.2190742));
        mList.add(new LatLng(53.4280665, -6.2272759));
        mList.add(new LatLng(53.42794929999999, -6.2290918));

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
//                    searchLocation(mStartText);
                }
                return false;
            }
        });

        mDesText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard();


        }
    };

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

    private void postData(final LatLng p) {
        Log.d(TAG, "post once");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                JSONObject post_result = null;
                String lat = Double.toString(p.latitude);
                String lng = Double.toString(p.longitude);
                params.put("lat", lat);
                params.put("lng", lng);
                try {
                    post_result = ConnectUtils.submitPostData(params, "utf-8");
                    Log.i("POST_RESULT", post_result.getString("a"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
}
