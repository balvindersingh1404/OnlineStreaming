package com.headsupseven.corp.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.headsupseven.corp.LockscreenActiivty;
import com.headsupseven.corp.application.MyApplication;

import java.util.List;

public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            if (!MyApplication.uploadDataFile) {
                openLocakScreen(context);
            }
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (!MyApplication.uploadDataFile) {
                openLocakScreen(context);
            }
        }
//
//        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//            // and do whatever you need to do here
//            wasScreenOn = true;
//            Log.e("test", "wasScreenOn" + wasScreenOn);
//        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//            Log.e("test", "userpresent");
//        }
//
//        //If the screen was just turned on or it just booted up, start your Lock Activity
//        if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//
//        }
    }

    public void openLocakScreen(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am
                .getRunningTasks(1);
        String topActivity = taskInfo.get(0).topActivity
                .getClassName();
        if (MyApplication.wasScreenOn) {
            Log.w("Application open ", "" + true);
            if (!topActivity.equalsIgnoreCase("com.headsupseven.corp.LockscreenActiivty")) {
                boolean backgroup = isApplicationSentToBackground(context);
                Log.w("backgroup", "" + backgroup);

                if (backgroup) {
                    Intent i = new Intent(context, LockscreenActiivty.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);

                } else {
                    Log.w("sadsdsfdsfsdfdsf", "sfdsfgdgdfgfdg");
                    Intent i = new Intent(context, LockscreenActiivty.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        } else {
            Log.w("Application open ", "" + false);
            Intent i = new Intent(context, LockscreenActiivty.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
