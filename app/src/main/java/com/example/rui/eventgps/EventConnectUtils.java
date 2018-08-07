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
 * The class handling connection to our web api for retrieving event data.
 * HttpURLConnection is used for send request to the flask server.
 * Created by rui on 2018/6/20.
 */

public class EventConnectUtils {
    private static final String TAG = "EventConnect";
    static HttpURLConnection urlConnection;

    /**
     * The method for getting event happening along the route while checking. This method
     * cooperates with the route check to get real time info of event along the route.
     *
     * @param date The system date while making the request
     * @param time The system time while making the request
     * @return A List containing EventItem objects for event details.
     */
    public static List<EventItem> sendEventRequest(String date, String time) {
        List<EventItem> mEvents = new ArrayList<>();
        StringBuilder ResponseResult = new StringBuilder();


        try {
            //Our web api url
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/sql/result/"+ date + "/" + time);
//            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/sql/result/"+ date + "/" + "19:'00:00");
//            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/sql/result/2018-08-04/17:45:00");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //Handle responds from the server, converting input stream to String
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                ResponseResult.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            //Disconnect for the server after the call
            urlConnection.disconnect();
        }

        //Decode the json data getting from web api into EventItem type and store in a list
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
                                          eventData.getDouble("Radius"),
                                          geoLocation));
                Log.d(TAG, "sendEventRequest: ");
            }
            return mEvents;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The method for getting all the event in Dublin on the date selected.
     *
     * @param date The data selected using data picker in EventFragment
     * @return A List containing EventItem objects for event details.
     */
    public static List<EventItem> sendDailyEventRequest(String date) {
        List<EventItem> mEvents = new ArrayList<>();
        StringBuilder ResponseResult = new StringBuilder();


        try {
            //Our web api url
            URL url = new URL("http://csstudent02.ucd.ie:443/EventGPS-api/events/all/"+ date);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //Handle responds from the server, converting input stream to String
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                ResponseResult.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            //Disconnect for the server after the call
            urlConnection.disconnect();
        }

        //Decode the json data getting from web api into EventItem type and store in a list
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
                        eventData.getDouble("Radius"),
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
