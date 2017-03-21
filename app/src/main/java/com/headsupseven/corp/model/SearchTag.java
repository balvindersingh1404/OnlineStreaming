package com.headsupseven.corp.model;

/**
 * Created by admin on 26/02/2017.
 */

public class SearchTag {

    private String ID;
    private String CreatedAt;
    private String TagName;
    private String PostRelate;
    private String Interactive;

    public String getInteractive() {
        return Interactive;
    }

    public void setInteractive(String interactive) {
        Interactive = interactive;
    }

    public String getPostRelate() {
        return PostRelate;
    }

    public void setPostRelate(String postRelate) {
        PostRelate = postRelate;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


}
