package com.headsupseven.corp.model;

/**
 * Created by Nam Nguyen on 4/14/2017.
 */

public class ForgetCodeModel {
    private int ID;
    private String UserName;
    private String Expired;
    private String SentAt;

    public void setID(int id){
        ID = id;
    }
    public int getID(){return ID;}

    public void setUserName(String v){
        UserName = v;
    }
    public String getUserName(){return UserName;}

    public void setExpired(String v){
        Expired = v;
    }
    public String getExpired(){return Expired;}

    public void setSentAt(String v){
        SentAt = v;
    }
    public String getSentAt(){return SentAt;}
}
