package com.codepath.stride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final String[] mArraySpinner = new String[] {
            "Walk", "Drive", "Bike", "Bus", "Train", "Taxi"
    };
    private TextView mTvUser;
    private TextView mTvName;
    private ImageView mIvProfilePhoto;
    private Spinner mPrefMode;
    private ParseUser mUser;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mTvName = findViewById(R.id.tvName);
        mTvUser = findViewById(R.id.tvUser);
        mIvProfilePhoto = findViewById(R.id.ivProfilePhoto);
        mPrefMode = findViewById(R.id.prefMode);
        mPrefMode.setOnItemSelectedListener(this);

        mDataManager = new DataManager();
        mUser = mDataManager.getUser();
        mTvUser.setText("@" + mUser.getString("username"));
        mTvName.setText(mUser.getString("name"));

        ParseFile image = mUser.getParseFile("profilePicture");
        if (image != null) {
            Log.i("ProfileFragment", image.toString() + image.getUrl());
            Glide.with(this).load(image.getUrl()).into(mIvProfilePhoto);
        }

        // Setting preferred mode options
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mArraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.i("SettingsActivity", "" + mUser.getInt("preferredMode"));
        mPrefMode.setAdapter(adapter);
        mPrefMode.setSelection(mDataManager.getPreferredMode());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Update User in Parse
        Log.i("SettingsActivity", "" + position + "" + id);
        mDataManager.updatePreferredMode(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("SettingsActivity", "Nothing is Selected");
    }
}