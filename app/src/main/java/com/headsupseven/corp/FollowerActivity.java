package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.adapter.FollowerAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.model.FriendModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by tmmac on 1/29/17.
 */

public class FollowerActivity extends AppCompatActivity {
    public Context mContext;
    private RecyclerView listView_follow;
    private LinearLayout ll_silding;
    private TextView text_title;
    private FollowerAdapter mFollowerAdapter;
    private Vector<FriendModel> allFriendModel = new Vector<FriendModel>();
    private String user_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        user_id = getIntent().getStringExtra("user_id");

        Log.w("user_id", "are" + user_id);
        initUI();
    }

    public void initUI() {
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowerActivity.this.finish();
            }
        });

        text_title = (TextView) this.findViewById(R.id.text_title);
        listView_follow = (RecyclerView) this.findViewById(R.id.listView_follow);
        mFollowerAdapter = new FollowerAdapter(mContext, allFriendModel);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listView_follow.setLayoutManager(mLinearLayoutManager);
        listView_follow.setHasFixedSize(true);
        listView_follow.setAdapter(mFollowerAdapter);
        listView_follow.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (page * 10 <= totalItemsCount) {
                    loadMoreData(page);
                }
            }
        });
        loadMoreData(0);
    }

    private void loadMoreData(int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "10");
        param.put("page", "" + page);
        Log.w("response", "are");

        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + user_id + "/get-followers", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are" + response);

                        if (code == 200 && response.length() > 0) {
                            responseDataShow(response);
                        }
                    }
                });
            }
        });
    }

    public void responseDataShow(final String response) {
        try {
            Log.w("response", "are" + response);
            final JSONObject json_ob = new JSONObject(response);
            final JSONArray json = json_ob.getJSONArray("msg");
            for (int index = 0; index < json.length(); index++) {
                FriendModel mFriendModel = new FriendModel();
                //===========data parsing here=================
                JSONObject mObject = json.getJSONObject(index);
                mFriendModel.setCreatedAt(mObject.getString("CreatedAt"));
                mFriendModel.setID(mObject.getString("ID"));
                mFriendModel.setUserID(mObject.getString("UserID"));
                mFriendModel.setFollowToUserID(mObject.getString("FollowToUserID"));
                mFriendModel.setFollowUserAvatar(mObject.getString("FollowUserAvatar"));
                mFriendModel.setFollowUserName(mObject.getString("FollowUserName"));
                mFriendModel.setIsFollowing(mObject.getBoolean("IsFollowing"));

                mFollowerAdapter.addnewItem(mFriendModel);

            }
        } catch (Exception ex) {

        }
        mFollowerAdapter.notifyDataSetChanged();
    }
}
