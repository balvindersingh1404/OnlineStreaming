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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by tmmac on 1/26/17.
 */


public class InboxAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    private Context mContext;
    private JSONArray data;


    public InboxAdapter(Context context) {
        this.mContext = context;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void UpdateData(JSONArray data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void DeleteData() {
        this.data = null;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {

        if (this.data != null)
            // Please Update the code
            return this.data.length();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {

        if (this.data != null)
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
                convertView = l_Inflater.inflate(R.layout.row_inbox, parent, false);
                holder.icon_prifile = (ImageView) convertView.findViewById(R.id.icon_prifile);
                holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                holder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_username.setText(msgData.getString("ChatUserName"));
            int MessageType = msgData.getInt("MessageType");
            if (MessageType == 1) {
                //text
                holder.tv_comment.setText(msgData.getString("LastMessage"));
            } else if (MessageType == 4) {
                //photo
                holder.tv_comment.setText("[photo]");

            } else if (MessageType == 5) {
                //video
                holder.tv_comment.setText("[Video]");
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date newDate = format.parse(msgData.getString("UpdatedAt"));
                format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                Date d = format.parse(format.format(newDate));
//                holder.tv_time.setText(GetTimeCovertAgo.getLikeConverter(d.getTime()));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.icon_prifile.setImageResource(R.drawable.user_avater);


        } catch (Exception ex) {

        }

//        if (!homeLsitModel.getCreatedByAvatar().equalsIgnoreCase("")) {
//            Picasso.with(mContext)
//                    .load(homeLsitModel.getCreatedByAvatar())
//                    .placeholder(R.drawable.user_avater)
//                    .error(R.drawable.user_avater)
//                    .into(viewHolder.profilePic);
//        } else {
//            viewHolder.profilePic.setImageResource(R.drawable.user_avater);
//        }
//
//
        return convertView;

    }

    static class ViewHolder {
        ImageView icon_prifile;
        TextView tv_username;
        TextView tv_comment;
        TextView tv_time;


    }


}


