package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.utils.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class NotificaionviewActivity extends AppCompatActivity {

    private Context mContext;
    private TouchImageView image_view;
    private ProgressBar progree_bar;
    String imageUri = "";
    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsrceen_imageview);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Intent intent = getIntent();
        imageUri = intent.getStringExtra("imagePath");
        initUI();

    }

    private void initUI() {
        Log.w("imageUri", "are" + imageUri);
        image_view = (TouchImageView) this.findViewById(R.id.image_view);
        LinearLayout ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        progree_bar = (ProgressBar) this.findViewById(R.id.progree_bar);
        progree_bar.setVisibility(View.VISIBLE);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.checkHomeActivty) {
                    NotificaionviewActivity.this.finish();
                } else {
                    Intent mm = new Intent(mContext, HomebaseActivity.class);
                    mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mm);
                    NotificaionviewActivity.this.finish();
                }

            }
        });

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                //Do somethin
                image_view.setImageBitmap(bitmap);
                progree_bar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(mContext)
                .load(imageUri)
                .into(mTarget);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MyApplication.checkHomeActivty) {
            NotificaionviewActivity.this.finish();
        } else {
            Intent mm = new Intent(mContext, HomebaseActivity.class);
            mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mm);
            NotificaionviewActivity.this.finish();
        }
    }
}
