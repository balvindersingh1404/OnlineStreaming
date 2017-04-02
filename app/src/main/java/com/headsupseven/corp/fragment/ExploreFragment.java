package com.headsupseven.corp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.HomebaseActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.adapter.ExploreAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.CategoryList;
import com.headsupseven.corp.model.HomeLsitModel;
import com.headsupseven.corp.model.SearchTag;
import com.headsupseven.corp.utils.AdapterCallback;

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
    private int typeforLazyLoader = 0;
    private Activity mActivity;
    private boolean loaderData = false;
    private HomebaseActivity baseActivity;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        baseActivity = (HomebaseActivity)getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (baseActivity != null) {
                baseActivity.showProgressDialog();
            }
            webserverLoadCategoryList();
        } else {
            // Do your Work
        }
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
                mExploreListAdapter.deleteAllItemTag();
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
                Log.w("loadsadsdads", "are" + page);
                Log.w("totalItemsCount", "are" + totalItemsCount);

                int dataView = ((page) * MyApplication.Max_post_per_page) + 1;
                if (dataView <= totalItemsCount) {
                    if (!loaderData) {
                        if (typeforLazyLoader == 0) {
                            homedata_refresh.setRefreshing(false);
                            defaultloadMoreData(page);
                        } else if (typeforLazyLoader == 1) {
                            CategoryloadMoreData(page);
                        } else if (typeforLazyLoader == 2) {
                            TagloadMoreData(2);

                        } else if (typeforLazyLoader == 3) {
                            searchLazyLoader(page);
                        }
                    }

                }
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_search.getText().toString().length() > 0) {
                    mExploreListAdapter.deleteAllItems();
                    ((HomebaseActivity) getActivity()).showProgressDialog();
                    searchLazyLoader(0);
                }

            }
        });
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edt_search.getText().toString().length() > 0) {
                        mExploreListAdapter.deleteAllItems();
                        ((HomebaseActivity) getActivity()).showProgressDialog();
                        searchLazyLoader(0);
                    }
                    return true;
                }
                return false;
            }
        });
        //========== check
        mExploreListAdapter.setAdapterCallback(new AdapterCallback() {
            @Override
            public void onMethodCallback(int type, String searchText) {

                ((HomebaseActivity) getActivity()).showProgressDialog();

                if (type == 1) {
                    typeforLazyLoader = 1;
                    mExploreListAdapter.deleteAllItems();
                    CategoryloadMoreData(0);
                } else if (type == 2) {
                    typeforLazyLoader = 2;
                    mExploreListAdapter.deleteAllItems();
                    TagloadMoreData(0);
                } else if (type == 3) {
                    typeforLazyLoader = 3;
                    mExploreListAdapter.deleteAllItems();
                    searchLazyLoader(0);
                }
            }
        });

    }

    //=================default server laod data====================
    private void defaultloadMoreData(int page) {

        loaderData = true;
        typeforLazyLoader = 0;
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "" + MyApplication.Max_post_per_page);
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/explore", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomebaseActivity) getActivity()).closeProgressDialog();
                        if (code == 200) {
                            responseDataShow(response);
                            mExploreListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }
    //============== Category loader =======================

    public void CategoryloadMoreData(final int page) {
        loaderData = true;

        typeforLazyLoader = 1;
        HashMap<String, String> mapCategory = mExploreListAdapter.loadCategory();
        String categoriesId = "";
        for (String key : mapCategory.keySet()) {
            String value = mapCategory.get(key);
            if (TextUtils.isEmpty(categoriesId))
                categoriesId = value;
            else
                categoriesId = categoriesId + "," + value;
        }
        if (categoriesId.length() == 0)
            return;

        searchForCategoryList(categoriesId, page);
    }

    public void searchForCategoryList(String categoriesId, final int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("categories", categoriesId);
        param.put("max", "" + MyApplication.Max_post_per_page);
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/search-by-categories", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("CategoryList ", "Date: " + response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomebaseActivity) getActivity()).closeProgressDialog();
                        responseDataShow(response);
                        mExploreListAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }
    //============== Tag loader =======================

    public void TagloadMoreData(final int page) {
        loaderData = true;
        typeforLazyLoader = 2;
        HashMap<String, String> mapCategory = mExploreListAdapter.loadTag();
        String categoriesId = "";
        for (String key : mapCategory.keySet()) {
            String value = mapCategory.get(key);
            if (TextUtils.isEmpty(categoriesId))
                categoriesId = value;
            else
                categoriesId = categoriesId + "," + value;
        }
        if (categoriesId.length() == 0)
            return;

        webRqquestTag(categoriesId, page);
    }

    public void webRqquestTag(String tag, final int page) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("tags", "" + tag);
        param.put("max", "" + MyApplication.Max_post_per_page);
        param.put("page", "" + page);
        APIHandler.Instance().GET_BY_AUTHEN("feeds/search-by-tag", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("Tag ", "Date: " + response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomebaseActivity) getActivity()).closeProgressDialog();
                        responseDataShow(response);
                        mExploreListAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    //======================= search list loader=========================
    //typeforLazyLoader==3
    public void searchLazyLoader(final int page) {
        typeforLazyLoader = 3;
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("s", "" + edt_search.getText().toString().trim());
        param.put("max", "" + MyApplication.Max_post_per_page);
        param.put("page", "" + (page));
        APIHandler.Instance().GET_BY_AUTHEN("feeds/search", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HomebaseActivity) getActivity()).closeProgressDialog();
                        responseDataShow(response);
                        mExploreListAdapter.notifyDataSetChanged();
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
            Log.w("json", "ar" + json.length());

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
        loaderData = false;

    }

    //load the category list from server
    public void webserverLoadCategoryList() {
        loaderData = true;
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
                            webserverLoadTagList();
                        }
                    });
                }
            }
        });
    }

    public void webserverLoadTagList() {
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
                            mExploreListAdapter.deleteAllItems();
                            defaultloadMoreData(0);
                        }

                    });
                }
            }
        });
    }

    public void refreshContent() {
        mExploreListAdapter.deleteAllItems();
        defaultloadMoreData(0);
    }

}


