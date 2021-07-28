package com.codepath.stride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.hardsoftstudio.widget.AnchorSheetBehavior;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

// Implement OnMapReadyCallback.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText mSearchQuery;
    private Button mBtnSearch;

    private MapManager mMapManager;

    private AnchorSheetBehavior<View> anchorBehavior;
    private TextView mName;
    private TextView mAddress;
    private TextView mRouteTime;

    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(" ");
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mMapManager = new MapManager(this, this, mapFragment);

        //TODO: Change origin to the location when the routing is selected, not current location.
        mMapManager.startLocationUpdates();

        mSearchQuery = findViewById(R.id.etPlaceSearch);
        mBtnSearch = findViewById(R.id.btnSearch);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String searchQuery = mSearchQuery.getText().toString();
                mMapManager.searchDestination(searchQuery);
                // Clear search query to indicate that a search has sent
                mSearchQuery.getText().clear();
            }
        });

        mName = findViewById(R.id.bottom_sheet_title);
        mAddress = findViewById(R.id.address);
        mRouteTime = findViewById(R.id.route_time);
        mImage = findViewById(R.id.image);

        anchorBehavior = AnchorSheetBehavior.from(findViewById(R.id.anchor_panel));
        anchorBehavior.setHideable(true);
        anchorBehavior.setState(AnchorSheetBehavior.STATE_HIDDEN);
        anchorBehavior.setAnchorSheetCallback(new AnchorSheetBehavior.AnchorSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @AnchorSheetBehavior.State int newState) {
                if (newState == AnchorSheetBehavior.STATE_HIDDEN) {
                    // If the bottom navigation bar is slid down, reset all values.
                    mMapManager.resetDestination();
                    mImage.setImageResource(0);
                    mName.setText("");
                    mAddress.setText("");
                    mRouteTime.setText("");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void openRouteInfo() {
        anchorBehavior.setState(AnchorSheetBehavior.STATE_COLLAPSED);

        JSONObject route = mMapManager.getmRouteInfo();
        JSONObject dest = mMapManager.getmDestInfo();

        try {
            mName.setText(dest.getString("name"));
            mAddress.setText(dest.getString("formatted_address"));
            Integer walkScore = mMapManager.getWalkScore(mRouteTime);
            mRouteTime.setText(walkScore.toString());
            if (mImage.getDrawable() == null) {
                mMapManager.getPhoto(mImage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Trigger new location updates at interval

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.imLogout) {
            // Logout is tapped, logout user and send back to login screen
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.imSettings) {
            // Settings Icon is tapped, open preferences
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}