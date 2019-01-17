package com.rainstorm.aleaf.base;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by liys on 2019-01-16.
 */

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
}
