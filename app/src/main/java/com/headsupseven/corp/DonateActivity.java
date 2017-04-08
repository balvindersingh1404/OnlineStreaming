package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.headsupseven.corp.adapter.VoteAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by admin on 26/01/2017.
 */

public class DonateActivity extends BaseActivity {
    VoteAdapter adapter;
    Context mContext;
    private LinearLayout ll_silding;
    private LinearLayout donate;
    private LinearLayout tv_donate;
    private static final String TAG = "paymentExample";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private EditText amount_donate;
    private String user_Name = "";
    private String CreatedBy = "";
    private TextView my_banlance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        user_Name = getIntent().getStringExtra("user_Name");
        CreatedBy = getIntent().getStringExtra("CreatedBy");
        WebGetbalance();
        initUI();

    }

    public void initUI() {
        my_banlance = (TextView) this.findViewById(R.id.my_banlance);
        amount_donate = (EditText) this.findViewById(R.id.amount_donate);
        tv_donate = (LinearLayout) this.findViewById(R.id.tv_donate);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DonateActivity.this.finish();
            }
        });
        donate = (LinearLayout) this.findViewById(R.id.tv_donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(DonateActivity.this, "Donate clicked", Toast.LENGTH_SHORT).show();
            }
        });
        tv_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount_donate.getText().toString().trim().equalsIgnoreCase("")) {
                    PopupAPI.showToast(mContext, "Please enter amount");
                } else {
                    webserviceupgrade();
                }

            }
        });
    }

    public void webserviceupgrade() {
        String texdescription = APIHandler.Instance().user.userName + " send to " + user_Name;
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("to", CreatedBy);
        param.put("amount", "" + amount_donate.getText().toString());
        APIHandler.Instance().POST_BY_AUTHEN("payment/add-donate", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);
                DonateActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {
                                amount_donate.setText("");
                                PopupAPI.showToast(mContext, mcode.getString("msg"));
                                WebGetbalance();

                            } else {
                                PopupAPI.make(mContext, "Error", mcode.getString("msg"));
                            }

                        } catch (Exception ex) {
                        }
                    }
                });
            }
        });
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
                            Log.w("response", "paylem" + response);
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
