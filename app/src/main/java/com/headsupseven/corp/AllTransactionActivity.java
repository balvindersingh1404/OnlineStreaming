package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.headsupseven.corp.adapter.AllTranscationAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.TransactionList;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by Prosanto on 2/28/17.
 */

public class AllTransactionActivity extends AppCompatActivity {
    private ListView listview_transcation;
    private AllTranscationAdapter mAllTranscationAdapter;
    private LinearLayout ll_silding;
    private Context mContext;
    private Vector<TransactionList> allTransactionLists = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alltransaction);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        listview_transcation = (ListView) this.findViewById(R.id.listview_transcation2);
        mAllTranscationAdapter = new AllTranscationAdapter(mContext, allTransactionLists);
        listview_transcation.setAdapter(mAllTranscationAdapter);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllTransactionActivity.this.finish();
            }
        });
        APIHandler.Instance().GET_BY_AUTHEN("payment/donate-transaction", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                AllTransactionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("sd", "are" + response);
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            JSONArray msg = mJsonObject.getJSONArray("msg");
                            for (int index = 0; index < msg.length(); index++) {
                                JSONObject mmm = msg.getJSONObject(index);
                                TransactionList mTransactionList = new TransactionList();
                                mTransactionList.setAmount(mmm.getString("Amount"));
                                mTransactionList.setCreatedAt(mmm.getString("CreatedAt"));
                                mTransactionList.setTransactionDescription(mmm.getString("TransactionDescription"));
                                mAllTranscationAdapter.addTransaation(mTransactionList);

                            }

                        } catch (Exception e) {
                            PopupAPI.make(mContext, "Error", "Can't connect to server");

                        }
                    }
                });

            }
        });
    }
}
