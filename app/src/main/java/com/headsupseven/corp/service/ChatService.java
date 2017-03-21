package com.headsupseven.corp.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.headsupseven.corp.ChatnewActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.api.chat.ChatManager;
import com.headsupseven.corp.chatdata.CommonValue;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Prosanto on 2/9/17.
 */

public class ChatService extends Service {
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        super.onCreate();
        initChatManagertListener();
    }

    private void initChatManagertListener() {
        try {
            if (APIHandler.Instance().Chat() == null)
                return;

            APIHandler.Instance().Chat().RegisterGetMessage(new ChatManager.GetDataComplete() {
                @Override
                public void onDataComplete(String response) {

                    try {
                        if (response != null) {
                            ActivityManager am = (ActivityManager) getApplicationContext()
                                    .getSystemService(Activity.ACTIVITY_SERVICE);
                            String packageName = am.getRunningTasks(1).get(0).topActivity
                                    .getPackageName();
                            List<ActivityManager.RunningTaskInfo> taskInfo = am
                                    .getRunningTasks(1);
                            String topActivity = taskInfo.get(0).topActivity
                                    .getClassName();
                            JSONObject mJsonObject = new JSONObject(response);
                            String from_name = mJsonObject.getString("from-name");
                            int from = mJsonObject.getInt("from");
                            String content_type = mJsonObject.getString("content-type");
                            String content = mJsonObject.getString("content");
                            if (content_type.toString().equals("ws-text")) {
                                from_name = from_name + " send a message";
                            } else if (content_type.toString().equals("ws-image")) {
                                from_name = from_name + " send a photo";
                            } else {
                                from_name = from_name + " send a video";

                            }Log.w("topActivity", "are" + topActivity);

                            if (topActivity.equalsIgnoreCase("com.headsupseven.corp.ChatnewActivity")) {
                                ChatnewActivity mChatActivity = ChatnewActivity.getChatnewActivity();
                                int friendID = mChatActivity.getFriendID();
                                if (from != friendID) {
                                    sendNotificationDis(from_name, content, from, from_name, ChatnewActivity.class);
                                } else {
                                    Intent intent = new Intent(CommonValue.NEW_MESSAGE_ACTION);
                                    intent.putExtra(CommonValue.messageData, response);
                                    sendBroadcast(intent);
                                }
                            } else {
                                sendNotificationDis(from_name, content, from, from_name, ChatnewActivity.class);
                            }

                        }


                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception ex) {

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotificationDis(String messageTitle, String messageBody, int sender_id, String sender_Name, Class neededClass) {

        Intent intent = new Intent(this, neededClass);
        intent.putExtra("ChatUser", sender_id);
        intent.putExtra("ChatUserName", sender_Name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setPriority(2)
                .setContentText(messageBody)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(messageTitle)
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }

}
