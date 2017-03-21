package com.headsupseven.corp.model;

/**
 * Created by Prosanto on 3/2/17.
 */

public class NotificationList {
    private String ID="";
    private String CreatedAt="";
    private String UserID="";
    private String Content="";
    private boolean Read=false;
    private int ReadAction=0;
    private String ReadData="";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public int getReadAction() {
        return ReadAction;
    }

    public void setReadAction(int readAction) {
        ReadAction = readAction;
    }

    public String getReadData() {
        return ReadData;
    }

    public void setReadData(String readData) {
        ReadData = readData;
    }
}


