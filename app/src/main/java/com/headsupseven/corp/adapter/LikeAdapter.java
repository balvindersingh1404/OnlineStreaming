package com.headsupseven.corp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.OtherProfileActivity;
import com.headsupseven.corp.R;

/**
 * Created by tmmac on 1/26/17.
 */


    public class LikeAdapter extends BaseAdapter {
        private LayoutInflater l_Inflater;
        Context mContext;

        public LikeAdapter(Context context) {
            this.mContext = context;
            l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            // Please Update the code
            return 20;
        }

        @Override
        public Object getItem(int position) {

            // Please Update tha code
            return 20;
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
                convertView = l_Inflater.inflate(R.layout.row_like_list, parent, false);
                holder.icon_prifile = (ImageView) convertView.findViewById(R.id.icon_prifile);
                holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, OtherProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
            holder.icon_prifile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, OtherProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
            return convertView;

        }

    static class ViewHolder {
        ImageView icon_prifile;
        TextView tv_username;
        TextView tv_time;

    }


}


