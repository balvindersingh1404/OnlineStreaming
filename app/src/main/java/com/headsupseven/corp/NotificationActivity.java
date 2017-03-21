package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.headsupseven.corp.adapter.NotificationAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.NotificationList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


public class NotificationActivity extends AppCompatActivity {

    public Context mContext;
    public NotificationAdapter mNotificationAdapter;
    private LinearLayout ll_silding;
    private ListView listview;
    private Vector<NotificationList> allNotificationLists = new Vector<NotificationList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();

    }

    private void initUI() {

        listview = (ListView) this.findViewById(R.id.listview);
        mNotificationAdapter = new NotificationAdapter(mContext, allNotificationLists);
        listview.setAdapter(mNotificationAdapter);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationActivity.this.finish();
            }
        });
        addCategory();

    }

    public void addCategory() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/notifications", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                NotificationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mObject = new JSONObject(response);
                            int code2 = mObject.getInt("code");
                            if (code2 == 1) {
                                JSONArray mJsonArray = mObject.getJSONArray("msg");
                                for (int index = 0; index < mJsonArray.length(); index++) {
                                    JSONObject mJsonObject = mJsonArray.getJSONObject(index);
                                    NotificationList mNotificationList = new NotificationList();
                                    mNotificationList.setID(mJsonObject.getString("ID"));
                                    mNotificationList.setUserID(mJsonObject.getString("UserID"));
                                    mNotificationList.setCreatedAt(mJsonObject.getString("CreatedAt"));
                                    mNotificationList.setContent(mJsonObject.getString("Content"));
                                    mNotificationList.setRead(mJsonObject.getBoolean("Read"));
                                    mNotificationList.setReadAction(mJsonObject.getInt("ReadAction"));
                                    mNotificationList.setReadData(mJsonObject.getString("ReadData"));
                                    mNotificationAdapter.addObject(mNotificationList);
                                }

                            }
                            Log.w("notifications", "" +response);


                        } catch (Exception ex) {

                        }
                    }
                });

//
            }
        });
    }


}
