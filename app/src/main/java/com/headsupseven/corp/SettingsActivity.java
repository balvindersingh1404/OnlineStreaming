package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.service.LockScreenService;
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


public class SettingsActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private RelativeLayout rl_Changes;
    private TextView ugrade_account;
    private SwitchCompat switch_Lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.finish();
            }
        });
        rl_Changes = (RelativeLayout) this.findViewById(R.id.rl_Changes);
        rl_Changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(mContext, ChangesselectcategoryActivity.class);
                startActivity(mm);
            }
        });
        ugrade_account = (TextView) this.findViewById(R.id.ugrade_account);
        ugrade_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBuyPressed();
            }
        });

        switch_Lock = (SwitchCompat) this.findViewById(R.id.switch_Lock);


        switch_Lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    PersistentUser.setLock(mContext, true);
                    startService(new Intent(SettingsActivity.this, LockScreenService.class));

                } else {
                    PersistentUser.setLock(mContext, false);
                    stopService(new Intent(SettingsActivity.this, LockScreenService.class));

                }
            }
        });
        if (PersistentUser.isLock(mContext))
            switch_Lock.setChecked(true);
        else
            switch_Lock.setChecked(false);
    }

    private static final String TAG = "paymentExample";
    private static final int REQUEST_CODE_PAYMENT = 1;

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("15.5"), "USD", "Upgrade For 1 Year",
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
                        webserviceupgrade();
                        // webCallRegistraion();

                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            }
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void webserviceupgrade() {

        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("payment/upgrade-account", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                SettingsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {
                                PopupAPI.showToast(mContext, mcode.getString("msg"));

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

}
