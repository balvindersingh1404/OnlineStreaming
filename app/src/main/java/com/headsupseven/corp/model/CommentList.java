package com.headsupseven.corp.model;

/**
 * Created by Prosanto on 2/26/17.
 */

public class CommentList {
    private String ID="";
    private String CreatedAt="";
    private String UserId="";
    private String PostId="";
    private String Content="";
    private String userNmae="";
    private String UserPic="";

    public String getUserNmae() {
        return userNmae;
    }

    public void setUserNmae(String userNmae) {
        this.userNmae = userNmae;
    }

    public String getUserPic() {
        return UserPic;
    }

    public void setUserPic(String userPic) {
        UserPic = userPic;
    }

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

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
