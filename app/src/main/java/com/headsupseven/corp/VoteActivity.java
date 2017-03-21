package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.headsupseven.corp.adapter.VoteAdapter;


/**
 * Created by admin on 26/01/2017.
 */

public class VoteActivity extends BaseActivity {
    private ListView listview_vote;
    private VoteAdapter adapter;
    private Context mContext;
    private LinearLayout ll_silding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
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

        listview_vote = (ListView) this.findViewById(R.id.listview_vote);
        adapter = new VoteAdapter(mContext);
        listview_vote.setAdapter(adapter);

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


}
