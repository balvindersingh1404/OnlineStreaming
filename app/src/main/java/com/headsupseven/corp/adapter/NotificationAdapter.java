package com.headsupseven.corp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.ChatnewActivity;
import com.headsupseven.corp.LiveVideoPlayerActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.NotificationList;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by tmmac on 1/26/17.
 */


public class NotificationAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;
    private Vector<NotificationList> allNotificationLists;

    public NotificationAdapter(Context context, Vector<NotificationList> allNotificationLists) {
        this.mContext = context;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allNotificationLists = allNotificationLists;
    }

    public void addObject(NotificationList mNotificationList) {
        allNotificationLists.addElement(mNotificationList);
        notifyDataSetChanged();

    }

    public void detailsOfPost(final String postId) {
        // api/feeds/ID/get
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("feeds/" + postId + "/get", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.w("response", "are" + response);
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            if (codePost == 1) {
                                JSONObject msg = mJsonObject.getJSONObject("msg");
                                String LiveStreamName = msg.getString("LiveStreamName");
                                String VideoName = msg.getString("VideoName");
                                String is_360 = msg.getString("VideoType");
                                boolean is_Live = msg.getBoolean("IsPostStreaming");
                                String postType = msg.getString("PostType");


                                Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                                intent.putExtra("Url_Stream", LiveStreamName);
                                intent.putExtra("Url_video", VideoName);
                                intent.putExtra("likecount",msg.getString("Like"));

                                intent.putExtra("is_360", !is_360.contentEquals("normal"));
                                intent.putExtra("is_Live", is_Live);
                                intent.putExtra("postID", Integer.parseInt(postId));
                                intent.putExtra("PostType", postType);
                                mContext.startActivity(intent);


                            }


                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    @Override
    public int getCount() {

        // Please Update the code
        return allNotificationLists.size();
    }

    @Override
    public Object getItem(int position) {

        // Please Update tha code
        return allNotificationLists.get(position);
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
            convertView = l_Inflater.inflate(R.layout.row_notification, parent, false);
            holder.icon_prifile = (ImageView) convertView.findViewById(R.id.icon_prifile);
            holder.notificioan_text = (TextView) convertView.findViewById(R.id.notificioan_text);
            holder.notification_time = (TextView) convertView.findViewById(R.id.notification_time);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NotificationList mNotificationList = allNotificationLists.get(position);
        if (mNotificationList.isRead()) {
            holder.notificioan_text.setText(mNotificationList.getContent());
        } else {
            String s = "<b>" + mNotificationList.getContent() + "</b>";
            holder.notificioan_text.setText(Html.fromHtml(s));
        }

        holder.notification_time.setText(APIHandler.getTimeAgo(mNotificationList.getCreatedAt()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationList mNotificationList = allNotificationLists.get(position);
                if (mNotificationList.getReadAction() == 1) {

                } else if (mNotificationList.getReadAction() == 2) {
                    Intent mIntent = new Intent(mContext, ChatnewActivity.class);
                    mIntent.putExtra("ChatUserName", "ChatUserName");
                    mIntent.putExtra("ChatUser", Integer.parseInt(mNotificationList.getUserID()));
                    mIntent.putExtra("mSessionID", "");
                    mContext.startActivity(mIntent);
                } else if (mNotificationList.getReadAction() == 3) {
                    readDataObject(mNotificationList.getReadData());
                } else if (mNotificationList.getReadAction() == 4) {
                    readDataObject(mNotificationList.getReadData());
                }
            }
        });
        //========showing the image==========
        try {
            JSONObject mJsonObject = new JSONObject(mNotificationList.getReadData());
            String thumbURL = mJsonObject.getString("thumbURL");
            if (!thumbURL.equalsIgnoreCase("")) {
                Picasso.with(mContext)
                        .load(thumbURL)
                        .placeholder(R.drawable.user_avater)
                        .error(R.drawable.user_avater)
                        .into(holder.icon_prifile);
            } else {
                holder.icon_prifile.setImageResource(R.drawable.user_avater);
            }
        } catch (Exception ex) {
        }

        return convertView;
    }

    public void readDataObject(String response) {
        try {
            JSONObject mJsonObject = new JSONObject(response);
            String postID = mJsonObject.getString("postID");
            detailsOfPost(postID);

        } catch (Exception ex) {
        }
    }

    static class ViewHolder {
        ImageView icon_prifile;
        TextView notificioan_text;
        TextView notification_time;

    }


}


