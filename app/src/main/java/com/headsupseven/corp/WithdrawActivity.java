package com.headsupseven.corp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.utils.PopupAPI;
import com.headsupseven.corp.utils.ValidateEmail;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tmmac on 1/29/17.
 */

public class WithdrawActivity extends AppCompatActivity {
    public Context mContext;
    private LinearLayout ll_silding;
    private EditText descrition;
    private EditText amount;
    private LinearLayout li_Withdraw;
    private static final int REQUEST_CODE_PROFILE_SHARING = 1;
    private TextView my_banlance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        startService(intent);


        initUI();

    }

    public void onProfileSharingPressed() {
        Intent intent = new Intent(WithdrawActivity.this, PayPalProfileSharingActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.config);
        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());
        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
            if (auth != null) {
                try {
                    String authorization_code = auth.getAuthorizationCode();
                    String getEnvironment = auth.getEnvironment();

                    Log.w("authorization_code", "are" + authorization_code);
                    Log.w("getEnvironment", "are" + getEnvironment);

                    sendAuthorizationToServer(auth);

                } catch (Exception e) {
                    Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("ProfileSharingExample", "The user canceled.");
        } else if (resultCode == PayPalProfileSharingActivity.RESULT_EXTRAS_INVALID) {
            Log.i("ProfileSharingExample",
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
        Log.w("authorization", "are");

    }

    private void initUI() {
        my_banlance = (TextView) this.findViewById(R.id.my_banlance);
        my_banlance.setTag("0");

        descrition = (EditText) this.findViewById(R.id.descrition);
        amount = (EditText) this.findViewById(R.id.amount);
        li_Withdraw = (LinearLayout) this.findViewById(R.id.li_Withdraw);
        li_Withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount.getText().toString().trim().equalsIgnoreCase("")) {
                    PopupAPI.showToast(mContext, "Please Enter Amoun");
                } else if (descrition.getText().toString().trim().equalsIgnoreCase("")) {
                    PopupAPI.showToast(mContext, "Please Enter Email Address");
                } else if (!ValidateEmail.validateEmail(descrition.getText().toString().trim())) {
                    PopupAPI.showToast(mContext, "Please Enter Valid EmailAddress");

                    return;
                } else if (Float.parseFloat(amount.getText().toString().trim()) > Float.parseFloat(my_banlance.getTag().toString().trim())) {
                    PopupAPI.showToast(mContext, "Not Available Balance");

                } else if (Float.parseFloat(amount.getText().toString().trim()) < 10) {
                    PopupAPI.showToast(mContext, " Balance can be withdraw more then 10");

                } else {
                    webserviceupgrade();
//                    onProfileSharingPressed();
                }
            }
        });

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawActivity.this.finish();
            }
        });

        WebGetbalance();
    }

    private ProgressDialog mProgressDialog;

    public void webserviceupgrade() {

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("amount", "" + amount.getText().toString());
        param.put("paypal-email", "" + descrition.getText().toString());
        Log.w("param", "are" + param.toString());
        APIHandler.Instance().POST_BY_AUTHEN("payment/add-withdraw", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                WithdrawActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mProgressDialog.dismiss();
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {
                                PopupAPI.showToast(mContext, mcode.getString("msg"));
                                Intent mIntent = new Intent();
                                setResult(Activity.RESULT_OK, mIntent);
                                finish();
//                                WebGetbalance();

                            } else {
                                PopupAPI.make(mContext, "Error", mcode.getString("msg"));

                            }


                        } catch (Exception ex) {

                        }
                    }
                });

//
            }
        });
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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
                                my_banlance.setTag("" + HeadsUp7Balance);
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
