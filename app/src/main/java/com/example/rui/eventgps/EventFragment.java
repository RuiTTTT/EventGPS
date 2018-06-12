package com.example.rui.eventgps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 2018/6/9.
 */

public class EventFragment extends Fragment implements OnMapReadyCallback {
    View myView;
    private MapView mMapView;
    private GoogleMap mMap;
    private static final LatLng DUBLIN = new LatLng(53.35, -6.26);
    private static final LatLng ARENA3 = new LatLng(53.347512, -6.228482);
    private static final LatLng AVIVA = new LatLng(53.335237, -6.228468);
    private static final LatLng AMVASSADOR = new LatLng(53.352809, -6.261987);
    private static final LatLng RDS = new LatLng(53.3257, -6.2297);
    private static final LatLng BORD = new LatLng(53.344201, -6.240274);
    private List<EventItem> eventList = new ArrayList<>();
    private static final float DEFAULT_ZOOM = 12f;
    private List<LatLng> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_event, container, false);

        mMapView = (MapView) myView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        return myView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Load Map", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        eventList.add(new EventItem("3 Arena", "Demi Lovate", "18:30", ARENA3));
        eventList.add(new EventItem("Aviva Stadium", "Guinness PRO14", "18:00", AVIVA));
        eventList.add(new EventItem("Ambassador Theatre", "Dinosaurs Around the World", "10:00", AMVASSADOR));
        eventList.add(new EventItem("RDS", "Dublin Horse Show", "9:00", RDS));
        eventList.add(new EventItem("Bord Gais Energy Theatre", "Wicked 2018", "19:30", BORD));
        addEventOnMap(eventList);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, DEFAULT_ZOOM));

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

        drawPolyLineOnMap(mList);

    }

    // Draw polyline on map
    public void drawPolyLineOnMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(10);
        polyOptions.addAll(list);

        //mMap.clear();
        mMap.addPolyline(polyOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
    }

    private void addEventOnMap(List<EventItem> list) {
        for (EventItem item : list) {
            mMap.addMarker(new MarkerOptions().position(item.getLatLng()).title(item.getInfo()));
        }

    }
}
