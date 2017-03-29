package com.headsupseven.corp.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.headsupseven.corp.NotificaionvideoActiivty;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //If the screen was just turned on or it just booted up, start your Lock Activity
        if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_BOOT_COMPLETED)) {

            ActivityManager am = (ActivityManager)context
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am
                    .getRunningTasks(1);
            String topActivity = taskInfo.get(0).topActivity
                    .getClassName();
            if (!topActivity.equalsIgnoreCase("com.headsupseven.corp.NotificaionvideoActiivty")) {
                Intent i = new Intent(context, NotificaionvideoActiivty.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }


        }
    }
}
