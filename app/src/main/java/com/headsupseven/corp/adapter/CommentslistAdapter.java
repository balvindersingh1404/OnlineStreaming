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
import com.headsupseven.corp.model.CommentList;
import com.squareup.picasso.Picasso;

import java.util.Vector;

/**
 * Created by elanicdroid on 28/10/15.
 */

public class CommentslistAdapter extends RecyclerView
        .Adapter<CommentslistAdapter
        .DataObjectHolder> {
    Context mContext;
    private Vector<CommentList> mDataset;
    static OnItemClickListener mItemClickListener;

    public CommentslistAdapter(Context context, Vector<CommentList> myDataset) {
        mContext = context;
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_comment_list, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        if (holder instanceof DataObjectHolder) {
            DataObjectHolder viewHolder = holder;
            final CommentList homeLsitModel = mDataset.elementAt(position);
            viewHolder.tv_comment.setText(homeLsitModel.getContent());
            if(homeLsitModel.getCreatedAt().equalsIgnoreCase(""))
                 viewHolder.tv_time.setText("");
            else
                viewHolder.tv_time.setText(APIHandler.getTimeAgo(homeLsitModel.getCreatedAt()));

            viewHolder.tv_username.setText(homeLsitModel.getUserNmae());
            if(!homeLsitModel.getUserPic().equalsIgnoreCase(""))
                Picasso.with(mContext).load(homeLsitModel.getUserPic()).into(viewHolder.icon_prifile);

        }
    }
    public void addItem(CommentList dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void addnewItem(CommentList dataObj) {
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
        private TextView tv_comment;
        private TextView tv_time;
        public DataObjectHolder(View itemView) {
            super(itemView);

            icon_prifile=(ImageView)itemView.findViewById(R.id.icon_prifile);
            tv_username=(TextView) itemView.findViewById(R.id.tv_username);
            tv_comment=(TextView)itemView.findViewById(R.id.tv_comment);
            tv_time=(TextView)itemView.findViewById(R.id.tv_time);


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



