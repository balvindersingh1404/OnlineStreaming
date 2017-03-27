package com.headsupseven.corp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.utils.PersistentUser;
import com.headsupseven.corp.utils.PopupAPI;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;


/**
 * Created by admin on 26/01/2017.
 */

public class VoteActivity extends BaseActivity {
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_WI = 10111;
    private Context mContext;
    private LinearLayout ll_silding;
    private TextView text_title;
    private LinearLayout new_message;
    private TextView my_banlance;
    private EditText amount_donate;
    private LinearLayout tv_donate;
    private String requestBalance = "";
    private int voteId=0,post_id= 0;
    private TextView vote_now;
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
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        startService(intent);
        voteId=getIntent().getIntExtra("voteId",0);
        post_id=getIntent().getIntExtra("post_id",0);

        initUI();

    }

    public void initUI() {

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteActivity.this.finish();
            }
        });
        vote_now=(TextView)this.findViewById(R.id.vote_now);
        amount_donate=(EditText)this.findViewById(R.id.amount_donate);
        text_title = (TextView) this.findViewById(R.id.text_title);
        text_title.setText("VOTE");
        vote_now.setText("Vote Now");
        new_message = (LinearLayout) this.findViewById(R.id.new_message);
        tv_donate=(LinearLayout)this.findViewById(R.id.tv_donate);
        my_banlance=(TextView)this.findViewById(R.id.my_banlance);

        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showsureaydialog();
            }
        });
        tv_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount_donate.getText().toString().trim().equalsIgnoreCase("")) {
                    PopupAPI.showToast(mContext, "Pease enter amount");
                }
                else if(1>Double.parseDouble(amount_donate.getText().toString().trim())){
                    PopupAPI.showToast(mContext, "Pease enter min $1");
                }
                else {
                    webservcieForVote();
                }

            }
        });

        WebGetbalance();
    }

    public void webservcieForVote() {
        
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("amount", "" + amount_donate.getText().toString());
        param.put("post_id", "" + post_id);
        APIHandler.Instance().POST_BY_AUTHEN("contest/"+voteId+"/vote", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);
                VoteActivity.this.runOnUiThread(new Runnable() {
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

    public void showsureaydialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView cancelDialog = (ImageView) dialog.findViewById(R.id.cross_btn);
        TextView tv_Start = (TextView) dialog.findViewById(R.id.tv_Start);
        final EditText edittext_amount = (EditText) dialog.findViewById(R.id.edittext_amount);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edittext_amount.getText().toString().trim().equalsIgnoreCase("")) {
                    PopupAPI.showToast(mContext, "Please Enter Amount");
                    return;
                } else {
                    dialog.dismiss();
                    requestBalance = edittext_amount.getText().toString().trim();
                    onBuyPressed(edittext_amount.getText().toString().trim());
                }

            }
        });

        dialog.show();
    }

    public void onBuyPressed(String amount) {

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, amount);
        Intent intent = new Intent(VoteActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent, String amount) {
        return new PayPalPayment(new BigDecimal(amount.toString()), "USD", "Add Payment",
                paymentIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paylem = confirm.toJSONObject().toString(4);
                        JSONObject mJsonObject = new JSONObject(paylem);
                        JSONObject response = mJsonObject.getJSONObject("response");
                        String id = response.getString("id");
                        Webaddbalance(id);
                    } catch (JSONException e) {
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == REQUEST_CODE_WI) {
            if (resultCode == Activity.RESULT_OK) {
                WebGetbalance();
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void Webaddbalance(String paypal_d) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("paypal-id", paypal_d);
        APIHandler.Instance().POST_BY_AUTHEN("payment/add-balance", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            String msg = mJsonObject.getString("msg");
                            if (codePost == 1) {
                                float banlance = Float.parseFloat(my_banlance.getTag().toString().trim());
                                banlance = banlance + Float.parseFloat(requestBalance);
                                my_banlance.setText("$" + banlance);
                                my_banlance.setTag(banlance);

                            } else if (codePost == -30) {
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
        });
    }

    ProgressDialog mProgressDialog;

    public void WebGetbalance() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("payment/get-balance", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                mProgressDialog.dismiss();
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
                                my_banlance.setTag("" + HeadsUp7Balance);
                            } else if (codePost == -30) {
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
        });
    }

}
