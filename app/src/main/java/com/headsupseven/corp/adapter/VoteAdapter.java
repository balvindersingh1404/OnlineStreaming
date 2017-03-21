package com.headsupseven.corp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.headsupseven.corp.R;


/**
 * Created by tmmac on 1/26/17.
 */


public class VoteAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;

    public VoteAdapter(Context context) {
        this.mContext = context;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        // Please Update the code
        return 10;
    }

    @Override
    public Object getItem(int position) {

        // Please Update tha code
        return 10;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = l_Inflater.inflate(R.layout.row_vote_list, parent, false);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    static class ViewHolder {


    }


}


