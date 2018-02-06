package ir.adonet.example;

import android.app.Application;

import ir.adonet.analytics.ADAnalytics;

/**
 * Created by itparsa on 2/6/18.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ADAnalytics.initialize(this);
    }
}
