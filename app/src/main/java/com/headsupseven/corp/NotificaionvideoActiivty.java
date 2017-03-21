package com.headsupseven.corp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;



/**
 * Created by Prosanto on 2/23/17.
 */

public class NotificaionvideoActiivty extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaionvideoview);
        this.getWindow().setType(
                WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        init();
    }

    private void init() {
        ImageView btnUnlock = (ImageView) findViewById(R.id.upview);
        btnUnlock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                jumpMain();
                return false;
            }
        });
        responseDataShow();

    }

    private void responseDataShow() {

        String responseData = "http://54.254.199.253:8080/recorded/live/IMG0001.1488825900503766746.MP4";
        Uri uri = Uri.parse(responseData);
        ImageView upview = (ImageView) this.findViewById(R.id.upview);
        upview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpMain();
            }
        });
    }

    private synchronized void jumpMain() {
        finish();
    }


}
