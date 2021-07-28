package com.codepath.stride;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {

    public String mName;
    public String mScreenName;
    public String mPreferredMode;

    public ParseUser user;

    // empty constructor for the Parceler library
    public DataManager(){
        user = ParseUser.getCurrentUser();
    }

    public ParseUser getUser() {
        return user;
    }

    public int getPreferredMode() {
        return user.getInt("preferredMode");
    }

    public void updatePreferredMode(int preferredMode) {
        user.put("preferredMode", preferredMode);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("SettingsActivity", "Error while saving", e);
                }
                Log.i("SettingsActivity", "Post save was successful");
            }
        });
    }
}
