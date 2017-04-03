package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.service.LockScreenService;
import com.headsupseven.corp.utils.PersistentUser;

import org.json.JSONObject;

import java.util.HashMap;


public class SplashActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        PersistentUser.SetPushkey(mContext, refreshedToken);
        MyApplication.wasScreenOn=true;
        if (PersistentUser.isLock(mContext))
            startService(new Intent(this, LockScreenService.class));

        mHandler.postDelayed(mPendingLauncherRunnable, 2000);
    }

    private final Runnable mPendingLauncherRunnable = new Runnable() {
        @Override
        public void run() {

            if (!PersistentUser.isSlider(mContext)) {
                Intent mm = new Intent(SplashActivity.this, SliderActivity.class);
                startActivity(mm);
                SplashActivity.this.finish();
            } else {
                if (PersistentUser.isLogged(mContext)) {
                    try {
                        loginInformation();
                    } catch (Exception ex) {
                        Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mm);
                        SplashActivity.this.finish();
                    }
                } else {
                    Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mm);
                    SplashActivity.this.finish();
                }

            }

        }
    };

    public void loginInformation() {
        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("UserName", PersistentUser.getUserName(mContext));
        authenPostData.put("Password", PersistentUser.getPassword(mContext));
        authenPostData.put("Platform", "mobile");


        APIHandler.Instance().POST("authen/", authenPostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, String response) {

                if (code == 200 && response.length() > 0) {
                    try {
                        JSONObject reader = new JSONObject(response);
                        int resultCode = reader.getInt("code");
                        if (resultCode == 1) {
                            PersistentUser.setUserDetails(mContext, response);
                            JSONObject msgData = reader.getJSONObject("msg");
                            APIHandler.Instance().user.SetAuthenData(msgData);
                            APIHandler.Instance().InitChatClient();
                            webCallPush();

                        } else {
                            PersistentUser.logOut(mContext);
                            Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(mm);
                            SplashActivity.this.finish();
                        }
                    } catch (Exception e) {
                        PersistentUser.logOut(mContext);
                        Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mm);
                        SplashActivity.this.finish();
                    }
                } else {
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PersistentUser.logOut(mContext);
                            Intent mm = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(mm);
                            SplashActivity.this.finish();
                        }
                    });
                }
            }
        });
    }

    public void webCallPush() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("device-token", PersistentUser.GetPushkey(mContext));
        APIHandler.Instance().POST_BY_AUTHEN("push/register-android", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PersistentUser.setLogin(mContext);
                        Intent mm = new Intent(mContext, HomebaseActivity.class);
                        mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mm);
                        SplashActivity.this.finish();
                    }
                });

            }
        });
    }
}
