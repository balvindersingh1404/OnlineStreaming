package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.headsupseven.corp.adapter.HomeDataAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.model.HomeLsitModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

import butterknife.ButterKnife;

/**
 * Created by admin on 26/01/2017.
 */

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private Context mContext;
    private SwipeRefreshLayout homedata_refresh;
    private HomeDataAdapter mHomeDataAdapter;
    private Vector<HomeLsitModel> allHedsUpModels = new Vector<HomeLsitModel>();
    private LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        mContext = this;
        selecteddeselectedTab(0);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        ButterKnife.bind(this);
        initUI();
    }


    public void initUI() {
        recyclerView = (RecyclerView) this.findViewById(R.id.listView);
        mHomeDataAdapter = new HomeDataAdapter(mContext, allHedsUpModels);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mHomeDataAdapter);
        homedata_refresh = (SwipeRefreshLayout) findViewById(R.id.homedata_refresh);
        homedata_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
               int currPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                Log.w("currPosition","are"+currPosition);
                loadMoreData(page);
            }
        });


        loadMoreData(0);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    ///
    private void loadMoreData(int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "10");
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/list", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200 && response.length() > 0) {
                            responseDataShow(response,0);
                        }
                    }
                });
            }
        });
    }
    // show the data into
    // row view
    public void responseDataShow(final String response,final int responseType) {
        try {
            final JSONObject json_ob = new JSONObject(response);
            final JSONArray json = json_ob.getJSONArray("msg");
            for (int index = 0; index < json.length(); index++) {
                HomeLsitModel model = new HomeLsitModel();
                JSONObject mObject = json.getJSONObject(index);
                model.setCreatedAt(mObject.getString("CreatedAt"));
                model.setCreatedByAvatar(mObject.getString("CreatedByAvatar"));
                model.setCreatedBy(mObject.getString("UpdatedAt"));
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
                model.setLiveStreamApp(mObject.getString("LiveStreamApp"));
                model.setLiveStreamName(mObject.getString("LiveStreamName"));
                model.setPostStreaming(mObject.getBoolean("IsPostStreaming"));
                model.setVideoType(mObject.getString("VideoType"));
                model.setVideoName(mObject.getString("VideoName"));
                String PostType = mObject.getString("PostType");
                model.setPostType(PostType);

                if (PostType.contains("ads")) {
                    model.setFlagAdd(true);
                    model.setID(mObject.getInt("AdsID"));
                } else {
                    model.setID(mObject.getInt("ID"));
                    model.setFlagAdd(false);
                }
                if(responseType==0)
                    mHomeDataAdapter.addnewItem(model);
                else
                    mHomeDataAdapter.addItem(model,0);
            }
        } catch (Exception ex) {

        }
        mHomeDataAdapter.notifyDataSetChanged();
    }
    ///Auto refresh web-service call
    private void refreshContent() {
        if (mHomeDataAdapter.topFeedsID() != -1) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("max-id", "" + mHomeDataAdapter.topFeedsID());
            APIHandler.Instance().GET_BY_AUTHEN("feeds/list", param, new APIHandler.RequestComplete() {
                @Override
                public void onRequestComplete(final int code, final String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("response","are: "+response);
                            homedata_refresh.setRefreshing(false);
                            if (code == 200 && response.length() > 0) {
                                responseDataShow(response,1);
                            }
                        }
                    });
                }
            });
        } else {
            homedata_refresh.setRefreshing(false);
        }

    }

}


