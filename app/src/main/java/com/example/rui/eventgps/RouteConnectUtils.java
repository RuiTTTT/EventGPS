package com.example.rui.eventgps;

import android.util.Log;

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
 * The class handling connection to our web api for retrieving route data..
 * HttpURLConnection is used for send request to the flask server.
 * Created by rui on 2018/6/19.
 */

public class RouteConnectUtils {
    private static final String TAG = "RouteConnect";
    static HttpURLConnection urlConnection;

    /**
     * The method for getting route data and the estimated travel time.
     * There will be 2-3 routes and time returned from the server based on the number of
     * alternative routes.
     * The route data returned from the server is encoded in String type. It gets decode in Map
     * fragment before displaying.
     * @param searchTerm A Java map contains latitude and longitude of source ann destination
     * @return A nested list containing 2-3 lists for encoded route data and the estimate time.
     */
    public static List<List> sendRouteRequest(Map<String, String> searchTerm) {
        List<List> routeData = new ArrayList<>();
        StringBuilder responseResult = new StringBuilder();
        //Get the lat/lng geo-location and convert to a proper type for api call.
        String mSearchStart = searchTerm.get("mStartLat")+ ',' + searchTerm.get("mStartLng");
        String mSearchDes = searchTerm.get("mDesLat") + "," + searchTerm.get("mDesLng");

        try {
            //Our web api url
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/googlemaps-api/route/" + mSearchStart + "/" + mSearchDes);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //Handle responds from the server, converting input stream to String
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                responseResult.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            //Disconnect for the server after the call
            urlConnection.disconnect();
        }

        //Decode the json data getting from web api into Strings and store in a list
        try {
            JSONObject jsonData = new JSONObject(responseResult.toString());
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
//            Log.d(TAG, "sendRouteRequest: "+routeData.toString());
            return routeData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
