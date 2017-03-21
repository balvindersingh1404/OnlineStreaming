package com.headsupseven.corp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.headsupseven.corp.R;
import com.headsupseven.corp.adapter.HomeDataAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.HomeLsitModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by admin on 26/01/2017.
 */

public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private Context mContext;
    private SwipeRefreshLayout homedata_refresh;
    private HomeDataAdapter mHomeDataAdapter;
    private Vector<HomeLsitModel> allHedsUpModels = new Vector<HomeLsitModel>();
    private View view;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);
        mContext = getActivity().getApplicationContext();
        initUI();
        return view;
    }

    public void initUI() {
        recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mHomeDataAdapter = new HomeDataAdapter(getActivity(), allHedsUpModels);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mHomeDataAdapter.setVideoTapCallback(new VideoViewShouldClose() {
            @Override
            public void ClickOnThumbShouldCloseVideo() {
            }
        });
        //----------------------------------------------------------------------
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mHomeDataAdapter);
        homedata_refresh = (SwipeRefreshLayout) view.findViewById(R.id.homedata_refresh);
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
                Log.w("currPosition", "are" + currPosition);
                loadMoreData(page);
            }
        });


        loadMoreData(0);
    }

    private void loadMoreData(int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "10");
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/list", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200 && response.length() > 0) {
                            responseDataShow(response, 0);
                        }
                    }
                });
            }
        });
    }

    // row view
    public void responseDataShow(final String response, final int responseType) {
        try {
            Log.w("HomeFragment ", "response" + response);
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
                model.setLiked(mObject.getBoolean("Liked"));


                model.setPostType(PostType);

                if (PostType.contains("ads")) {
                    model.setFlagAdd(true);
                    model.setID(mObject.getInt("AdsID"));
                } else {
                    model.setID(mObject.getInt("ID"));
                    model.setFlagAdd(false);
                }
                if (responseType == 0)
                    mHomeDataAdapter.addnewItem(model);
                else
                    mHomeDataAdapter.addItem(model, 0);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.w("response", "are: " + response);
                            homedata_refresh.setRefreshing(false);
                            if (code == 200 && response.length() > 0) {
                                responseDataShow(response, 1);
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


