package com.headsupseven.corp.utils;

import android.util.Log;

/**
 * Created by Prosanto on 3/12/17.
 */

public class Logcate {
    public static void logcateW(String type,String text){
        Log.w(type,text);
    }
    public static void logcateE(String type,String text){
        Log.e(type,text);
    }

}
