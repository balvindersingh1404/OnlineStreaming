package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.headsupseven.corp.adapter.LikeAdapter;


public class LikeActivity extends AppCompatActivity {

    public Context mContext;
    public LikeAdapter mlikeAdapter;
    private LinearLayout ll_silding;
    private TextView text_title;
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
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

        text_title=(TextView)this.findViewById(R.id.text_title);
        listview=(ListView)this.findViewById(R.id.listview);
        text_title.setText("Like");
        mlikeAdapter = new LikeAdapter(mContext);
        listview.setAdapter(mlikeAdapter);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeActivity.this.finish();
            }
        });

    }


}
