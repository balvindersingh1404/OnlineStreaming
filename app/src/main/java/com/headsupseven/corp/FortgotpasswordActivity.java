package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.ForgetCodeModel;
import com.headsupseven.corp.utils.PersistentUser;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;

public class FortgotpasswordActivity extends AppCompatActivity {

    private ImageView li_back;
    private LinearLayout Already;
    private EditText txtUsername;
    private LinearLayout submitBtn;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mContext=this;
        li_back=(ImageView)this.findViewById(R.id.li_back);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FortgotpasswordActivity.this.finish();
            }
        });

        txtUsername = (EditText)this.findViewById(R.id.txtUserName);
        submitBtn = (LinearLayout)this.findViewById(R.id.b_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate
                String username = txtUsername.getText().toString();
                if(username.isEmpty()){
                    PopupAPI.make(mContext, "Error", "You need enter user name or email.");
                    return;
                }
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("user", username);
                APIHandler.Instance().POST("user/forget-pass", postData, new APIHandler.RequestComplete() {
                    @Override
                    public void onRequestComplete(int code, String response) {
                        Log.w("response", "are: " + response);
                        Log.w("code", "are: " + code);

                        if (code == 200 && response.length() > 0) {
                            try {
                                JSONObject reader = new JSONObject(response);
                                int resultCode = reader.getInt("code");
                                if (resultCode == 1) {
                                    // save data
                                    JSONObject msgObj = reader.getJSONObject("msg");
                                    APIHandler.Instance().forgetMode = new ForgetCodeModel();
                                    APIHandler.Instance().forgetMode.setID(msgObj.getInt("id"));
                                    APIHandler.Instance().forgetMode.setUserName(msgObj.getString("name"));
                                    APIHandler.Instance().forgetMode.setExpired(msgObj.getString("expired"));
                                    APIHandler.Instance().forgetMode.setSentAt(msgObj.getString("sent_at"));
                                    // TODO: should save on cached
                                    PersistentUser.setForgetMode(mContext, msgObj.toString());

                                    FortgotpasswordActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PopupAPI.make(mContext, "Success", "We was sent an email to you with a validate code.");
                                        }
                                    });
                                    // change to recovery screen
                                    Intent mm = new Intent(mContext, RecoverpasswordActivity.class);
                                    startActivity(mm);
                                } else {
                                    final String resultMessage = reader.getString("msg");
                                    FortgotpasswordActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PopupAPI.make(mContext, "Error", resultMessage);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                            }
                        } else {
                            FortgotpasswordActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PopupAPI.make(mContext, "Error", "Can't connect to server");
                                }
                            });
                        }
                    }
                });
            }
        });

        Already=(LinearLayout)this.findViewById(R.id.Already);
        Already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(mContext, RecoverpasswordActivity.class);
                startActivity(mm);
            }
        });
    }

}
