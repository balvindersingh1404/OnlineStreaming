package com.headsupseven.corp.holder;

import com.headsupseven.corp.model.HomeLsitModel;

import java.util.Vector;


/**
 * Created by admin on 20/02/2017.
 */

public class AllHomeDataList {


    public static Vector<HomeLsitModel> allHomeDataList = new Vector<HomeLsitModel>();

    public static Vector<HomeLsitModel> getAllHomeDataList() {
        return allHomeDataList;
    }

    public static void setAllHomeDataList(Vector<HomeLsitModel> allHomeDataList) {
        AllHomeDataList.allHomeDataList = allHomeDataList;
    }

    public static HomeLsitModel getHomeDataList(int pos) {
        return allHomeDataList.elementAt(pos);
    }

    public static void setomeDataList(HomeLsitModel _allHomeDataList) {
        AllHomeDataList.allHomeDataList.addElement(_allHomeDataList);
    }

    public static void removeallhomeDatalist() {
        AllHomeDataList.allHomeDataList.removeAllElements();
    }
}
