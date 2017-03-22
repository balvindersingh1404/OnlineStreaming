package com.headsupseven.corp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.headsupseven.corp.adapter.EventdetailsAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.HomeLsitModel;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;


/**
 * Created by admin on 26/01/2017.
 */

public class EventDetailsActivity extends AppCompatActivity {
    RecyclerView listView;
    Context mContext;
    LinearLayout back;
    private Vector<HomeLsitModel> alHomeLsitModels = new Vector<>();
    private EventdetailsAdapter mEventdetailsAdapter;
    private TextView tv_join;
    private TextView tv_price;
    private TextView tv_Registration;
    private TextView tv_end;
    private int EventId = 0;
    private RelativeLayout active_jon;
    private ImageView event_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        EventId = getIntent().getIntExtra("EventId", 0);
        initUI();

    }

    public void initUI() {

        listView = (RecyclerView) this.findViewById(R.id.listView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(layoutManager);
        mEventdetailsAdapter = new EventdetailsAdapter(mContext, alHomeLsitModels);
        listView.setAdapter(mEventdetailsAdapter);
        listView.setNestedScrollingEnabled(false);
        listView.setFocusable(false);
        back = (LinearLayout) this.findViewById(R.id.ll_silding);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.this.finish();
            }
        });

        tv_join = (TextView) this.findViewById(R.id.tv_join);
        tv_price = (TextView) this.findViewById(R.id.tv_price);

        tv_Registration = (TextView) this.findViewById(R.id.tv_Registration);
        tv_end = (TextView) this.findViewById(R.id.tv_end);


        tv_Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePopupShow();
            }
        });
        event_report = (ImageView) this.findViewById(R.id.event_report);
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("event_id", "" + EventId);
        APIHandler.Instance().GET_BY_AUTHEN("contest/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are" + response);
                        showInformationIntoUi(response);
                    }
                });
            }
        });
        active_jon = (RelativeLayout) this.findViewById(R.id.active_jon);
        tv_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                HashMap<String, String> param = new HashMap<String, String>();
                APIHandler.Instance().POST_BY_AUTHEN("contest/" + EventId + "/join", param, new APIHandler.RequestComplete() {
                    @Override
                    public void onRequestComplete(final int code, final String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Log.w("sd", "ae" + response);
                                    JSONObject mJsonObject = new JSONObject(response);
                                    int codeServer = mJsonObject.getInt("code");
                                    if (codeServer < 0 && mJsonObject.getString("msg") == "not-enough-balance") {
                                        PopupAPI.showToast(mContext, "not enough balance");
                                        Intent mIntent = new Intent(mContext, MypaymentActivity.class);
                                        startActivity(mIntent);

                                    } else if (codeServer < 0 && mJsonObject.getString("msg") == "You already join this contest") {
                                        PopupAPI.showToast(mContext, "already join contest");

                                    } else if (codeServer < 0 && mJsonObject.getString("msg") == "Contest invalid. Please check again") {
                                        PopupAPI.showToast(mContext, "token ID wrong");

                                    } else if (codeServer < 0 && mJsonObject.getString("msg") == "User ID wrong, User not found") {
                                        PopupAPI.showToast(mContext, "token invalid or input user wrong");

                                    } else if (codeServer == 1) {
                                        tv_join.setVisibility(View.GONE);

                                    }

                                } catch (Exception e) {
                                    PopupAPI.showToast(mContext, e.getMessage());

                                }
                            }
                        });
                    }
                });
            }

        });
        event_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePopupShow();
            }
        });

    }

    public void showInformationIntoUi(String response) {
        try {

            JSONObject mJsonObject = new JSONObject(response);
            if (mJsonObject.getInt("code") == 1) {
                JSONObject msg = mJsonObject.getJSONObject("msg");
                //========event details=============================
                JSONObject contest = msg.getJSONObject("contest");
                mEventdetailsAdapter.addJSONboject(contest.toString());
                String JoinFees = contest.getString("JoinFees");
                boolean Active = contest.getBoolean("Active");
                boolean IsJoin = contest.getBoolean("IsJoin");


                tv_price.setText(" Price:$" + JoinFees);
                tv_join.setTag("" + contest);
                if (Active) {
                    active_jon.setVisibility(View.VISIBLE);
                } else {
                    active_jon.setVisibility(View.GONE);
                }
                if (IsJoin) {
                    tv_join.setVisibility(View.GONE);
                } else {
                    tv_join.setVisibility(View.VISIBLE);
                }

                String RegistrationStart = contest.getString("RegistrationStart");
                String RegistrationEnd = contest.getString("RegistrationEnd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");
                Date date = currentDateFormat.parse(RegistrationStart);
                Date date2 = currentDateFormat.parse(RegistrationEnd);

                String start = dateFormat.format(date);
                String end = dateFormat.format(date2);
                tv_Registration.setText("Registration start date: " + start);
                tv_end.setText("Registration end date: " + end);


                //============== Event post array==============
                JSONArray posts = msg.getJSONArray("posts");
                for (int index = 0; index < posts.length(); index++) {
                    HomeLsitModel model = new HomeLsitModel();
                    JSONObject mObject = posts.getJSONObject(index);
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
                    mEventdetailsAdapter.addnewItem(model);

                }
                mEventdetailsAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {

        }
    }

    public void sharePopupShow() {
        final Dialog d = new Dialog(mContext, android.R.style.Theme_Translucent);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.share_popup);
        d.setCancelable(false);

        TextView tv_share = (TextView) d.findViewById(R.id.tv_share);
        LinearLayout ll_main = (LinearLayout) d.findViewById(R.id.ll_main);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTo(mContext, "", "");
                d.dismiss();

            }
        });
        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }

    public static void shareTo(Context mContext, String title, String thumbURl) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "" + thumbURl);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "" + title);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
