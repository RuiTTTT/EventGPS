package com.example.rui.eventgps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ray on 2018/6/19.
 */

public class RouteConnectUtil {
    private static final String TAG = "conn";
    static HttpURLConnection urlConnection;

    public static List<LatLng> sendRouteRequest(Map<String, String> searchTerm) {
        List<LatLng> mRoutePoints = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        String mSearchStart = searchTerm.get("mStartLat")+ ',' + searchTerm.get("mStartLng");
        String mSearchDes = searchTerm.get("mDesLat") + "," + searchTerm.get("mDesLng");

        try {
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/googlemaps-api/route/"+mSearchStart+"/"+mSearchDes);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        try {
            JSONObject jsonData = new JSONObject(result.toString());
            int len = jsonData.length();
            Log.d(TAG, "length: "+len);
            for (int i = 0; i < len; i++) {
                JSONObject geoData = (JSONObject) jsonData.get(Integer.toString(i));
                mRoutePoints.add(new LatLng(geoData.getDouble("lat"), geoData.getDouble("lng")));
                Log.d(TAG, "sendRouteRequest: "+Double.toString(geoData.getDouble("lat"))+Double.toString(geoData.getDouble("lng")));
            }
            return mRoutePoints;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
