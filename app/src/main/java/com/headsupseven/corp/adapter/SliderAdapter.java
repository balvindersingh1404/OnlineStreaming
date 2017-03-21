package com.headsupseven.corp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.headsupseven.corp.fragment.SliderFragment;


public class SliderAdapter extends FragmentStatePagerAdapter {
    public SliderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        return SliderFragment.newInstance(position);
    }
}
