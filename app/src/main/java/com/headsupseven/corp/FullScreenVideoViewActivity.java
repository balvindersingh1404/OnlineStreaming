package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.headsupseven.corp.api.APIHandler;

import java.io.File;
import java.io.IOException;


public class FullScreenVideoViewActivity extends AppCompatActivity {

    private Context mContext;
    String videoUri="";
    private boolean isLocalFile=false;
    private  FullscreenVideoLayout videoLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatvideo);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Intent intent=getIntent();
        videoUri = intent.getStringExtra("videoUri");
        isLocalFile=intent.getBooleanExtra("local",false);
        initUI();

    }

    private void initUI() {
        LinearLayout ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {FullScreenVideoViewActivity.this.finish();
            }
        });

        videoLayout = (FullscreenVideoLayout) findViewById(R.id.videoview);

        if (isLocalFile) {
            File mFile = new File(videoUri);
            if (mFile.exists()) {
                Uri uriVideo = Uri.parse(mFile.getAbsolutePath());
                try {
                    videoLayout.setVideoURI(uriVideo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File mFile = createfilefordata(videoUri);
            if (mFile.exists()) {
                Uri uriVideo = Uri.parse(mFile.getAbsolutePath());
                try {
                    videoLayout.setVideoURI(uriVideo);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                String uri = APIHandler.ChatURL + "pub/" + videoUri;
                Uri uriVideo = Uri.parse(videoUri);
                try {
                    videoLayout.setVideoURI(uriVideo);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public File createfilefordata(String nameOffile) {
        final String extStorageDirectory = Environment
                .getExternalStorageDirectory().toString();
        File myFile = new File(extStorageDirectory + "/"
                + mContext.getString(R.string.app_name) + "/pub" + "/" + nameOffile);
        return myFile;
    }



}
