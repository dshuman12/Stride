package com.codepath.stride;

import android.os.Build;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.maps.model.LatLng;

public class SidewalkConditionsClient {
    public static final String CONDITIONS_ACCESS = "https://api.walkscore.com/score?format=json&%s&wsapikey=%s"; //"https://data.cityofnewyork.us/resource/ucwm-fgww.json%s";
    public static final String TAG = "SidewalkConditionsClient";

    public static void getWalkabilityScore(String query, JsonHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        query = String.format(CONDITIONS_ACCESS, query, BuildConfig.WALK_SCORE_API_KEY);
        // Calls the API to get the location from the query text
        client.get(query, handler);
    }
}
