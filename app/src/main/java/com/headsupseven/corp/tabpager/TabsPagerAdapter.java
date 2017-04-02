package com.headsupseven.corp.tabpager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.headsupseven.corp.fragment.ExploreFragment;
import com.headsupseven.corp.fragment.Headsup7Fragment;
import com.headsupseven.corp.fragment.HomeFragment;
import com.headsupseven.corp.fragment.MyprofileFragment;

/**
 * Created by Prosanto on 2/4/17.
 */


public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new HomeFragment();
            case 1:
                return new Headsup7Fragment();
            case 2:
                return new ExploreFragment();
            case 3:
                return new MyprofileFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}