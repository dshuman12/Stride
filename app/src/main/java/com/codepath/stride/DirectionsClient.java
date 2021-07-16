package com.codepath.stride;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsClient {

    public static final String DIRECTIONS_ROUTE = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=%s";//https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s";
    public static final String TAG = "DirectionsClient";

    public static void getRouteFromLocations(LatLng origin, LatLng destination, JsonHttpResponseHandler handler) {
        String origin_str = origin.latitude + "," + origin.longitude;
        String destination_str = destination.latitude + "," + destination.longitude;

        AsyncHttpClient client = new AsyncHttpClient();
        String query = String.format(DIRECTIONS_ROUTE, origin_str, destination_str, BuildConfig.MAPS_API_KEY);
        client.get(query, handler);
    }

    public static PolylineOptions createDisplayRoute(JSONArray steps) throws JSONException {
        PolylineOptions route = new PolylineOptions();
        for (int i = 0; i < steps.length(); i++) {
            JSONObject start = (JSONObject) steps.getJSONObject(i).get("start_location");
            JSONObject end = (JSONObject) steps.getJSONObject(i).get("end_location");

            route.add(new LatLng(start.getDouble("lat"), start.getDouble("lng")));
            route.add(new LatLng(end.getDouble("lat"), end.getDouble("lng")));
        }
        return route;
    }
}
