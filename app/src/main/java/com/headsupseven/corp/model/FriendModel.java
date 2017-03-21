package com.headsupseven.corp.model;

/**
 * Created by admin on 20/02/2017.
 */

public class FriendModel {
    private String ID="";
    private String CreatedAt="";
    private String UserID="";
    private String FollowToUserID="";
    private String FollowUserAvatar="";
    private String FollowUserName="";
    private Boolean IsFollowing = false;


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

    public String getFollowToUserID() {
        return FollowToUserID;
    }

    public void setFollowToUserID(String followToUserID) {
        FollowToUserID = followToUserID;
    }

    public String getFollowUserAvatar() {
        return FollowUserAvatar;
    }

    public void setFollowUserAvatar(String followUserAvatar) {
        FollowUserAvatar = followUserAvatar;
    }

    public String getFollowUserName() {
        return FollowUserName;
    }

    public void setFollowUserName(String followUserName) {
        FollowUserName = followUserName;
    }

    public Boolean getIsFollowing() {
        return IsFollowing;
    }

    public void setIsFollowing(boolean isfollowing) {
        IsFollowing = isfollowing;
    }
}
