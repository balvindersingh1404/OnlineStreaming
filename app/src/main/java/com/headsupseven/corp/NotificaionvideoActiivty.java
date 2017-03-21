package com.headsupseven.corp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.headsupseven.corp.customview.VideoView;


/**
 * Created by Prosanto on 2/23/17.
 */

public class NotificaionvideoActiivty extends AppCompatActivity {
    private VideoView video_view ;
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

        String responseData = "http://d1exh2z1dm71tm.cloudfront.net/uploaded/A14857078588661489939238012947800.1489939222621592816.FLV";
        Uri uri = Uri.parse(responseData);
        ImageView upview = (ImageView) this.findViewById(R.id.upview);
        upview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpMain();
            }
        });

        video_view=(VideoView)this.findViewById(R.id.video_view);
        video_view.setVideoURI(uri);
//        video_view.setVideoPath(responseData);
        video_view.requestFocus();
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                video_view.start();
            }
        });

    }

    private synchronized void jumpMain() {
        finish();
    }


}
