package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.headsupseven.corp.model.HomeLsitModel;

import java.util.Vector;


/**
 * Created by admin on 26/01/2017.
 */

public class VideoDetailsActivity extends BaseActivity {
    RecyclerView listView;
    Context mContext;
    LinearLayout back;
    private Vector<HomeLsitModel>alHomeLsitModels=new Vector<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();

    }

    public void initUI() {

//        listView = (RecyclerView) this.findViewById(R.id.listView);
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//        listView.setLayoutManager(layoutManager);
//        mOtherListdataAdapter = new OtherListdataAdapter(mContext,alHomeLsitModels);
//        listView.setAdapter(mOtherListdataAdapter);
//        listView.setNestedScrollingEnabled(false);
//        listView.setFocusable(false);
//        back=(LinearLayout)this.findViewById(R.id.ll_silding);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VideoDetailsActivity.this.finish();
//            }
//        });
    }


}
