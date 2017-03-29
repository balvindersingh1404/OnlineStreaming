package com.headsupseven.corp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.MD360DirectorFactory;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDPinchConfig;
import com.headsupseven.corp.api.APIHandler;

import java.io.IOException;
import java.util.HashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Nam Nguyen on 2/1/2017.
 */

public class LiveVideoPlayerActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    public Context mContext;
    private MDVRLibrary mVRLibrary;

    // UI
    private RelativeLayout mTopView, mBottomView;
    private ImageView mBtnBack, m360Mode, mVRMode;
    private ImageView mBtnPlay;
    private SeekBar mSeekBar;
    private TextView mPlayTime;

    // video variables
    Boolean is360, isLive;
    String urlWatch, urlVideo;
    private String PostType = "";
    private boolean is360mode = false;

    //-----------------------------------
    // video player
    protected IMediaPlayer mMediaPlayer;
    private static final int STATUS_IDLE = 0;
    private static final int STATUS_PREPARING = 1;
    private static final int STATUS_PREPARED = 2;
    private static final int STATUS_STARTED = 3;
    private static final int STATUS_PAUSED = 4;
    private static final int STATUS_STOPPED = 5;
    private int mStatus = STATUS_IDLE;
    private MediaController mController;
    private int mSeekWhenPrepared;
    private int mCurrentBufferPercentage;
    private boolean isShowRating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_live_video_player_new);

        mContext = this;
        PlayUsingVitamio();

        String urlData = "";
        PostType = getIntent().getStringExtra("PostType");
        int postId = getIntent().getIntExtra("postID", 0);
        if (PostType.equalsIgnoreCase("ads")) {
            urlData = "ads/" + postId + "/add-view";
        } else {
            urlData = "feeds/" + postId + "/add-view";
        }

        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN(urlData, param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // success
                    }
                });
            }
        });
        mBtnBack = (ImageView) this.findViewById(R.id.back_player);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LiveVideoPlayerActivity.this.finish();
            }
        });
    }


    public void PlayUsingVitamio() {
        isLive = getIntent().getBooleanExtra("is_Live", false);
        is360 = getIntent().getBooleanExtra("is_360", false);
        urlWatch = getIntent().getStringExtra("Url_Stream");
        urlVideo = getIntent().getStringExtra("Url_video");

        busy();

        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
                .projectionMode(MDVRLibrary.PROJECTION_MODE_SPHERE)
                .motionDelay(SensorManager.SENSOR_DELAY_GAME)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(SurfaceTexture surfaceTexture) {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.setSurface(new Surface(surfaceTexture));
                            playerPrepare();
                        }
                    }
                })
                .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
                    @Override
                    public void onNotSupport(int mode) {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                        Toast.makeText(LiveVideoPlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .pinchConfig(new MDPinchConfig().setMin(1.0f).setMax(8.0f).setDefaultValue(0.1f))
                .pinchEnabled(true)
                .directorFactory(new MD360DirectorFactory() {
                    @Override
                    public MD360Director createDirector(int i) {
                        return MD360Director.builder().setPitch(90).build();
                    }
                })
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.95f))
                .build(R.id.video_view);

        // init overlay UI
        m360Mode = (ImageView)findViewById(R.id.view_360);
        mVRMode = (ImageView)findViewById(R.id.view_vr);
        mBtnPlay = (ImageView)findViewById(R.id.video_play_paush);
        mSeekBar = (SeekBar)findViewById(R.id.seek_video);
        mPlayTime = (TextView)findViewById(R.id.video_progress);
        // init event
        //--------------------------------------------
        // if video is 360 mode
        if (is360) {
            // set default select is 360 video player
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            m360Mode.setVisibility(View.VISIBLE);
            m360Mode.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.DST);
            mVRMode.setVisibility(View.VISIBLE);
            mVRMode.setColorFilter(R.color.white, PorterDuff.Mode.DST);

            // setup event for button mode
            m360Mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!is360mode){
                        is360mode = true;
                        m360Mode.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.DST);
                        mVRMode.setColorFilter(R.color.white, PorterDuff.Mode.DST);
                        mVRLibrary.switchDisplayMode(LiveVideoPlayerActivity.this, MDVRLibrary.DISPLAY_MODE_NORMAL);
                        mVRLibrary.switchProjectionMode(LiveVideoPlayerActivity.this, MDVRLibrary.PROJECTION_MODE_SPHERE);
                        mVRLibrary.switchInteractiveMode(LiveVideoPlayerActivity.this, MDVRLibrary.INTERACTIVE_MODE_TOUCH);
                        mVRLibrary.setAntiDistortionEnabled(false);
                    }
                }
            });
            mVRMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(is360mode){
                        is360mode = false;
                        m360Mode.setColorFilter(R.color.white, PorterDuff.Mode.DST);
                        mVRMode.setColorFilter(R.color.colorPrimary, PorterDuff.Mode.DST);
                        mVRLibrary.switchDisplayMode(LiveVideoPlayerActivity.this, MDVRLibrary.DISPLAY_MODE_GLASS);
                        mVRLibrary.switchProjectionMode(LiveVideoPlayerActivity.this, MDVRLibrary.PROJECTION_MODE_SPHERE);
                        mVRLibrary.switchInteractiveMode(LiveVideoPlayerActivity.this, MDVRLibrary.INTERACTIVE_MODE_MOTION);
                        mVRLibrary.setAntiDistortionEnabled(true);
                    }
                }
            });
            is360mode = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            //---------------------------------------------------------------
            // set to normal video mode
            mVRLibrary.switchDisplayMode(this, MDVRLibrary.DISPLAY_MODE_NORMAL);
            mVRLibrary.switchProjectionMode(this, MDVRLibrary.PROJECTION_MODE_PLANE_FIT);
            mVRLibrary.switchInteractiveMode(this, MDVRLibrary.INTERACTIVE_MODE_TOUCH);

            // hiden 360 and vr mode button
            m360Mode.setVisibility(View.GONE);
            mVRMode.setVisibility(View.GONE);
        }

        //-------------------------------------------------
        // init video player
        mMediaPlayer = new IjkMediaPlayer();
        mMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                cancelBusy();
                mStatus = STATUS_PREPARED;
                playerStart();
                mVRLibrary.notifyPlayerChanged();

                int seekToPosition = mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    seekTo(seekToPosition);
                }
            }
        });
        mStatus = STATUS_IDLE;
        mCurrentBufferPercentage = 0;
        mMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                mCurrentBufferPercentage = i;
            }
        });
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                mController.show(0);
            }
        });
        mMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (!isShowRating) {
                    showRatingDialog();
                    isShowRating = true;
                }

            }
        });
        isShowRating = false;
        mMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        Toast.makeText(mContext, "Network weak", Toast.LENGTH_LONG).show();
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        busy();
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        cancelBusy();
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        break;
                }
                return true;
            }
        });
        mMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i){
                    case IMediaPlayer.MEDIA_ERROR_IO:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        break;
                }
                return true;
            }
        });
        // init media controller
        mController = new MediaController(this) {
            @Override
            public void hide() {
            }      // Prevent hiding of controls.
        };
        mController.setMediaPlayer(this);
        View anchorView = findViewById(R.id.video_view);
        mController.setAnchorView(anchorView);
        mController.setEnabled(true);

        if (isLive) {
            playerOpenURL(urlWatch);
        } else {
            playerOpenURL(urlVideo);
        }
    }


    public void cancelBusy() {
        findViewById(R.id.layout_loader).setVisibility(View.GONE);
    }

    public void busy() {
        findViewById(R.id.layout_loader).setVisibility(View.VISIBLE);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
        playerDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
        playerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
        playerResume();
    }

    public void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_rating);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView cancelDialog = (ImageView) dialog.findViewById(R.id.cross_btn);
        TextView tv_submit = (TextView) dialog.findViewById(R.id.tv_submit);
        RatingBar rating_bar = (RatingBar) dialog.findViewById(R.id.rating_bar);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //////////////////////////////////////////////////////////////////////
    // Player
    //////////////////////////////////////////////////////////////////////

    public void playerPrepare() {
        if (mMediaPlayer == null) return;
        if (mStatus == STATUS_IDLE || mStatus == STATUS_STOPPED) {
            mMediaPlayer.prepareAsync();
            mStatus = STATUS_PREPARING;
        }
    }

    public void playerOpenURL(String url) {
        try {
            mSeekWhenPrepared = 0;

            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playerStop() {
        if (mMediaPlayer == null) return;
        if (mStatus == STATUS_STARTED || mStatus == STATUS_PAUSED) {
            mMediaPlayer.stop();
            mStatus = STATUS_STOPPED;
        }
    }

    public void playerPause() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying() && mStatus == STATUS_STARTED) {
            mMediaPlayer.pause();
            mStatus = STATUS_PAUSED;
        }
    }

    public void playerStart() {
        if (mMediaPlayer == null) return;
        if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED) {
            mMediaPlayer.start();
            mStatus = STATUS_STARTED;
            mController.show(500000000);
        }
    }

    public void playerResume() {
        playerStart();
    }

    public void playerDestroy() {
        playerStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(null);
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }

    //////////////////////////////////////////////////////////////////////
    // Media Controller
    //////////////////////////////////////////////////////////////////////
    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mStatus != STATUS_STOPPED &&
                mStatus != STATUS_IDLE &&
                mStatus != STATUS_PREPARING);
    }

    @Override
    public void start() {
        playerStart();
    }

    @Override
    public void pause() {
        playerPause();
    }

    @Override
    public int getDuration() {
        if (isLive) return 0;
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isLive) return 0;
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isLive) return;
        if (isInPlaybackState()) {
            mSeekWhenPrepared = 0;
            mMediaPlayer.seekTo(pos);
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return mStatus == STATUS_STARTED;
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    @Override
    public boolean canPause() {
        return !isLive;
    }

    @Override
    public boolean canSeekBackward() {
        if (isLive) return false;
        return true;
    }

    @Override
    public boolean canSeekForward() {
        if (isLive) return false;
        return true;
    }

    @Override
    public int getAudioSessionId() {
        if (isInPlaybackState())
            return mMediaPlayer.getAudioSessionId();
        return 0;
    }
}
