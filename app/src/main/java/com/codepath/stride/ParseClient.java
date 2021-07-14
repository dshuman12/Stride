package com.codepath.stride;

import android.app.Application;

import com.parse.Parse;

public class ParseClient extends Application {
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("2p3sWGmzM37JcjYuvDO6D418wlexKGqeJeFmmvs8")
                .clientKey("ZBmHwODprTYQZ9fggHRkvqAwsuaIq9OyxgWHwknJ")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
