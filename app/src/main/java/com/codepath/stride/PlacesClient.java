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

    public static final String PLACES_CANDIDATES = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%s&inputtype=textquery&fields=photos,formatted_address,name,geometry&key=%s";
    public static final String TAG = "PlacesClient";

    public static void getTopCandidateFromQuery(String query, JsonHttpResponseHandler handler) {
        final LatLng[] latLng = new LatLng[1];
        // Replaces whitespace with space tag
        query = query.replace(" ", "%20");

        AsyncHttpClient client = new AsyncHttpClient();
        query = String.format(PLACES_CANDIDATES, query, BuildConfig.MAPS_API_KEY);
        // Calls the API to get the location from the query text
        client.get(query, handler);
    }
}
