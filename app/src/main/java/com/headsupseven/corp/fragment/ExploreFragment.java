package com.headsupseven.corp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.R;
import com.headsupseven.corp.adapter.ExploreAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.CategoryList;
import com.headsupseven.corp.model.HomeLsitModel;
import com.headsupseven.corp.model.SearchTag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by admin on 26/01/2017.
 */

public class ExploreFragment extends Fragment {
    private Context mContext;


    private Vector<HomeLsitModel> allHomeLsitModels = new Vector<HomeLsitModel>();
    ExploreAdapter mExploreListAdapter;
    private Vector<CategoryList> allCategoryList = new Vector<CategoryList>();
    private Vector<SearchTag> allSearchTag = new Vector<SearchTag>();
    private SwipeRefreshLayout homedata_refresh;
    private EditText edt_search;
    private ImageView img_search;
    private View view;
    RecyclerView listView_explorer;

    private LinearLayoutManager mLinearLayoutManager;
    private boolean isSearching = false;
    private boolean isLoadingData = false;

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
        view = inflater.inflate(R.layout.activity_explore2, container, false);
        mContext = getActivity().getApplicationContext();
        initUI();
        return view;
    }

    public void initUI() {
        mExploreListAdapter = new ExploreAdapter(getActivity(), allHomeLsitModels);

        img_search = (ImageView) view.findViewById(R.id.img_search);
        edt_search = (EditText) view.findViewById(R.id.edt_search);
        homedata_refresh = (SwipeRefreshLayout) view.findViewById(R.id.homedata_refresh);
        homedata_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        listView_explorer = (RecyclerView) view.findViewById(R.id.listView_explorer);
        //----------------------------------------------------------------------
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mExploreListAdapter.setVideoTapCallback(new VideoViewShouldClose() {
            @Override
            public void ClickOnThumbShouldCloseVideo() {
            }
        });
        listView_explorer.setLayoutManager(mLinearLayoutManager);
        listView_explorer.setHasFixedSize(true);
        //listView_explorer.setNestedScrollingEnabled(false);
        listView_explorer.setAdapter(mExploreListAdapter);
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
                homedata_refresh.setRefreshing(false);
                loadMoreData(page);
            }
        });


        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExploreListAdapter.deleteAllItems();
                search();

            }
        });
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        webserverLoadCategoryList();
        gwebserverLoadTagList();

    }


    public void search() {
        isSearching = true;
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("s", "" + edt_search.getText().toString().trim());
        param.put("max", "10");
        param.put("page", "0");
        APIHandler.Instance().GET_BY_AUTHEN("feeds/search", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExploreListAdapter.deleteAllItem();
                        mExploreListAdapter.notifyDataSetChanged();
                        responseDataShow(response);
                        mExploreListAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }


    private void loadMoreData(int page) {
        if(isLoadingData) return;
        //------------------------------------------
        // NOTE: if we are searching thing then please avoid load data ( this will cause data refresh )
        if(isSearching) return;
        //------------------------------------------
        // on set if passing here.
        isLoadingData = true;

        //------------------------------------------
        if(page==0){
            mExploreListAdapter.deleteAllItem();
            mExploreListAdapter.notifyDataSetChanged();
        }
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "10");
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/explore", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200) {
                            responseDataShow(response);
                            mExploreListAdapter.notifyDataSetChanged();
                        }
                        isLoadingData = false;
                    }
                });
            }
        });
    }

    public void responseDataShow(final String response) {
        homedata_refresh.setRefreshing(false);

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
                model.setID(mObject.getInt("ID"));
                mExploreListAdapter.addnewItem(model);
            }
        } catch (Exception ex) {

        }
    }

    //load the category list from server
    public void webserverLoadCategoryList() {
        allCategoryList.removeAllElements();
        APIHandler.Instance().GET_BY_AUTHEN("category", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (response.length() > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {
                                    JSONArray msg = mJsonObject.getJSONArray("msg");
                                    for (int index = 0; index < msg.length(); index++) {
                                        JSONObject mObject = msg.getJSONObject(index);
                                        CategoryList mCategoryList = new CategoryList();
                                        mCategoryList.setID(mObject.getString("ID"));
                                        mCategoryList.setName(mObject.getString("Name"));
                                        mCategoryList.setThumbnailUrl(mObject.getString("ThumbnailUrl"));
                                        mCategoryList.setDescription(mObject.getString("Description"));
                                        allCategoryList.addElement(mCategoryList);
                                    }
                                    mExploreListAdapter.addHeaderCategoryList(allCategoryList);
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }
        });
    }

    public void gwebserverLoadTagList() {
        allSearchTag.removeAllElements();
        APIHandler.Instance().GET_BY_AUTHEN("feeds/hot-tag", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (response.length() > 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);

                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {
                                    JSONArray msg = mJsonObject.getJSONArray("msg");
                                    for (int index = 0; index < msg.length(); index++) {
                                        JSONObject mObject = msg.getJSONObject(index);
                                        SearchTag mSearchTag = new SearchTag();
                                        mSearchTag.setID(mObject.getString("ID"));
                                        mSearchTag.setTagName(mObject.getString("TagName"));
                                        mSearchTag.setCreatedAt(mObject.getString("CreatedAt"));
                                        mSearchTag.setInteractive(mObject.getString("Interactive"));
                                        mSearchTag.setPostRelate(mObject.getString("PostRelate"));
                                        allSearchTag.add(mSearchTag);
                                    }
                                    mExploreListAdapter.addHeaderTag(allSearchTag);
                                }
                            } catch (Exception e) {
                            }
                            loadMoreData(0);
                        }
                    });
                }
            }
        });

    }


    //Auto refresh web-service call
    private void refreshContent() {
        if(isSearching){
            isSearching = false;
            mExploreListAdapter.deleteAllItems();
            mExploreListAdapter.notifyDataSetChanged();
            edt_search.setText("");
            loadMoreData(0);
            return;
        }

        if (mExploreListAdapter.topFeedsID() != -1) {
            loadMoreData(0);
        } else {
            homedata_refresh.setRefreshing(false);
        }

    }
}


