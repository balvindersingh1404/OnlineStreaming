package com.headsupseven.corp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.TransactionList;
import com.headsupseven.corp.utils.PersistentUser;
import com.headsupseven.corp.utils.PopupAPI;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;


public class MypaymentActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private TextView tv_Withdraw;
    private Intent mIntent;
    ///
    LinearLayout LinearLayout_transcation;
    private LinearLayout all_LinearLayout_transcation;
    private Vector<TransactionList> allTransactionLists = new Vector<>();

    private Vector<TransactionList> allReciveTransactionLists = new Vector<>();
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_WI = 10111;

    public static final String TAG = "paymentExample";
    private LinearLayout new_message;
    private TextView my_banlance;
    private String requestBalance = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypayment);
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

    private void initUI() {

        my_banlance = (TextView) this.findViewById(R.id.my_banlance);

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MypaymentActivity.this.finish();
            }
        });

        tv_Withdraw = (TextView) this.findViewById(R.id.tv_Withdraw);
        tv_Withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mContext, WithdrawActivity.class);
                startActivityForResult(mIntent, REQUEST_CODE_WI);
            }
        });


        all_LinearLayout_transcation = (LinearLayout) this.findViewById(R.id.all_LinearLayout_transcation);
        LinearLayout_transcation = (LinearLayout) this.findViewById(R.id.LinearLayout_transcation);
        WebGetbalance();

        APIHandler.Instance().GET_BY_AUTHEN("payment/transactions", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                MypaymentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            if (codePost == 1) {
                                JSONArray msg = mJsonObject.getJSONArray("msg");
                                for (int index = 0; index < msg.length(); index++) {
                                    JSONObject mmm = msg.getJSONObject(index);
                                    TransactionList mTransactionList = new TransactionList();
                                    mTransactionList.setAmount(mmm.getString("Amount"));
                                    mTransactionList.setCreatedAt(mmm.getString("CreatedAt"));
                                    mTransactionList.setTransactionDescription(mmm.getString("TransactionDescription"));
                                    allTransactionLists.addElement(mTransactionList);
                                }
                                addTransactionView();
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

        new_message = (LinearLayout) this.findViewById(R.id.new_message);
        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showsureaydialog();
            }
        });
    }

    public void addTransactionView() {
        LinearLayout_transcation.removeAllViews();
        all_LinearLayout_transcation.setVisibility(View.VISIBLE);

        if (allTransactionLists.size() == 0) {
            all_LinearLayout_transcation.setVisibility(View.GONE);
        }
        for (int index = 0; index < allTransactionLists.size(); index++) {
            LayoutInflater l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View convertView = l_Inflater.inflate(R.layout.row_transcation_list, null, false);
            TextView amount = (TextView) convertView.findViewById(R.id.amount);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView date = (TextView) convertView.findViewById(R.id.date);


            final TransactionList mTransactionList = allTransactionLists.elementAt(index);
            date.setText(APIHandler.getTimeAgo(mTransactionList.getCreatedAt()));
            amount.setText(mTransactionList.getAmount() + "$");
            title.setText(mTransactionList.getTransactionDescription());
            LinearLayout_transcation.addView(convertView);

        }

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
        Intent intent = new Intent(MypaymentActivity.this, PaymentActivity.class);
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
