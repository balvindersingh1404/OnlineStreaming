package com.headsupseven.corp.chatdata;

/**
 * Created by Droidroid on 2016/4/25.
 */
public class ChatBean {
    public static final int LEFT_TEXT = 0;
    public static final int LEFT_IMAGE = 1;
    public static final int LEFT_VIDEO = 2;
    //
    public static final int RIGHT_TEXT = 5;
    public static final int RIGHT_IMAGE = 6;
    public static final int RIGHT_VIDEO = 7;

    public int type;
    public String text;
    public String userId;
    public String UserName;
    public String UserIcon;
    public boolean localFile=false;
    public boolean uploadingfile=false;

    public boolean isUploadingfile() {
        return uploadingfile;
    }

    public void setUploadingfile(boolean uploadingfile) {
        this.uploadingfile = uploadingfile;
    }

    public boolean isLocalFile() {
        return localFile;
    }

    public void setLocalFile(boolean localFile) {
        this.localFile = localFile;
    }
    public void setType(int type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setUserIcon(String userIcon) {
        UserIcon = userIcon;
    }
}
