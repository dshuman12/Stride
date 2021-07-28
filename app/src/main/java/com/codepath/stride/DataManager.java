package com.codepath.stride;

import com.parse.ParseUser;

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

    public static DataManager fromJson(JSONObject jsonObject) throws JSONException {
        DataManager user = new DataManager();
        user.mName = jsonObject.getString("name");
        user.mScreenName = jsonObject.getString("screen_name");
        user.mPreferredMode = jsonObject.getString("profile_image_url_https");
        return user;
    }

    public ParseUser getUser() {
        return user;
    }

    public String getPreferredMode() {
        return user.getString("preferredMode");
    }
}
