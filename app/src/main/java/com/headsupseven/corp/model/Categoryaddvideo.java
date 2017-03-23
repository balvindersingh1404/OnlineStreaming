package com.headsupseven.corp.model;

/**
 * Created by Prosanto on 3/23/17.
 */

public class Categoryaddvideo {
    private String ID="";
    private String Name="";
    private boolean flaABoolean=false;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isFlaABoolean() {
        return flaABoolean;
    }

    public void setFlaABoolean(boolean flaABoolean) {
        this.flaABoolean = flaABoolean;
    }
}
