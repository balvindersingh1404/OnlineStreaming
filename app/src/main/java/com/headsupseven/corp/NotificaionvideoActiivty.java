package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.VideoView;
import com.headsupseven.corp.renderers.ScaleRenderer;
import com.headsupseven.corp.sliders.HorizontalSlider;
import com.headsupseven.corp.slideunlock.ISlideListener;
import com.headsupseven.corp.slideunlock.SlideLayout;
import com.headsupseven.corp.utils.PersistentUser;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by Prosanto on 2/23/17.
 */

public class NotificaionvideoActiivty extends AppCompatActivity {
    private VideoView video_view;
    private Context mContext;
    SlideLayout slider;

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
        mContext = this;
        //http://www.coders-hub.com/2013/11/how-to-get-battery-level-using.html#.WNI-8xKGOuU
        init();
    }

    private void init() {
//        ImageView btnUnlock = (ImageView) findViewById(R.id.upview);
//        btnUnlock.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                jumpMain();
//                return false;
//            }
//        });
        slider = (SlideLayout) findViewById(R.id.slider1);

        slider.setRenderer(new ScaleRenderer());
        slider.setSlider(new HorizontalSlider());
        slider.addSlideListener(new ISlideListener() {
            @Override
            public void onSlideDone(SlideLayout slider, boolean done) {
                if (done) {
                    // restore start state
                    Log.w("slide done","");
                    jumpMain();
                    slider.reset();
                }else {

                }
            }
        });
        if (PersistentUser.getlastModified(mContext).equalsIgnoreCase("")) {
            HashMap<String, String> param = new HashMap<String, String>();
            APIHandler.Instance().GET_BY_AUTHEN("android-lockscreen", new APIHandler.RequestComplete() {
                @Override
                public void onRequestComplete(final int code, final String response) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String CurrentDateAndTime = getCurrentDateAndTime();
                            PersistentUser.setlastModified(mContext, CurrentDateAndTime);
                            PersistentUser.setVideolist(mContext, response);
                            responseDataShow(PersistentUser.getVideodetails(mContext));

                            // success
                        }
                    });
                }
            });
        } else {
            String string = PersistentUser.getlastModified(mContext);
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                Date dateSave = format.parse(string);
                String CurrentDateAndTime = getCurrentDateAndTime();
                Date dateCurrent = format.parse(CurrentDateAndTime);
                long minutes = distance(dateCurrent, dateSave);
                Log.w("minutes", "are" + minutes);
                if (minutes >= 10) {

                    HashMap<String, String> param = new HashMap<String, String>();
                    APIHandler.Instance().GET_BY_AUTHEN("android-lockscreen", new APIHandler.RequestComplete() {
                        @Override
                        public void onRequestComplete(final int code, final String response) {
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String CurrentDateAndTime = getCurrentDateAndTime();
                                    PersistentUser.setlastModified(mContext, CurrentDateAndTime);
                                    PersistentUser.setVideolist(mContext, response);
                                    responseDataShow(PersistentUser.getVideodetails(mContext));

                                    // success
                                }
                            });
                        }
                    });
                } else {
                    responseDataShow(PersistentUser.getVideodetails(mContext));

                }

            } catch (Exception ex) {

            }


        }


    }


    private void responseDataShow(String details) {

        try {

            JSONObject mJsonObject = new JSONObject(details);
            if (mJsonObject.getInt("code") == 1) {
                JSONObject msg = mJsonObject.getJSONObject("msg");
                String post_id = msg.getString("post_id");
                String post_type = msg.getString("post_type");
                String thumb = msg.getString("thumb");
                String video = msg.getString("video");
                String video_description = msg.getString("video_description");
                String video_name = msg.getString("video_name");
                String video_type = msg.getString("video_type");

                Log.w("video", "are" + video);

                //===============Coding for play Video==================
                Uri uri = Uri.parse(video);
//                ImageView upview = (ImageView) this.findViewById(R.id.upview);
//                upview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        jumpMain();
//                    }
//                });
                video_view = (VideoView) this.findViewById(R.id.video_view);
                video_view.setVideoURI(uri);
                video_view.requestFocus();
                video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        video_view.start();
                    }
                });
            }

        } catch (Exception ex) {

        }


    }

    private synchronized void jumpMain() {
        finish();
    }

    private String getCurrentDateAndTime() {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return simple.format(new Date());
    }

    public long distance(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        //long hours = minutes / 60;
        //long days = hours / 24;
        return minutes;
    }


}
