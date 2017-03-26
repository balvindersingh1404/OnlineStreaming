package com.headsupseven.corp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.VideoView;
import com.headsupseven.corp.renderers.ScaleRenderer;
import com.headsupseven.corp.sliders.HorizontalSlider;
import com.headsupseven.corp.slideunlock.ISlideListener;
import com.headsupseven.corp.slideunlock.SlideLayout;
import com.headsupseven.corp.utils.PersistentUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    IntentFilter s_intentFilter;
    TextView tv_time, tv_date;
    private Target mTarget;
    private LinearLayout li_clickView;
    private ImageView image_view;
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                    action.equals(Intent.ACTION_TIME_TICK)) {

                setDateTime();
            }
        }
    };


    private BroadcastReceiver mbcr = new BroadcastReceiver() {
        //onReceive method will receive updates
        public void onReceive(Context c, Intent i) {
            int level = i.getIntExtra("level", 0);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
            TextView tv = (TextView) findViewById(R.id.textView1);
            pb.setProgress(level);
            tv.setText(Integer.toString(level) + "%");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaionvideoview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        li_clickView = (LinearLayout) this.findViewById(R.id.li_clickView);
        video_view = (VideoView) this.findViewById(R.id.video_view);
        image_view = (ImageView) this.findViewById(R.id.image_view);
        tv_time = (TextView) this.findViewById(R.id.tv_time);
        tv_date = (TextView) this.findViewById(R.id.tv_date);
        setDateTime();
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(m_timeChangedReceiver, s_intentFilter);
        registerReceiver(mbcr, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        slider = (SlideLayout) findViewById(R.id.slider1);

        slider.setRenderer(new ScaleRenderer());
        slider.setSlider(new HorizontalSlider());
        slider.addSlideListener(new ISlideListener() {
            @Override
            public void onSlideDone(SlideLayout slider, boolean done) {
                if (done) {
                    // restore start state
                    Log.w("slide done", "");
                    jumpMain();
                    slider.reset();
                } else {

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
                final String post_id = msg.getString("post_id");
                final String post_type = msg.getString("post_type");
                final String thumb = msg.getString("thumb");
                final String video = msg.getString("video");
                final String video_description = msg.getString("video_description");
                final String video_name = msg.getString("video_name");
                final String video_type = msg.getString("video_type");

                Log.w("response", "are" + details);


                mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        //Do somethin
                        image_view.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(mContext)
                        .load(thumb)
                        .into(mTarget);

                //======== check the post type==========
                if (post_type.equalsIgnoreCase("news")) {
                    video_view.setVisibility(View.GONE);
                } else if (post_type.equalsIgnoreCase("event")) {
                    video_view.setVisibility(View.GONE);

                } else {
                    /// adds or post
                    if (video_type.equalsIgnoreCase("photo")) {
                        video_view.setVisibility(View.GONE);

                    } else {
                        video_view.setVisibility(View.VISIBLE);
                        //===============Coding for play Video==================
                        Uri uri = Uri.parse(video);
                        video_view.setVideoURI(uri);
                        video_view.requestFocus();
                        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            // Close the progress bar and play the video
                            public void onPrepared(MediaPlayer mp) {
                                video_view.start();
                                image_view.setVisibility(View.GONE);
                                video_view.mute();
                            }
                        });
                        video_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                    }

                }
                //========image click option=========================


                li_clickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (post_type.equalsIgnoreCase("news")) {
                            Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
                            webViewIntent.putExtra("Url", video);
                            webViewIntent.putExtra("Title", video_name);
                            webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(webViewIntent);
                            NotificaionvideoActiivty.this.finish();

                        } else if (post_type.equalsIgnoreCase("event")) {
                            Intent webViewIntent = new Intent(mContext, EventDetailsActivity.class);
                            webViewIntent.putExtra("EventId", post_id);
                            webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(webViewIntent);
                            NotificaionvideoActiivty.this.finish();

                        } else {
                            if (video_type.equalsIgnoreCase("photo")) {
                                Intent webViewIntent = new Intent(mContext, NotificaionviewActivity.class);
                                webViewIntent.putExtra("imagePath", video);
                                webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(webViewIntent);
                                NotificaionvideoActiivty.this.finish();

                            } else {
                                if (post_type.equalsIgnoreCase("ads")) {
                                    Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                                    intent.putExtra("Url_Stream", video);
                                    intent.putExtra("Url_video", video);
                                    intent.putExtra("is_360", !video_type.contentEquals("normal"));
                                    intent.putExtra("is_Live", false);
                                    intent.putExtra("postID", Integer.parseInt(post_id));
                                    intent.putExtra("PostType", post_type);
                                    startActivity(intent);
                                    NotificaionvideoActiivty.this.finish();

                                } else {
                                    detailsOfPost(post_id);
                                }

                            }

                        }
                    }
                });

            }
        } catch (Exception ex) {
            Log.w("Exception", "wre" + ex.getMessage());
        }


    }

    public void loginInformation(final String postId) {
        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("UserName", PersistentUser.getUserName(mContext));
        authenPostData.put("Password", PersistentUser.getPassword(mContext));
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

                            detailsOfPost(postId);
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        });
    }


    public void detailsOfPost(final String postId) {
        // api/feeds/ID/get
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("feeds/" + postId + "/get", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.w("response", "are" + response);
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            if (codePost == 1) {
                                JSONObject msg = mJsonObject.getJSONObject("msg");
                                String LiveStreamName = msg.getString("LiveStreamName");
                                String VideoName = msg.getString("VideoName");
                                String is_360 = msg.getString("VideoType");
                                boolean is_Live = msg.getBoolean("IsPostStreaming");
                                String postType = msg.getString("PostType");

                                Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                                intent.putExtra("Url_Stream", LiveStreamName);
                                intent.putExtra("Url_video", VideoName);
                                intent.putExtra("is_360", !is_360.contentEquals("normal"));
                                intent.putExtra("is_Live", is_Live);
                                intent.putExtra("postID", Integer.parseInt(postId));
                                intent.putExtra("PostType", postType);
                                startActivity(intent);
                                NotificaionvideoActiivty.this.finish();


                            } else if (0 > codePost) {
                                loginInformation(postId);
                            }

                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_timeChangedReceiver);
        unregisterReceiver(mbcr);
    }

    public void setDateTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);
        tv_time.setText(currentDateTimeString + "");
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + currentDateTimeString);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        SimpleDateFormat ssdf = new SimpleDateFormat("EEE");
        Date date = new Date();
        String dayOfTheWeek = ssdf.format(date);
        tv_date.setText(formattedDate + " " + dayOfTheWeek);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
