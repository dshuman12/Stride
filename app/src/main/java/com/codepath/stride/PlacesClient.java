package com.codepath.stride;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class PlacesClient {

    //TODO: Add more specific fields such as: &fields=photos,formatted_address,name,rating,opening_hours,geometry
    public static final String PLACES_CANDIDATES = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=geometry&key=AIzaSyBIF-MI96VV6LerlB1fl-ZZX1WhzQv013M";
    public static final String TAG = "PlacesClient";

    public static void getTopCandidateFromQuery(String query, JsonHttpResponseHandler handler) {
        final LatLng[] latLng = new LatLng[1];
        // Replaces whitespace with space tag
        query = query.replace(" ", "%20");

        AsyncHttpClient client = new AsyncHttpClient();
        query = String.format(PLACES_CANDIDATES, query);
        // Calls the API to get the location from the query text
        client.get(query, handler);
    }
}
