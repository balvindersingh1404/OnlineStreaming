package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.headsupseven.corp.adapter.AddmanagerAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.AddmanagerList;
import com.headsupseven.corp.utils.Logcate;
import com.headsupseven.corp.utils.PersistentUser;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by tmmac on 1/29/17.
 */

public class AddmanagerActivity extends AppCompatActivity {
    public Context mContext;
    private LinearLayout ll_silding;
    private ListView listview_add_manage;
    private AddmanagerAdapter mAddmanagerAdapter;
    private Intent mIntent;
    private static final int FORCREATEADD = 101;
    private Vector<AddmanagerList> addmanagerLists = new Vector<AddmanagerList>();
    private TextView my_banlance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmanager);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();

    }

    private void initUI() {

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddmanagerActivity.this.finish();
            }
        });
        my_banlance = (TextView) this.findViewById(R.id.my_banlance);
        listview_add_manage = (ListView) this.findViewById(R.id.listview_add_manage);
        mAddmanagerAdapter = new AddmanagerAdapter(mContext, addmanagerLists);
        listview_add_manage.setAdapter(mAddmanagerAdapter);
        mAddmanagerAdapter.notifyDataSetChanged();

        LinearLayout new_create_add = (LinearLayout) this.findViewById(R.id.new_create_add);
        new_create_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIntent = new Intent(mContext, CreateaddActivity.class);
                startActivityForResult(mIntent, FORCREATEADD);
            }
        });
        WebGetbalance();
        loadAllAddManager();
    }

    public void loadAllAddManager() {
        APIHandler.Instance().GET_BY_AUTHEN("ads", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (response.length() > 0) {
                    AddmanagerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Logcate.logcateW("response", response);

                                JSONObject mJsonObject = new JSONObject(response);
                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {

                                    JSONArray mArray = mJsonObject.getJSONArray("msg");
                                    for (int inde = 0; inde < mArray.length(); inde++) {
                                        JSONObject object = mArray.getJSONObject(inde);
                                        AddmanagerList mAddmanagerList = new AddmanagerList();
                                        mAddmanagerList.setID(object.getString("ID"));
                                        mAddmanagerList.setCreatedAt(object.getString("CreatedAt"));
                                        mAddmanagerList.setUpdatedAt(object.getString("UpdatedAt"));
                                        mAddmanagerList.setUserID(object.getString("UserID"));
                                        mAddmanagerList.setStartDate(object.getString("StartDate"));
                                        mAddmanagerList.setEndDate(object.getString("EndDate"));
                                        mAddmanagerList.setState(object.getString("State"));
                                        mAddmanagerList.setAdsRunningMode(object.getString("AdsRunningMode"));
                                        mAddmanagerList.setAdsMoneyAmount(object.getString("AdsMoneyAmount"));
                                        mAddmanagerList.setAdsLocation(object.getString("AdsLocation"));
                                        mAddmanagerList.setVideoType(object.getString("VideoType"));
                                        mAddmanagerList.setVideoUrl(object.getString("VideoUrl"));
                                        mAddmanagerList.setVideoThumbUrl(object.getString("VideoThumbUrl"));
                                        mAddmanagerList.setViewCount(object.getString("ViewCount"));
                                        mAddmanagerList.setPostName(object.getString("PostName"));
                                        mAddmanagerList.setPostDescription(object.getString("PostDescription"));

                                        mAddmanagerList.setViewCount(object.getString("ViewCount"));
                                        mAddmanagerList.setCommentCount(object.getString("CommentCount"));
                                        mAddmanagerList.setLikeCount(object.getString("LikeCount"));
                                        mAddmanagerList.setCountryData(object.getString("CountryData"));
                                        mAddmanagerList.setCreatedByName(object.getString("CreatedByName"));
                                        mAddmanagerList.setCreatedByAvavar(object.getString("CreatedByAvavar"));
                                        mAddmanagerAdapter.setAddmanagerLists(mAddmanagerList);

                                    }
                                    mAddmanagerAdapter.notifyDataSetChanged();

                                } else if (codeServer == -30) {
                                    PersistentUser.logOut(mContext);
                                    PersistentUser.setUserDetails(mContext, "");
                                    Intent mm = new Intent(mContext, LoginActivity.class);
                                    mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mm);
                                    finish();
                                }

                            } catch (Exception e) {

                            }

                        }
                    });

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FORCREATEADD) {
            loadAllAddManager();
            if (resultCode == Activity.RESULT_OK) {
                //loadAllAddManager();
                Log.w("sds", "d");
            }
        }
    }

    public void WebGetbalance() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("payment/get-balance", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            if (codePost == 1) {
                                JSONObject mObject = mJsonObject.getJSONObject("msg");
                                String HeadsUp7Balance = mObject.getString("HeadsUp7Balance");
                                my_banlance.setText("$" + HeadsUp7Balance);
                            } else {
                                String msg = mJsonObject.getString("msg");
                                PopupAPI.showToast(mContext, msg);
                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }
}


