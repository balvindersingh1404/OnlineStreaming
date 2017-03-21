package com.headsupseven.corp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.AddmanagerdetailsActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.model.AddmanagerList;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by tmmac on 1/26/17.
 */


public class AddmanagerAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;
    private Vector<AddmanagerList> addmanagerLists;

    public AddmanagerAdapter(Context context, Vector<AddmanagerList> addmanagerLists) {
        this.mContext = context;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addmanagerLists = addmanagerLists;
    }

    public void setAddmanagerLists(AddmanagerList mAddmanagerList) {
        addmanagerLists.add(mAddmanagerList);
        notifyDataSetChanged();
    }

    public AddmanagerList getAddmanagerList(int pos) {
        return addmanagerLists.get(pos);
    }

    @Override
    public int getCount() {
        // Please Update the code
        return addmanagerLists.size();
    }

    public void clearAdapder() {
        // Please Update the code
        addmanagerLists.removeAllElements();
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {

        // Please Update tha code
        return addmanagerLists.get(position);
    }

    public void stopAddes(final int position, String ID) {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("ads/" + ID + "/stop", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            String msg = mJsonObject.getString("msg");
                            if (codePost == 1) {
                                addmanagerLists.get(position).setState("2");
                                notifyDataSetChanged();
                                notifyDataSetChanged();
                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    public void resumeAddes(final int position, String ID) {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("ads/" + ID + "/resume", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            String msg = mJsonObject.getString("msg");
                            if (codePost == 1) {
                                addmanagerLists.get(position).setState("1");
                                notifyDataSetChanged();
                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = l_Inflater.inflate(R.layout.row_addmanager, parent, false);

            holder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.add_State = (ImageView) convertView.findViewById(R.id.add_State);

            holder.adde_title = (TextView) convertView.findViewById(R.id.adde_title);
            holder.cb_next_money = (CheckBox) convertView.findViewById(R.id.cb_next_money);
            holder.add_State_title = (TextView) convertView.findViewById(R.id.add_State_title);
            holder.AdsMoneyAmount = (TextView) convertView.findViewById(R.id.AdsMoneyAmount);
            // holder.root_view = (LinearLayout) convertView.findViewById(R.id.root_view);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        AddmanagerList addmanagerList = addmanagerLists.get(position);


        if (addmanagerList.getState().equalsIgnoreCase("1")) {
            holder.add_State_title.setText("Active");
            holder.add_State.setImageResource(R.drawable.active);
            holder.cb_next_money.setChecked(true);
        } else if (addmanagerList.getState().equalsIgnoreCase("2")) {
            holder.add_State_title.setText("Deactive");
            holder.add_State.setImageResource(R.drawable.inactive);
            holder.cb_next_money.setChecked(false);
        } else {
            holder.add_State_title.setText("Ended");
            holder.add_State.setImageResource(R.drawable.inactive);
            holder.cb_next_money.setChecked(false);
            holder.cb_next_money.setEnabled(false);
            holder.cb_next_money.setClickable(false);
        }

        holder.AdsMoneyAmount.setText(addmanagerList.getAdsMoneyAmount() + "$");
        holder.adde_title.setText(addmanagerList.getPostName());
        holder.cb_next_money.setTag("" + position);
        holder.cb_next_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.parseInt(view.getTag().toString().trim());
                AddmanagerList AddmanagerList = addmanagerLists.get(pos);
                if (AddmanagerList.getState().equalsIgnoreCase("1")) {
                    stopAddes(pos, AddmanagerList.getID());
                } else if (AddmanagerList.getState().equalsIgnoreCase("2")) {
                    resumeAddes(pos, AddmanagerList.getID());
                } else {
//                    resumeAddes(pos, AddmanagerList.getID());
                }


            }
        });
        if (!addmanagerList.getVideoThumbUrl().equalsIgnoreCase(""))
            Picasso.with(mContext).load(addmanagerList.getVideoThumbUrl()).into(holder.profile_image);

//        holder.root_view.setTag(addmanagerList);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddmanagerList addmanagerList = addmanagerLists.get(position);
                MyApplication.mAddmanagerList = addmanagerList;
                Intent mIntent = new Intent(mContext, AddmanagerdetailsActivity.class);
                mContext.startActivity(mIntent);
            }
        });
        return convertView;

    }

    static class ViewHolder {
        ImageView profile_image;
        TextView adde_title;
        CheckBox cb_next_money;
        ImageView add_State;
        TextView add_State_title;
        TextView AdsMoneyAmount;
        //LinearLayout root_view;

    }


}


