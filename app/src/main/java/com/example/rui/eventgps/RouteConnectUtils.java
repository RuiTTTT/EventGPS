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
        List<List> routeData = new ArrayList<>();
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
//            JSONObject jsonRoute = (JSONObject) jsonData.getJSONObject("0");
            int len = jsonData.length();
            Log.d(TAG, "length: "+len);
            for (int j = 0; j<len; j++) {
                List<String> mRoutes = new ArrayList<>();
                JSONObject jsonRoute = (JSONObject) jsonData.getJSONObject(Integer.toString(j));
                String route = jsonRoute.getString("0");
                mRoutes.add(route);
                String timeEstimated = jsonRoute.getString("1");
                mRoutes.add(timeEstimated);
                Log.d(TAG, "Json" + mRoutes);
                routeData.add(mRoutes);
                }
            Log.d(TAG, "sendRouteRequest: "+routeData.toString());
            return routeData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
