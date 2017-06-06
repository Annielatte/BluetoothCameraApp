package com.minee9351gmail.test1.activity;

import android.app.Application;

import com.minee9351gmail.test1.module.SLog;

/**
 * Created by ehdal on 2017-04-30.
 */

public class BluetoothCameraApplication extends Application {

    public static String state="Camera";
    public static String shareImg="";
    @Override
    public void onCreate() {
        super.onCreate();
        SLog.debug = false;

        SLog.e("Application Start");

    }
}
