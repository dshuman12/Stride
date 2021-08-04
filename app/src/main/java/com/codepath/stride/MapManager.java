package com.codepath.stride;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

// Encapsulate all map related initialization
public class MapManager implements OnMapReadyCallback {

    private static final int REQUEST_FINE_LOCATION = 34;
    private static final String TAG = "MapManager";
    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;

    private LatLng mOrigin;
    private LatLng mDest;
    private JSONObject mDestInfo;
    private Marker mDestMarker;
    private Polyline mRoute;
    private JSONObject mRouteInfo;

    private Activity mActivity;
    private Context mContext;
    private Double mAvgWalkability;
    private Integer mCountWalkability;

    public MapManager(Activity activity, Context context, SupportMapFragment mapFragment) {
        mActivity = activity;
        mContext = context;
        mAvgWalkability = 0.0;
        mCountWalkability = 0;

        mapFragment.getMapAsync(this);
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (checkPermissions()) {
            // If the location permissions were not granted...
            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(@NonNull LatLng latLng) {
                    if (mDestMarker != null) {
                        resetDestination();
                    }
                    addPointToMap(latLng);
                }
            });
        }
    }

    public void searchDestination(String query) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                Log.i(TAG, "search destination: " + jsonObject.toString());
                try {
                    JSONArray results = jsonObject.getJSONArray("candidates");
                    if (results.length() > 0) {
                        mDestInfo = results.getJSONObject(0);
                        JSONObject top_candidate = (JSONObject) mDestInfo.get("geometry");
                        JSONObject location = (JSONObject) top_candidate.get("location");
                        Double latitude = (Double) location.get("lat");
                        Double longitude = (Double) location.get("lng");
                        LatLng destination = new LatLng(latitude, longitude);
                        addPointToMap(destination);
                        //Add route
                        addRouteToMap();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        };
        PlacesClient.getTopCandidateFromQuery(query, handler);
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Display Location
        if (latLng != null) {
            mOrigin = latLng;
        } else {
            Toast.makeText(mActivity, "Current location was null, please input your location in the settings page", Toast.LENGTH_SHORT).show();
        }
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(mActivity);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(mActivity);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(mActivity).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        Location lastLocation = locationResult.getLastLocation();
                        onLocationChanged(lastLocation);

                        // If there is no destination to focus on, zoom in on current location
                        if (mDest == null) {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                                    17);
                            mMap.animateCamera(cameraUpdate);
                        }

                    }
                },
                Looper.myLooper());
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(mActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    private void addPointToMap(@NonNull LatLng latLng) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        BitmapDescriptor customMarker =
                BitmapDescriptorFactory.fromResource(R.drawable.pin);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(customMarker));
        mDestMarker = marker;
        mDest = latLng;

        // Display the found location to the User by navigating to pin location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    private void addRouteToMap() {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    if (routes.length() > 0) {
                        mRouteInfo = routes.getJSONObject(0);
                        JSONArray legs = mRouteInfo.getJSONArray("legs");
                        if (legs.length() > 0) {
                            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
                            if (steps.length() > 0) {
                                // Get the display route JSON response
                                PolylineOptions options = DirectionsClient.createDisplayRoute(steps);
                                options.color(ContextCompat.getColor(mContext, R.color.dark_tan));
                                options.endCap(new RoundCap());
                                options.width(30);
                                // Add display route to the map
                                mRoute = mMap.addPolyline(options);
                                getSidewalkContext(steps);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Found error with accessing routes");
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        };

        if (mRoute != null) {
            mRoute.remove();
        }

        DirectionsClient.getRouteFromLocations(mOrigin, mDest, handler);
    }

    public void getSidewalkContext(JSONArray route) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    Integer score = (Integer) jsonObject.get("walkscore");
                    mAvgWalkability += score.doubleValue();
                    mCountWalkability += 1;
                    if (mCountWalkability >= route.length()) {
                        ((MainActivity)mContext).openRouteInfo();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    ((MainActivity)mContext).openRouteInfo();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        };

        for(int i = 0; i < route.length(); i++) {
            try {
                //If distance between startloc and endloc is > 100 meters
                // Get midpoint between two lat lons
                JSONObject step = (JSONObject) route.get(i);
                JSONObject startLoc = (JSONObject) step.get("start_location");
                JSONObject endLoc = (JSONObject) step.get("end_location");
                /*double stepDistance = distance((double) startLoc.get("lat"), (double) endLoc.get("lng"),
                        (double) startLoc.get("lat"), (double) endLoc.get("lng"));
                if (stepDistance >= 100.0) {
                    LatLng midpoint = midPoint((double) startLoc.get("lat"), (double) endLoc.get("lng"),
                            (double) startLoc.get("lat"), (double) endLoc.get("lng"));
                    String midpointQuery = "lat=" + midpoint.latitude + "&lon=" + midpoint.longitude;
                    SidewalkConditionsClient.getWalkabilityScore(midpointQuery, handler);
                }*/

                String query = "lat=" + startLoc.get("lat") + "&lon=" + startLoc.get("lng");
                SidewalkConditionsClient.getWalkabilityScore(query, handler);
            }
            catch (JSONException e) {
                Log.d(TAG, "Issue accessing step from JSON response");
            }
        }
    }


    public static ArrayList<LatLng> midpoints(double lat1, double lat2, double lon1,
                                              double lon2, ArrayList<LatLng> built) {
        double stepDistance = distance(lat1, lon1, lat2, lon2);
        if (stepDistance >= 100.0) {
            LatLng midpoint = midPoint(lat1, lon1,
                    lat2, lon2);
            midpoints(lat1, midpoint.latitude, lon1, midpoint.longitude, built);
            midpoints(midpoint.latitude, lat2, midpoint.longitude, lon2, built);
        }
        built.add(new LatLng(lat1, lon1));
        return built;
    }

    public static LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        return new LatLng(lat3, lon3);
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public JSONObject getmRouteInfo() {
        return mRouteInfo;
    }

    public JSONObject getmDestInfo() {
        return mDestInfo;
    }

    public Integer getWalkScore(TextView mWalkScoreView) {
        if (mCountWalkability == 0) {
            mWalkScoreView.setVisibility(View.INVISIBLE);
            return 0;
        }
        mWalkScoreView.setVisibility(View.VISIBLE);
        double walkscore = mAvgWalkability / mCountWalkability;
        return (int) walkscore;
    }

    public void getPhoto(ImageView im) {
        String photoReference = null;
        try {
            JSONObject photo = (JSONObject) mDestInfo.getJSONArray("photos").getJSONObject(0);
            photoReference = photo.getString("photo_reference");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (photoReference == null) {
            // There was an issue in grabbing the photo reference from the json response
            Log.i(TAG, "onFailure: Issue grabbing photo reference from Place response");
            return;
        }
        String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                + photoReference +"&key=" + BuildConfig.MAPS_API_KEY;
        Glide.with(mActivity)
                .load(imageUrl)
                .centerCrop()
                .into(im);
    }

    public void resetDestination() {
        mDestInfo = new JSONObject();
        mRouteInfo = new JSONObject();
        mDest = null;
        mAvgWalkability = 0.0;
        mCountWalkability = 0;

        mDestMarker.remove();
        mRoute.remove();
    }

}
