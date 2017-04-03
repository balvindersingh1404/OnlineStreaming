package com.headsupseven.corp.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.headsupseven.corp.model.AddmanagerList;


/**
 * Created by Prosanto on 11/11/16.
 */

public class MyApplication extends Application {
    public static boolean wasScreenOn = false;
    public static boolean uploadDataFile = false;
    public static final int Max_post_per_page = 10;
    public static AddmanagerList mAddmanagerList = null;
    public static boolean checkHomeActivty = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
