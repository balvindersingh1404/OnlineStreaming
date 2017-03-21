package com.headsupseven.corp;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.adapter.ExploreAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
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

public class ExplorenewActivity extends BaseActivity {
    private Vector<HomeLsitModel> allHomeLsitModels = new Vector<HomeLsitModel>();
    ExploreAdapter mExploreListAdapter;
    private Vector<CategoryList> allCategoryList = new Vector<CategoryList>();
    private Vector<SearchTag> allSearchTag = new Vector<SearchTag>();
    private SwipeRefreshLayout homedata_refresh;
    private EditText edt_search;
    private ImageView img_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_explore2, frameLayout);
        mContext = this;
        selecteddeselectedTab(2);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mExploreListAdapter = new ExploreAdapter(mContext, allHomeLsitModels);

        img_search = (ImageView) this.findViewById(R.id.img_search);
        edt_search = (EditText) this.findViewById(R.id.edt_search);
        homedata_refresh = (SwipeRefreshLayout) findViewById(R.id.homedata_refresh);

        homedata_refresh = (SwipeRefreshLayout) findViewById(R.id.homedata_refresh);
        homedata_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                refreshContent();
            }
        });

        RecyclerView listView_explorer = (RecyclerView) this.findViewById(R.id.listView_explorer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listView_explorer.setLayoutManager(linearLayoutManager);
        listView_explorer.setHasFixedSize(true);
        listView_explorer.setNestedScrollingEnabled(false);
        listView_explorer.setAdapter(mExploreListAdapter);

        listView_explorer.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                homedata_refresh.setRefreshing(false);


                homedata_refresh.setRefreshing(false);
                Log.w("totalItemsCount", "are" + totalItemsCount);
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
                    mExploreListAdapter.deleteAllItems();


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
        HashMap<String, String> param = new HashMap<String, String>();
        Log.w("test", "are " + edt_search.getText().toString().trim());

        param.put("s", "" + edt_search.getText().toString().trim());
        param.put("max", "6");
        param.put("page", "0");
        APIHandler.Instance().GET_BY_AUTHEN("feeds/search", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ExplorenewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mExploreListAdapter.deleteAllItem();

                        Log.w("response", "are" + response);
                        responseDataShow(response);
                    }
                });

            }
        });
    }


    private void loadMoreData(int page) {
        Log.w("page", "are" + page);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "6");
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/explore", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200) {
                            Log.w("Resposne explore", "are" + response);
                            mExploreListAdapter.deleteAllItem();

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
            mExploreListAdapter.notifyDataSetChanged();

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
                    ExplorenewActivity.this.runOnUiThread(new Runnable() {
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
                    ExplorenewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);

                                Log.w("response", "are search" + response);
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
        if (mExploreListAdapter.topFeedsID() != -1) {


            loadMoreData(0);
        } else {
            homedata_refresh.setRefreshing(false);
        }

    }

}
