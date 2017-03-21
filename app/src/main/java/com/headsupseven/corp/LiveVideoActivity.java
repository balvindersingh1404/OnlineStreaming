package com.headsupseven.corp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class LiveVideoActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();
    }

    private void initUI() {

//        listview=(ListView)this.findViewById(R.id.listview);
//        mCommentAdaper = new CommentAdapter(mContext);
//        listview.setAdapter(mCommentAdaper);
//        listview.setFocusable(false);
//
//        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
//        ll_silding.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LiveVideoActivity.this.finish();
//            }
//        });

        showRatingdialog();
    }

    public void showRatingdialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_rating);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView cancelDialog=(ImageView)dialog.findViewById(R.id.cross_btn);
        TextView tv_submit=(TextView)dialog.findViewById(R.id.tv_submit);
        RatingBar rating_bar=(RatingBar)dialog.findViewById(R.id.rating_bar);
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



}
