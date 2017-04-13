package com.headsupseven.corp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
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
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.media.IjkVideoView;
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

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by Prosanto on 2/23/17.
 */

public class LockscreenActiivty extends AppCompatActivity {
    private IjkVideoView video_view_IjkVideoView;
    private Context mContext;
    SlideLayout slider;
    IntentFilter s_intentFilter;
    TextView tv_time, tv_date;
    private Target mTarget;
    private LinearLayout li_clickView;
    private ImageView image_view;
    private ImageView charging;
    private ImageView row_video_icon;
    private IMediaPlayer miMediaPlayer;

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
        Log.w("LockscreenActiivty", "LockscreenActiivty");

        //http://www.coders-hub.com/2013/11/how-to-get-battery-level-using.html#.WNI-8xKGOuU
        init();
    }

    private void init() {

        row_video_icon = (ImageView) this.findViewById(R.id.row_video_icon);
        li_clickView = (LinearLayout) this.findViewById(R.id.li_clickView);
//        video_view = (VideoView) this.findViewById(R.id.video_view);
        video_view_IjkVideoView = (IjkVideoView) this.findViewById(R.id.video_view_IjkVideoView);

        image_view = (ImageView) this.findViewById(R.id.image_view);
        charging = (ImageView) this.findViewById(R.id.imgCharging);
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
//                    if (!MyApplication.wasScreenOn) {
//                        loginInformationOtherCall(2);
//                    }

                    LockscreenActiivty.this.finish();
                    slider.reset();
                } else {

                }
            }
        });

        if (PersistentUser.getlastModified(mContext).equalsIgnoreCase("")) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("uid", APIHandler.GetDeviceID(this));
            APIHandler.Instance().GET_BY_AUTHEN("android-lockscreen",param, new APIHandler.RequestComplete() {
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
                if (minutes >= 10) {

                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("uid", APIHandler.GetDeviceID(this));
                    APIHandler.Instance().GET_BY_AUTHEN("android-lockscreen", param, new APIHandler.RequestComplete() {
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

                //============ show icon for post=============
                if (post_type.equalsIgnoreCase("news")) {
                    row_video_icon.setVisibility(View.VISIBLE);
                    row_video_icon.setImageResource(R.drawable.news_icon);
                } else if (post_type.equalsIgnoreCase("event")) {
                    row_video_icon.setVisibility(View.VISIBLE);
                    row_video_icon.setImageResource(R.drawable.events_icon);
                } else {
                    if (video_type.equalsIgnoreCase("photo")) {
                        row_video_icon.setVisibility(View.VISIBLE);
                        row_video_icon.setImageResource(R.drawable.photos_icon);
                    }
                    if (video_type.contentEquals("360")) {
                        row_video_icon.setVisibility(View.VISIBLE);
                        row_video_icon.setImageResource(R.drawable.video_icon_360);
                    } else {
                        row_video_icon.setVisibility(View.VISIBLE);
                        row_video_icon.setImageResource(R.drawable.ic_play);
                    }
                }
                //======== check the post type==========
                if (post_type.equalsIgnoreCase("news")) {
                    video_view_IjkVideoView.setVisibility(View.GONE);
                } else if (post_type.equalsIgnoreCase("event")) {
                    video_view_IjkVideoView.setVisibility(View.GONE);

                } else {
                    if (video_type.equalsIgnoreCase("photo")) {
                        video_view_IjkVideoView.setVisibility(View.GONE);

                    } else {
                        video_view_IjkVideoView.setVisibility(View.VISIBLE);
                        videoPlay(video);
                    }

                }
                //========image click option=========================
                li_clickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (MyApplication.checkHomeActivty) {
                            if (post_type.equalsIgnoreCase("news")) {
                                Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
                                webViewIntent.putExtra("Url", video);
                                webViewIntent.putExtra("Title", video_name);
                                startActivity(webViewIntent);
                                LockscreenActiivty.this.finish();

                            } else if (post_type.equalsIgnoreCase("event")) {
                                Intent webViewIntent = new Intent(mContext, EventDetailsActivity.class);
                                webViewIntent.putExtra("EventId", post_id);
                                startActivity(webViewIntent);
                                LockscreenActiivty.this.finish();

                            } else {
                                if (video_type.equalsIgnoreCase("photo")) {
                                    Intent webViewIntent = new Intent(mContext, NotificaionviewActivity.class);
                                    webViewIntent.putExtra("imagePath", video);
                                    startActivity(webViewIntent);
                                    LockscreenActiivty.this.finish();

                                } else {
                                    if (post_type.equalsIgnoreCase("ads")) {
                                        Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                                        intent.putExtra("Url_Stream", video);
                                        intent.putExtra("Url_video", video);
                                        intent.putExtra("likecount","0");
                                        intent.putExtra("liked", false);

                                        intent.putExtra("is_360", !video_type.contentEquals("normal"));
                                        intent.putExtra("is_Live", false);
                                        intent.putExtra("postID", Integer.parseInt(post_id));
                                        intent.putExtra("PostType", post_type);
                                        startActivity(intent);
                                        LockscreenActiivty.this.finish();

                                    } else {
                                        detailsOfPost(post_id);
                                    }

                                }

                            }
                        } else {
                            loginInformationOtherCall(0);
                        }


                    }
                });

            }
        } catch (Exception ex) {
            Log.w("Exception", "wre" + ex.getMessage());
        }
    }

    public void videoPlay(String video) {
        video_view_IjkVideoView.setVisibility(View.VISIBLE);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        Uri mVideoUri = Uri.parse(video);
        video_view_IjkVideoView.setVideoURI(mVideoUri);
        video_view_IjkVideoView.requestFocus();

        video_view_IjkVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                miMediaPlayer = iMediaPlayer;
                miMediaPlayer.setLooping(true);
                miMediaPlayer.setVolume(0, 0);
                video_view_IjkVideoView.start();
                image_view.setVisibility(View.GONE);

            }
        });
    }

    public void checkServerData() {
        try {

            String details = PersistentUser.getVideodetails(mContext);
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

                if (post_type.equalsIgnoreCase("news")) {
                    Intent webViewIntent = new Intent(mContext, WebViewActivity.class);
                    webViewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    webViewIntent.putExtra("Url", video);
                    webViewIntent.putExtra("Title", video_name);
                    startActivity(webViewIntent);
                    LockscreenActiivty.this.finish();

                } else if (post_type.equalsIgnoreCase("event")) {
                    Intent webViewIntent = new Intent(mContext, EventDetailsActivity.class);
                    webViewIntent.putExtra("EventId", post_id);
                    startActivity(webViewIntent);
                    LockscreenActiivty.this.finish();

                } else {
                    if (video_type.equalsIgnoreCase("photo")) {
                        Intent webViewIntent = new Intent(mContext, NotificaionviewActivity.class);
                        webViewIntent.putExtra("imagePath", video);
                        startActivity(webViewIntent);
                        LockscreenActiivty.this.finish();

                    } else {
                        if (post_type.equalsIgnoreCase("ads")) {
                            Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                            intent.putExtra("Url_Stream", video);
                            intent.putExtra("Url_video", video);
                            intent.putExtra("is_360", !video_type.contentEquals("normal"));
                            intent.putExtra("is_Live", false);
                            intent.putExtra("likecount","0");

                            intent.putExtra("postID", Integer.parseInt(post_id));
                            intent.putExtra("PostType", post_type);
                            startActivity(intent);
                            LockscreenActiivty.this.finish();

                        } else {
                            detailsOfPost(post_id);
                        }

                    }

                }

            }
        } catch (Exception ex) {
            Log.w("Exception", "Exception" + ex.getMessage());

        }
    }

    public void loginInformationOtherCall(final int type) {

        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("UserName", PersistentUser.getUserName(mContext));
        authenPostData.put("Password", PersistentUser.getPassword(mContext));
        authenPostData.put("Platform", "mobile");

        APIHandler.Instance().POST("authen/", authenPostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are" + response);
                        if (code == 200 && response.length() > 0) {
                            try {
                                JSONObject reader = new JSONObject(response);
                                int resultCode = reader.getInt("code");
                                if (resultCode == 1) {
                                    PersistentUser.setUserDetails(mContext, response);
                                    JSONObject msgData = reader.getJSONObject("msg");
                                    APIHandler.Instance().user.SetAuthenData(msgData);
                                    APIHandler.Instance().InitChatClient();
                                    if (type == 0) {
                                        checkServerData();

                                    } else {
                                        PersistentUser.setLogin(mContext);
                                        Intent mm = new Intent(mContext, HomebaseActivity.class);
                                        startActivity(mm);
                                        LockscreenActiivty.this.finish();
                                    }
                                }
                            } catch (Exception ex) {

                            }
                        }
                    }
                });

            }
        });
    }

    public void loginInformation(final String postId) {
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
                                intent.putExtra("likecount",msg.getString("Like"));
                                intent.putExtra("liked",msg.getBoolean("Liked"));

                                intent.putExtra("is_Live", is_Live);
                                intent.putExtra("postID", Integer.parseInt(postId));
                                intent.putExtra("PostType", postType);
                                startActivity(intent);
                                LockscreenActiivty.this.finish();


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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat ssdf = new SimpleDateFormat("EEE");
        Date date = new Date();
        String dayOfTheWeek = ssdf.format(date);
        tv_date.setText(formattedDate + " " + dayOfTheWeek);
    }

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
            int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean bCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if (bCharging) {
                charging.setVisibility(View.VISIBLE);
            } else {
                charging.setVisibility(View.GONE);
            }

        }
    };

}
