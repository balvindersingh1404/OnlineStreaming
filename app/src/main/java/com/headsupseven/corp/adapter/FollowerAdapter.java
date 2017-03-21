package com.headsupseven.corp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.FriendModel;
import com.squareup.picasso.Picasso;

import java.util.Vector;
/**
 * Created by tmmac on 1/26/17.
 */


public class FollowerAdapter extends RecyclerView
        .Adapter<FollowerAdapter
        .DataObjectHolder> {
    Context mContext;
    private Vector<FriendModel> mDataset;
    static OnItemClickListener mItemClickListener;

    public FollowerAdapter(Context context, Vector<FriendModel> myDataset) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_like_list, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        if (holder instanceof DataObjectHolder) {
            DataObjectHolder viewHolder = holder;
            final FriendModel mFriendModel = mDataset.elementAt(position);
            viewHolder.tv_username.setText(mFriendModel.getFollowUserName());
            viewHolder.tv_time.setText(APIHandler.getTimeAgo(mFriendModel.getCreatedAt()));
            if(!mFriendModel.getFollowUserAvatar().equalsIgnoreCase(""))
                Picasso.with(mContext).load(mFriendModel.getFollowUserAvatar()).into(viewHolder.icon_prifile);
        }
    }

    public void addItem(FriendModel dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void addnewItem(FriendModel dataObj) {
        mDataset.add(dataObj);
        notifyDataSetChanged();
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAllItem() {
        mDataset.removeAllElements();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        mItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        private ImageView icon_prifile;
        private TextView tv_username;
        private TextView tv_time;

        public DataObjectHolder(View itemView) {
            super(itemView);

            icon_prifile = (ImageView) itemView.findViewById(R.id.icon_prifile);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);


            itemView.setTag(getPosition());
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getPosition());
        }
    }
}




