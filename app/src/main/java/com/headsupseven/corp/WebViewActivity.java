package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.utils.Helper;

/**
 * Created by Nam Nguyen on 3/16/2017.
 */

public class WebViewActivity extends AppCompatActivity {

    public Context mContext;

    private WebView mWebView;
    private TextView mTitleView;
    private LinearLayout mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        String url = getIntent().getStringExtra("Url");
        String title = getIntent().getStringExtra("Title");
        if (url == "") url = "www.google.com";

        busy();

        if (Helper.isAppRunning(WebViewActivity.this, "com.headsupseven.corp.HomebaseActivity")) {
            Log.w("App is  running", "App is  running");
            // App is running
        } else {
            // App is not running
            Log.w("App is not running", "App is not running");

        }

        // init UI
        mWebView = (WebView) findViewById(R.id.screen_web_view);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                cancelBusy();
            }
        });

        mTitleView = (TextView) findViewById(R.id.text_title);
        mTitleView.setText(title);

        mBackButton = (LinearLayout) this.findViewById(R.id.ll_silding);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackButtonActiion();
            }
        });
    }

    public void BackButtonActiion() {

        if (MyApplication.checkHomeActivty) {
            WebViewActivity.this.finish();
        } else {
            Intent mm = new Intent(mContext, HomebaseActivity.class);
            startActivity(mm);
            WebViewActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MyApplication.checkHomeActivty) {
            WebViewActivity.this.finish();
        } else {
            Intent mm = new Intent(mContext, HomebaseActivity.class);
            mm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mm);
            WebViewActivity.this.finish();
        }
    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void busy() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }
}
