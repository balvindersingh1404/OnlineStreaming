package com.headsupseven.corp.api;

import android.util.Log;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Nam Nguyen on 2/1/2017.
 */

public class User {

    public String accessToken = "";
    public String refreshToken = "";
    public String loginIP = "";
    public Date expireDate;

    public String userName = "";
    public int userID = 0;
    public String account_type = "";
    public String avatar_url = "";
    public String banner_url = "";
    public String email = "";
    public String full_name = "";

    public void SetAuthenData(JSONObject data) {
        try {
            accessToken = data.getString("token");
            refreshToken = data.getString("refresh_token");
            loginIP = data.getString("login_ip");
            // TODO: process expire data later
            JSONObject userObj = data.getJSONObject("user");
            account_type = userObj.getString("account_type");
            avatar_url = userObj.getString("avatar_url");
            banner_url = userObj.getString("banner_url");
            full_name = userObj.getString("full_name");
            userName = userObj.getString("user_name");
            userID = userObj.getInt("id");

        } catch (Exception e) {
            Log.w("ex", "are: " + e.getMessage());
        }


    }
}


