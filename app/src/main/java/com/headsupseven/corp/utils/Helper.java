package com.headsupseven.corp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Prosanto on 4/3/17.
 */

public class Helper {
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                Log.w("procInfos", ""+procInfos);

                if (processInfo.processName.equals(packageName)) {

                    return true;
                }
            }
        }
        return false;
    }
}
