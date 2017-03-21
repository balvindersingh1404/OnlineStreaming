package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.headsupseven.corp.adapter.NewmessageAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.api.chat.ChatManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewmessageActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private ListView listview_inbox;
    private NewmessageAdapter mNewmessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        //===============================================================================
        //
        APIHandler.Instance().Chat().GetFriendsList(APIHandler.Instance().user.userID, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUI(response);
                    }
                });
            }
        });
        //===============================================================================
        //

    }

    private void initUI(String response) {
        try {

            JSONObject obj = new JSONObject(response);
            if (obj.getInt("code") != 15) return;
            ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
            ll_silding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewmessageActivity.this.finish();
                }
            });
            listview_inbox = (ListView) this.findViewById(R.id.listview_inbox);
            mNewmessageAdapter = new NewmessageAdapter(mContext);
            listview_inbox.setAdapter(mNewmessageAdapter);

            if (obj.getString("content") != null) {
                final JSONArray data = new JSONArray(obj.getString("content"));
                listview_inbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {

                            JSONObject dw = data.getJSONObject(position);
                            int userID = dw.getInt("id");
                            Intent mIntent = new Intent(mContext, ChatnewActivity.class);
                            mIntent.putExtra("ChatUser", userID);
                            startActivity(mIntent);
                        } catch (Exception e) {

                        }
                    }
                });
                mNewmessageAdapter.DeleteData();
                mNewmessageAdapter.UpdateData(data);
            }
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftKeyboard();
    }

    //hide the keyborad
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
