package com.headsupseven.corp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Prosanto on 8/24/16.
 */
public class FCMMessageReceiverService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());
            checkView(object.toString());

        } catch (Exception ex) {
            Log.w("Exception", "ere" + ex.getMessage());
        }

    }

    public void checkView(String order_status) {

        try {
            //{action=1, message=test notificaition, thumb-url=this is image url}
            JSONObject mJsonObject = new JSONObject(order_status);
            int action = mJsonObject.getInt("action");
            String message = mJsonObject.getString("message");
            sendNotificationPlace(message);

            //String thumb=mJsonObject.getString("thumb-url");
//
//            ActivityManager am = (ActivityManager) getApplicationContext()
//                    .getSystemService(Activity.ACTIVITY_SERVICE);
//            String packageName = am.getRunningTasks(1).get(0).topActivity
//                    .getPackageName();
//            List<ActivityManager.RunningTaskInfo> taskInfo = am
//                    .getRunningTasks(1);
//            String topActivity = taskInfo.get(0).topActivity
//                    .getClassName();
//            if (packageName.equalsIgnoreCase("com.headsupseven.corp")) {
//                Intent mIntent = new Intent(getApplicationContext(), NotificaionvideoActiivty.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mIntent);
//            } else {
//                Intent mIntent = new Intent(getApplicationContext(), NotificaionvideoActiivty.class);
//                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mIntent);
//            }
        } catch (Exception ex) {
            Log.w("Exception", "Exception" + ex.getMessage());

        }

    }

    private void sendNotificationPlace(String messageBody) {
        Intent intent = new Intent(this, HomebaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

    }
}

