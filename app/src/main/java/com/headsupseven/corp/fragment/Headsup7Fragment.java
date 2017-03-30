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
import com.headsupseven.corp.adapter.Headsup7Adapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.SimpleDividerItemDecoration;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.HomeLsitModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;
;

/**
 * Created by admin on 26/01/2017.
 */

public class Headsup7Fragment extends Fragment {
    private View view;
    private RecyclerView listView;
    private Headsup7Adapter mHeadsup7Adapter;
    private Context mContext;
    private Vector<HomeLsitModel> allHedsUpModels = new Vector<HomeLsitModel>();
    SwipeRefreshLayout homedata_refresh;
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
        view = inflater.inflate(R.layout.activity_headup7, container, false);
        mContext = getActivity().getApplicationContext();
        initUI();
        return view;
    }

    public void initUI() {
        mHeadsup7Adapter = new Headsup7Adapter(getActivity(), allHedsUpModels);

        homedata_refresh = (SwipeRefreshLayout) view.findViewById(R.id.homedata_refresh);
        homedata_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHeadsup7Adapter.deleteAllItem();
                loadMoreData(0);
                refreshContent();
            }
        });
        listView = (RecyclerView) view.findViewById(R.id.listView);
        //----------------------------------------------------------------------
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mHeadsup7Adapter.setVideoTapCallback(new VideoViewShouldClose() {
            @Override
            public void ClickOnThumbShouldCloseVideo() {
            }
        });


        listView.setLayoutManager(mLinearLayoutManager);
        listView.setHasFixedSize(true);
        listView.setNestedScrollingEnabled(false);

        listView.setAdapter(mHeadsup7Adapter);
        listView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        listView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e("Headsup7Fragment ", "page" + page);

                if (page * 10 <= totalItemsCount) {
                    loadMoreData(page);
                    Log.e("Headsup7Fragment ", "totalItemsCount" + totalItemsCount);
                    Log.e("Headsup7Fragment ", "page" + page);

                }
            }
        });
        loadMoreData(0);
    }


    private void loadMoreData(int page) {
        if (page == 0)
            mHeadsup7Adapter.deleteAllItem();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("Headsup7Fragment", "Headsup7Fragment");
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/headsup7", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
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

    public void responseDataShow(String response) {

        try {
            final JSONObject json_ob = new JSONObject(response);
            final JSONArray json = json_ob.getJSONArray("msg");
            Log.w("json", "are:" + json.length());
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

                mHeadsup7Adapter.addnewItem(model);
            }
        } catch (Exception ex) {

        }
        mHeadsup7Adapter.notifyDataSetChanged();
    }

    private void refreshContent() {
        homedata_refresh.setRefreshing(false);
    }
}


