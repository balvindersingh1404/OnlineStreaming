package com.headsupseven.corp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.headsupseven.corp.EditProfileActivity;
import com.headsupseven.corp.HomebaseActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.adapter.ProfileAdapter;
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

public class MyprofileFragment extends Fragment {


    private Context mContext;
    private Vector<HomeLsitModel> allHomeLsitModels = new Vector<HomeLsitModel>();
    ProfileAdapter mProfileAdapter;
    private View view;
    RecyclerView listView_explorer;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getActivity().getApplicationContext();
        initUI();
        return view;
    }

    public void initUI() {

        HomebaseActivity activity = (HomebaseActivity) getActivity();
        activity.geteditprofile().setVisibility(View.VISIBLE);
        activity.geteditprofile().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, EditProfileActivity.class);
                startActivityForResult(mIntent, 1111);
            }
        });

        mProfileAdapter = new ProfileAdapter(getActivity(), allHomeLsitModels);
        listView_explorer = (RecyclerView) view.findViewById(R.id.profile_listView);
        //----------------------------------------------------------------------
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mProfileAdapter.setVideoTapCallback(new VideoViewShouldClose() {
            @Override
            public void ClickOnThumbShouldCloseVideo() {
            }
        });

        listView_explorer.setLayoutManager(mLinearLayoutManager);
        listView_explorer.setHasFixedSize(true);
        listView_explorer.setNestedScrollingEnabled(false);
        listView_explorer.setAdapter(mProfileAdapter);

        listView_explorer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        listView_explorer.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int currPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (page * 12 <= totalItemsCount) {
                    loadMoreData(page);

                }
            }
        });
        serverDataProcess();
        loadMoreData(0);
    }

    private void serverDataProcess() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                mProfileAdapter.addJSONboject(response);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1111) {
            serverDataProcess();
        }
    }
    public void loadMoreData(int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "12");
        param.put("page", "" + page);
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/posts", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (code == 200 && response.length() > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200 && response.length() > 0) {
                                responseDataShow(response);
                            }
                        }
                    });
                }

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
                model.setCreatedByAvatar(mObject.getString("CreatedByAvatar"));
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
                mProfileAdapter.addnewItem(model);

            }
            mProfileAdapter.notifyDataSetChanged();
        } catch (Exception ex) {

        }
    }
    //===== for my profile fragment ============

}


