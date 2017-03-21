package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.utils.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public class FullScreenImaveViewActivity extends AppCompatActivity {

    private Context mContext;
    private TouchImageView image_view;
    private AQuery androidQuery;
    private ProgressBar progree_bar;
    String imageUri = "";
    private boolean isLocalFile = false;
    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsrceen_imageview);
        mContext = this;
        androidQuery = new AQuery(mContext);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Intent intent = getIntent();
        imageUri = intent.getStringExtra("imagePath");
        isLocalFile = intent.getBooleanExtra("local", false);
        initUI();

    }

    private void initUI() {
        image_view = (TouchImageView) this.findViewById(R.id.image_view);
        LinearLayout ll_back = (LinearLayout) this.findViewById(R.id.ll_back);
        progree_bar = (ProgressBar) this.findViewById(R.id.progree_bar);
        progree_bar.setVisibility(View.GONE);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenImaveViewActivity.this.finish();
            }
        });

        if (isLocalFile) {
            File mFile = new File(imageUri);
            if (mFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                image_view.setImageBitmap(myBitmap);
            }
        } else {
            File mFile = createfilefordata(imageUri);
            if (mFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                image_view.setImageBitmap(myBitmap);
            } else {
                String uri = APIHandler.ChatURL + "pub/" + imageUri;
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
                        .load(uri)
                        .into(mTarget);
            }
        }

//        androidQuery.id(image_view).progress(R.id.progres).image(imageUri, true, true, 0, 0, new BitmapAjaxCallback() {
//
//            @Override
//            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
//                progree_bar.setVisibility(View.GONE);
//                iv.setImageBitmap(bm);
//
//            }
//        });
    }

    public File createfilefordata(String nameOffile) {
        final String extStorageDirectory = Environment
                .getExternalStorageDirectory().toString();
        File myFile = new File(extStorageDirectory + "/"
                + mContext.getString(R.string.app_name) + "/pub" + "/" + nameOffile);
        return myFile;
    }


}
