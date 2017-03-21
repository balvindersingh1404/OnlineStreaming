package com.headsupseven.corp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.R;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by tmmac on 1/26/17.
 */


public class NewmessageAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;

    JSONArray data;

    public NewmessageAdapter(Context context) {
        this.mContext = context;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void UpdateData(JSONArray data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void DeleteData() {
        this.data = null;
    }

    @Override
    public int getCount() {
        if(this.data != null)
            // Please Update the code
            return this.data.length();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if(this.data != null)
            // Please Update the code
            return this.data.length();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        try {
            final JSONObject msgData = this.data.getJSONObject(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = l_Inflater.inflate(R.layout.row_comment_list, parent, false);
                holder.icon_prifile = (ImageView) convertView.findViewById(R.id.icon_prifile);
                holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_time.setVisibility(View.INVISIBLE);
            holder.tv_time.setText("");
            holder.tv_comment.setVisibility(View.GONE);

            holder.tv_username.setText(msgData.getString("username"));
        }catch (Exception e){

        }

        return convertView;

    }

    static class ViewHolder {
        ImageView icon_prifile;
        TextView tv_username;
        TextView tv_comment;
        TextView tv_time;


    }


}


