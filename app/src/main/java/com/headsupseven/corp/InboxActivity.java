package com.headsupseven.corp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.headsupseven.corp.adapter.InboxAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.api.chat.ChatManager;

import org.json.JSONArray;
import org.json.JSONObject;


public class InboxActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private ListView listview_inbox;
    private InboxAdapter mInboxAdapter;
    private LinearLayout new_message;
    //    private BusyDialog mbusyDialog;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        //==============================================================
        new_message = (LinearLayout) this.findViewById(R.id.new_message);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InboxActivity.this.finish();
            }
        });

        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, NewmessageActivity.class);
                startActivity(mIntent);
            }
        });


        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        APIHandler.Instance().Chat().GetOldSessions(APIHandler.Instance().user.userID, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {
                Log.w("response", "are" + response);
                mProgressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are" + response);
                        initUI(response);
                    }
                });
            }
        });
        //===============================================================================
        //

    }

    private void initUI(String response) {

        listview_inbox = (ListView) this.findViewById(R.id.listview_inbox);
        mInboxAdapter = new InboxAdapter(mContext);
        listview_inbox.setAdapter(mInboxAdapter);
        mInboxAdapter.notifyDataSetChanged();
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getInt("code") != 20) return;

            mInboxAdapter.DeleteData();
            if (obj.getString("content") != null) {
                final JSONArray data = new JSONArray(obj.getString("content"));
                mInboxAdapter.UpdateData(data);
                listview_inbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            JSONObject dw = data.getJSONObject(i);
                            String ChatUser = dw.getString("ChatUser");
                            Intent mIntent = new Intent(mContext, ChatnewActivity.class);
                            mIntent.putExtra("ChatUser", Integer.parseInt(ChatUser));
                            startActivity(mIntent);

//                            loadProfilw(ChatUser);

                        } catch (Exception ex) {

                        }

                    }
                });
            }

        } catch (Exception ex) {

        }

    }
}

