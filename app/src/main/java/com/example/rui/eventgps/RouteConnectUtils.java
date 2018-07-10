package com.example.rui.eventgps;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
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

public class RouteConnectUtils {
    private static final String TAG = "RouteConnect";
    static HttpURLConnection urlConnection;

    public static List<List> sendRouteRequest(Map<String, String> searchTerm) {
//        List<LatLng> mRoutePoints = new ArrayList<>();
        List<List> mRoutes = new ArrayList<>();
        StringBuilder responseResult = new StringBuilder();
        String mSearchStart = searchTerm.get("mStartLat")+ ',' + searchTerm.get("mStartLng");
        String mSearchDes = searchTerm.get("mDesLat") + "," + searchTerm.get("mDesLng");

        try {
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/googlemaps-api/route/" + mSearchStart + "/" + mSearchDes);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                responseResult.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        try {
            JSONObject jsonData = new JSONObject(responseResult.toString());
            int len = jsonData.length();
            Log.d(TAG, "length: "+len);
            for (int j = 0; j<len; j++) {
                List<LatLng> mRoutePoints = new ArrayList<>();
                JSONObject route = (JSONObject) jsonData.getJSONObject(Integer.toString(j));
//                JSONArray route = jsonData.getJSONArray(Integer.toString(j));
                Log.d(TAG, "Json" + route.toString());
                int length = route.length();
                for (int i = 0; i < length; i++) {
                    JSONObject geoData = (JSONObject) route.getJSONObject(Integer.toString(i));
//                    JSONObject geoData = route.getJSONObject(i);
//                    Log.d(TAG, "Json" + geoData.toString());

                    mRoutePoints.add(new LatLng(geoData.getDouble("lat"), geoData.getDouble("lng")));
//                    Log.d(TAG, "sendRouteRequest: "+Double.toString(geoData.getDouble("lat"))+Double.toString(geoData.getDouble("lng")));
                }

                mRoutes.add(mRoutePoints);
                Log.d(TAG, "sendRouteRequest: "+mRoutes.toString());
//                mRoutePoints.clear();
            }
            Log.d(TAG, "sendRouteRequest: "+mRoutes.toString());
            return mRoutes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
