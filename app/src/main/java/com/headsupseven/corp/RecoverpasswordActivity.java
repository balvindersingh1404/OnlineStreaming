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

import java.util.HashMap;

public class RecoverpasswordActivity extends AppCompatActivity {

    private ImageView li_back;
    private EditText edit_validate;
    private EditText editi_newpassword;
    private EditText edit_confirmpassword;
    private LinearLayout Update;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_recoverpassword);
        li_back=(ImageView)this.findViewById(R.id.li_back);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecoverpasswordActivity.this.finish();
            }
        });
        edit_validate=(EditText)this.findViewById(R.id.edit_validate);
        editi_newpassword=(EditText)this.findViewById(R.id.editi_newpassword);
        edit_confirmpassword=(EditText)this.findViewById(R.id.edit_confirmpassword);
        Update=(LinearLayout) this.findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (APIHandler.Instance().forgetMode == null){
                    //TODO: get forgetmode from cached
                    String msg = PersistentUser.getForgetMode(mContext);
                    try {
                        JSONObject msgObj = new JSONObject(msg);
                        APIHandler.Instance().forgetMode = new ForgetCodeModel();
                        APIHandler.Instance().forgetMode.setID(msgObj.getInt("id"));
                        APIHandler.Instance().forgetMode.setUserName(msgObj.getString("name"));
                        APIHandler.Instance().forgetMode.setExpired(msgObj.getString("expired"));
                        APIHandler.Instance().forgetMode.setSentAt(msgObj.getString("sent_at"));
                    }catch (Exception e){

                    }
                    //if still null then show this.
                    if (APIHandler.Instance().forgetMode == null) {
                        PopupAPI.make(mContext, "Error", "You need use this device to request code in order to Recover password.");
                        return;
                    }
                }
                // validate
                String code = edit_validate.getText().toString();
                String newPass = editi_newpassword.getText().toString();
                String confirmPass= edit_confirmpassword.getText().toString();

                if (code.length() != 6){
                    PopupAPI.make(mContext, "Error", "Validate code need to be 6 digits.");
                    return;
                }
                if(newPass.length() < 6){
                    PopupAPI.make(mContext, "Error", "Password need more than 6 letters.");
                    return;
                }
                if(!newPass.contentEquals(confirmPass)){
                    PopupAPI.make(mContext, "Error", "Password not match.");
                    return;
                }
                // request
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("user", APIHandler.Instance().forgetMode.getUserName());
                postData.put("code", code);
                postData.put("new_pass", newPass);

                APIHandler.Instance().POST("user/forget-pass-verify", postData, new APIHandler.RequestComplete() {
                    @Override
                    public void onRequestComplete(int code, String response) {
                        Log.w("response", "are: " + response);
                        Log.w("code", "are: " + code);

                        if (code == 200 && response.length() > 0) {
                            try {
                                JSONObject reader = new JSONObject(response);
                                int resultCode = reader.getInt("code");
                                if (resultCode == 1) {
                                    // TODO: should clear on cached
                                    APIHandler.Instance().forgetMode = null;
                                    PersistentUser.removeForgetMode(mContext);

                                    final String resultMessage = reader.getString("msg");
                                    RecoverpasswordActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PopupAPI.make(mContext, "Success", resultMessage);
                                            RecoverpasswordActivity.this.finish();
                                        }
                                    });
                                } else {
                                    final String resultMessage = reader.getString("msg");
                                    RecoverpasswordActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PopupAPI.make(mContext, "Error", resultMessage);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                            }
                        } else {
                            RecoverpasswordActivity.this.runOnUiThread(new Runnable() {
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
    }

}
