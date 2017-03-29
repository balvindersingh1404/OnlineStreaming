package com.headsupseven.corp.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.headsupseven.corp.model.AddmanagerList;


/**
 * Created by Prosanto on 11/11/16.
 */

public class MyApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static  final int Max_post_per_page=10;
    public static AddmanagerList mAddmanagerList = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
//        Fabric.with(this, new Twitter(authConfig));
//    }

}
