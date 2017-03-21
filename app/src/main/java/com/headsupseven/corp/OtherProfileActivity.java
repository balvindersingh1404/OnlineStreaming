package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.headsupseven.corp.adapter.NewOtherProfileAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.SimpleDividerItemDecoration;
import com.headsupseven.corp.model.HomeLsitModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by admin on 26/01/2017.
 */

public class OtherProfileActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String user_id = "";
    private Context mContext;
    private Vector<HomeLsitModel> allHomeLsitModels = new Vector<>();
    private NewOtherProfileAdapter mOtherListdataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        user_id = getIntent().getStringExtra("user_id");
        initUI();

    }

    public void initUI() {

        LinearLayout ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OtherProfileActivity.this.finish();
            }
        });

        recyclerView = (RecyclerView) this.findViewById(R.id.listView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);

        mOtherListdataAdapter = new NewOtherProfileAdapter(mContext, user_id, allHomeLsitModels);
        recyclerView.setAdapter(mOtherListdataAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(OtherProfileActivity.this));
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (page * 12 <= totalItemsCount) {
                    postData(page);

                }
            }
        });
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + user_id + "/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);
                mOtherListdataAdapter.addJSONboject(response);


            }
        });
        postData(0);

    }


    public void postData(int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "12");
        param.put("page", "" + page);
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + user_id + "/posts", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
            final JSONObject json_ob = new JSONObject(response);
            final JSONArray json = json_ob.getJSONArray("msg");
            for (int index = 0; index < json.length(); index++) {
                HomeLsitModel model = new HomeLsitModel();
                JSONObject mObject = json.getJSONObject(index);
                model.setID(mObject.getInt("ID"));
                model.setCreatedAt(mObject.getString("CreatedAt"));
                model.setUpdatedAt(mObject.getString("UpdatedAt"));
                model.setPublish(mObject.getString("Publish"));
                model.setCreatedBy(mObject.getString("CreatedBy"));
                model.setCreatedByName(mObject.getString("CreatedByName"));
                model.setDeviceID(mObject.getString("DeviceID"));
                model.setPostName(mObject.getString("PostName"));
                model.setPostDescription(mObject.getString("PostDescription"));
                model.setPostThumbUrl(mObject.getString("PostThumbUrl"));
                model.setView(mObject.getString("View"));
                model.setLike(mObject.getString("Like"));
                model.setComment(mObject.getString("Comment"));
                model.setRate(mObject.getString("Rate"));
                model.setRateValue(mObject.getString("RateValue"));
                model.setPostType(mObject.getString("PostType"));
                model.setLiveStreamApp(mObject.getString("LiveStreamApp"));
                model.setLiveStreamName(mObject.getString("LiveStreamName"));
                model.setPostStreaming(mObject.getBoolean("IsPostStreaming"));
                model.setVideoType(mObject.getString("VideoType"));
                model.setVideoName(mObject.getString("VideoName"));
                model.setCreatedByAvatar(mObject.getString("CreatedByAvatar"));

                String PostType = mObject.getString("PostType");
                model.setLiked(mObject.getBoolean("Liked"));

                model.setPostType(PostType);

                if (PostType.contains("ads")) {
                    model.setFlagAdd(true);
                    model.setID(mObject.getInt("AdsID"));
                } else {
                    model.setID(mObject.getInt("ID"));
                    model.setFlagAdd(false);
                }
                mOtherListdataAdapter.addnewItem(model);

            }

            mOtherListdataAdapter.notifyDataSetChanged();


            Log.w("check", "response");
        } catch (Exception ex) {
            Log.w("Exception", "are" + ex.getMessage());

        }
    }


}
