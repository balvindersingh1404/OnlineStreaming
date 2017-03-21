package com.headsupseven.corp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.headsupseven.corp.R;


public final class SliderFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    public static SliderFragment newInstance(int content) {
        SliderFragment fragment = new SliderFragment();
        fragment.mContent = content;
        return fragment;
    }

    private Context mContext;
    private int mContent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getInt(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.row_slider,
                container, false);
        mContext = getActivity().getApplicationContext();
        ImageView sliding_image = (ImageView) layout.findViewById(R.id.sliding_image);
        sliding_image.setImageResource(R.drawable.device1);
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContent);
    }
}
