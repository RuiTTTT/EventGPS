package com.example.rui.eventgps;

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
 * Created by ray on 2018/6/20.
 */

public class EventConnectUtils {
    private static final String TAG = "EventConnect";
    static HttpURLConnection urlConnection;

    public static List<EventItem> sendEventRequest(String date, String time) {
        List<EventItem> mEvents = new ArrayList<>();
        StringBuilder ResponseResult = new StringBuilder();


        try {
//            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/sql/result/"+ date + "/" + time);
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/sql/result/2018-06-27/19:00:00");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                ResponseResult.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        try {
            JSONObject jsonData = new JSONObject(ResponseResult.toString());
            int len = jsonData.length();
            Log.d(TAG, "length: "+len);
            for (int i = 0; i < len; i++) {
                JSONObject eventData = (JSONObject) jsonData.get(Integer.toString(i));
                LatLng geoLocation = new LatLng(eventData.getDouble("Latitude"), eventData.getDouble("Longitude"));
                mEvents.add(new EventItem(eventData.getString("VenueName"),
                                          eventData.getString("EventName"),
                                          eventData.getString("StartTime"),
                                          eventData.getString("EndTime"),
                                          geoLocation));
                Log.d(TAG, "sendEventRequest: ");
            }
            return mEvents;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
