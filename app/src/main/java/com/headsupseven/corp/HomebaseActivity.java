package com.headsupseven.corp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.service.ChatService;
import com.headsupseven.corp.tabpager.TabsPagerAdapter;
import com.headsupseven.corp.utils.PersistentUser;
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONObject;

import java.util.HashMap;


public class HomebaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    public Context mContext;
    public FrameLayout frameLayout;
    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private TextView text_title;
    private LinearLayout layout_tab_home;
    private LinearLayout layout_tab_heads_up;
    private LinearLayout layout_live;
    private LinearLayout layout_tab_explore;
    private LinearLayout layout_tab_profile;
    private LinearLayout ll_silding;
    private SimpleSideDrawer slide_me;
    private LinearLayout li_inbox;
    private LinearLayout li_contact_us;
    private LinearLayout li_about_us;
    private LinearLayout li_settings;
    private LinearLayout li_my_payment;
    private LinearLayout li_add_manage;
    private LinearLayout li_survay;
    private Intent mIntent;
    private RelativeLayout layout_notification;
    public ImageView editprofile;
    private LinearLayout layout_accounttype;
    private LinearLayout li_Logout;
    private ImageView user_profile_pic;
    private TextView full_name;
    private AQuery androidQuery;
    private TextView notitification_text;
    private HomebaseActivity mHomebaseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homebase);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Log.w("user_id", "are" + APIHandler.Instance().user.userID);
        this.mHomebaseActivity = this;
        androidQuery = new AQuery(mContext);
        MyApplication.checkHomeActivty=true;
        MyApplication.uploadDataFile = false;
        startService(new Intent(this, ChatService.class));
        initUIBaseActivity();

    }

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    public void closeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void initUIBaseActivity() {

        editprofile = (ImageView) this.findViewById(R.id.editprofile);
        layout_notification = (RelativeLayout) this.findViewById(R.id.layout_notification);
        layout_notification.setVisibility(View.GONE);
        editprofile.setVisibility(View.GONE);

        slide_me = new SimpleSideDrawer(this);
        slide_me.setLeftBehindContentView(R.layout.home_left_menu);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(listener);

        layout_tab_home = (LinearLayout) this.findViewById(R.id.layout_tab_home);
        layout_tab_home.setOnClickListener(listener);
        layout_tab_heads_up = (LinearLayout) this.findViewById(R.id.layout_tab_heads_up);
        layout_tab_heads_up.setOnClickListener(listener);
        layout_live = (LinearLayout) this.findViewById(R.id.layout_live);
        layout_live.setOnClickListener(listener);
        layout_tab_explore = (LinearLayout) this.findViewById(R.id.layout_tab_explore);
        layout_tab_explore.setOnClickListener(listener);
        layout_tab_profile = (LinearLayout) this.findViewById(R.id.layout_tab_profile);
        layout_tab_profile.setOnClickListener(listener);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        text_title = (TextView) this.findViewById(R.id.text_title);
        frameLayout = (FrameLayout) this.findViewById(R.id.content_frame);
        li_Logout = (LinearLayout) this.findViewById(R.id.li_Logout);
        li_Logout.setOnClickListener(listener);
        full_name = (TextView) this.findViewById(R.id.full_name);
        user_profile_pic = (ImageView) this.findViewById(R.id.user_profile_pic);
        full_name.setText(APIHandler.Instance().user.full_name);
        androidQuery.id(user_profile_pic).image(APIHandler.Instance().user.avatar_url, true, true, 0, 0, new BitmapAjaxCallback() {
            @Override
            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                iv.setImageBitmap(bm);
            }
        });
        //

        li_inbox = (LinearLayout) this.findViewById(R.id.li_inbox);
        li_inbox.setOnClickListener(listener);
        li_about_us = (LinearLayout) this.findViewById(R.id.li_about_us);
        li_about_us.setOnClickListener(listener);
        li_contact_us = (LinearLayout) this.findViewById(R.id.li_contact_us);
        li_contact_us.setOnClickListener(listener);
        li_settings = (LinearLayout) this.findViewById(R.id.li_settings);
        li_settings.setOnClickListener(listener);
        li_my_payment = (LinearLayout) this.findViewById(R.id.li_my_payment);
        li_my_payment.setOnClickListener(listener);
        li_add_manage = (LinearLayout) this.findViewById(R.id.li_add_manage);
        li_add_manage.setOnClickListener(listener);
        li_survay = (LinearLayout) this.findViewById(R.id.li_survay);
        li_survay.setOnClickListener(listener);

        layout_accounttype = (LinearLayout) this.findViewById(R.id.layout_accounttype);
        if (APIHandler.Instance().user.account_type.contains("regular"))
            layout_accounttype.setVisibility(View.GONE);
        else
            layout_accounttype.setVisibility(View.VISIBLE);

        layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mContext, NotificationActivity.class);
                startActivity(mIntent);
            }
        });

        notitification_text = (TextView) this.findViewById(R.id.notitification_text);
        notitification_text.setVisibility(View.GONE);
        addCategory();

        //=============View page for sliding============
        mViewPager = (ViewPager) findViewById(R.id.pager_home);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selecteddeselectedTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selecteddeselectedTab(0);

    }

    public ImageView geteditprofile() {
        return editprofile;
    }

    // listener of Side Menu
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.ll_silding:
                    slide_me.toggleLeftDrawer();
                    break;
                case R.id.li_inbox:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, InboxActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.li_add_manage:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, AddmanagerActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.li_about_us:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, AboutusActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.li_contact_us:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, ContactustActivity.class);
                    startActivity(mIntent);
                    break;

                case R.id.li_survay:
                    showsureaydialog();
                    break;
                case R.id.li_my_payment:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, MypaymentActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.li_settings:
                    slide_me.toggleLeftDrawer();
                    mIntent = new Intent(mContext, SettingsActivity.class);
                    startActivity(mIntent);
                    break;


                case R.id.layout_tab_home:
                    selecteddeselectedTab(0);
                    break;

                case R.id.layout_tab_heads_up:
                    selecteddeselectedTab(1);
                    break;
                case R.id.layout_live:
                    mIntent = new Intent(mContext, AddvideoActivity.class);
                    startActivity(mIntent);
                    break;

                case R.id.layout_tab_explore:
                    selecteddeselectedTab(2);

                    break;
                case R.id.layout_tab_profile:
                    selecteddeselectedTab(3);
                    break;

                case R.id.li_Logout:
                    PersistentUser.logOut(mContext);
                    PersistentUser.setUserDetails(mContext, "");
                    Intent mm = new Intent(mContext, LoginActivity.class);
                    mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mm);
                    finish();
                    break;

                default:
                    break;

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }
    /*
       Change the bottom tab color depend constractor
        0==Home tab
        1=HeadsUp7 tab
        2=Explore
        3==Profile
     */

    public void selecteddeselectedTab(int type) {
        int currentItem = mViewPager.getCurrentItem();

        mViewPager.setCurrentItem(type);
        if (type == 0) {
            layout_notification.setVisibility(View.VISIBLE);
            editprofile.setVisibility(View.GONE);
            text_title.setText("Home");
            layout_tab_home.setSelected(true);
            layout_tab_heads_up.setSelected(false);
            layout_tab_explore.setSelected(false);
            layout_tab_profile.setSelected(false);

        } else if (type == 1) {
            layout_notification.setVisibility(View.VISIBLE);
            editprofile.setVisibility(View.GONE);
            text_title.setText("HeadsUp7");
            layout_tab_home.setSelected(false);
            layout_tab_heads_up.setSelected(true);
            layout_tab_explore.setSelected(false);
            layout_tab_profile.setSelected(false);
        } else if (type == 2) {
            layout_notification.setVisibility(View.VISIBLE);
            editprofile.setVisibility(View.GONE);
            text_title.setText("Explore");
            layout_tab_home.setSelected(false);
            layout_tab_heads_up.setSelected(false);
            layout_tab_explore.setSelected(true);
            layout_tab_profile.setSelected(false);
        } else if (type == 3) {
            layout_notification.setVisibility(View.GONE);
            editprofile.setVisibility(View.VISIBLE);
            text_title.setText("Profile");
            layout_tab_home.setSelected(false);
            layout_tab_heads_up.setSelected(false);
            layout_tab_explore.setSelected(false);
            layout_tab_profile.setSelected(true);

        }

    }

    public void addCategory() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/notifications-count", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                HomebaseActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.w("response", "are" + response);
                            JSONObject mObject = new JSONObject(response);
                            int code2 = mObject.getInt("code");
                            if (code == 1) {
                                int total = mObject.getInt("total");
                                if (total == 0)
                                    notitification_text.setVisibility(View.GONE);
                                else {
                                    notitification_text.setText("" + total);
                                    notitification_text.setVisibility(View.VISIBLE);

                                }


                            }
                            Log.w("notifications", "" + response);

                        } catch (Exception ex) {

                        }
                    }
                });

//
            }
        });
    }

    public void showsureaydialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_survay);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView cancelDialog = (ImageView) dialog.findViewById(R.id.cross_btn);
        TextView tv_Start = (TextView) dialog.findViewById(R.id.tv_Start);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(mContext, SurveyActivity.class);
                startActivity(mIntent);
                slide_me.toggleLeftDrawer();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ChatService.class));

    }
}
