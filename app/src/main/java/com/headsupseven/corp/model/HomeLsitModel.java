package com.headsupseven.corp.model;

/**
 * Created by admin on 20/02/2017.
 */

public class HomeLsitModel {


    private int ID;
    private String CreatedAt;
    private String UpdatedAt;
    private String Publish;
    private String CreatedBy;
    private String CreatedByName;
    private String DeviceID;
    private String PostName;
    private String PostDescription;
    private String PostThumbUrl;
    private String View;
    private String Like;
    private String Comment;
    private String Rate;
    private String PostType;
    private String RateValue;
    private String LiveStreamApp;
    private String LiveStreamName;
    private String CreatedByAvatar;
    private boolean flagAdd=false;
    private boolean IsPostStreaming;
    private String VideoType;
    private String VideoName;
    private boolean Liked=false;

    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }

    public boolean isFlagAdd() {
        return flagAdd;
    }

    public void setFlagAdd(boolean flagAdd) {
        this.flagAdd = flagAdd;
    }

    public String getCreatedByAvatar() {
        return CreatedByAvatar;
    }

    public void setCreatedByAvatar(String createdByAvatar) {
        CreatedByAvatar = createdByAvatar;
    }

    public boolean isPostStreaming() {
        return IsPostStreaming;
    }

    public void setPostStreaming(boolean postStreaming) {
        IsPostStreaming = postStreaming;
    }





    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getPublish() {
        return Publish;
    }

    public void setPublish(String publish) {
        Publish = publish;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getPostName() {
        return PostName;
    }

    public void setPostName(String postName) {
        PostName = postName;
    }

    public String getPostDescription() {
        return PostDescription;
    }

    public void setPostDescription(String postDescription) {
        PostDescription = postDescription;
    }

    public String getPostThumbUrl() {
        return PostThumbUrl;
    }

    public void setPostThumbUrl(String postThumbUrl) {
        PostThumbUrl = postThumbUrl;
    }

    public String getView() {
        return View;
    }

    public void setView(String view) {
        View = view;
    }

    public String getLike() {
        return Like;
    }

    public void setLike(String like) {
        Like = like;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getPostType() {
        return PostType;
    }

    public void setPostType(String postType) {
        PostType = postType;
    }

    public String getRateValue() {
        return RateValue;
    }

    public void setRateValue(String rateValue) {
        RateValue = rateValue;
    }

    public String getLiveStreamApp() {
        return LiveStreamApp;
    }

    public void setLiveStreamApp(String liveStreamApp) {
        LiveStreamApp = liveStreamApp;
    }

    public String getLiveStreamName() {
        return LiveStreamName;
    }

    public void setLiveStreamName(String liveStreamName) {
        LiveStreamName = liveStreamName;
    }



    public String getVideoType() {
        return VideoType;
    }

    public void setVideoType(String videoType) {
        VideoType = videoType;
    }


}
