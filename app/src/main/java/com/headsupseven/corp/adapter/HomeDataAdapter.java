package com.headsupseven.corp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.CommentActivity;
import com.headsupseven.corp.DonateActivity;
import com.headsupseven.corp.EventDetailsActivity;
import com.headsupseven.corp.HomebaseActivity;
import com.headsupseven.corp.LiveVideoPlayerActivity;
import com.headsupseven.corp.OtherProfileActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.Channgecustomview;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.HomeLsitModel;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


/**
 * Created by elanicdroid on 28/10/15.
 */

public class HomeDataAdapter extends RecyclerView
        .Adapter<HomeDataAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    Context mContext;
    private Vector<HomeLsitModel> mDataset;
    static OnItemClickListener mItemClickListener;
    String thumbURl = "";

    private VideoViewShouldClose videoCallback = null;

    public void setVideoTapCallback(VideoViewShouldClose callback) {
        this.videoCallback = callback;
    }


    public HomeDataAdapter(Context context, Vector<HomeLsitModel> myDataset) {
        mContext = context;
        mDataset = myDataset;
    }

    public HomeLsitModel getModelAt(int index) {
        return mDataset.get(index);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_headsup, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    public Vector<HomeLsitModel> getCurrentDataSet() {
        return mDataset;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        if (holder instanceof DataObjectHolder) {
            final DataObjectHolder viewHolder = holder;
            final HomeLsitModel homeLsitModel = mDataset.elementAt(position);

            try {
                viewHolder.vote.setText("Gift");
                viewHolder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sharePopupShow(homeLsitModel.getCreatedByName(), thumbURl);

                    }
                });
                viewHolder.ll_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (homeLsitModel.getPostType().equalsIgnoreCase("ads")) {
                            String urlData = "ads/" + homeLsitModel.getID() + "/like";
                            webCallForAdsLike(position, urlData);
                        } else {
                            String urlData = "feeds/" + homeLsitModel.getID() + "/like";
                            webCallForAdsLike(position, urlData);

                        }
                    }
                });
                viewHolder.ll_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Channgecustomview.commentTextView = viewHolder.tv_comment;
                        Channgecustomview.homeLsitModel = homeLsitModel;
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra("Postid", homeLsitModel.getID());
                        intent.putExtra("PostType", homeLsitModel.getPostType());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
                viewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(mContext, OtherProfileActivity.class);
                        intent.putExtra("user_id", "" + homeLsitModel.getCreatedBy());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
                viewHolder.vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, DonateActivity.class);
                        intent.putExtra("user_Name", "" + homeLsitModel.getCreatedByName());
                        intent.putExtra("CreatedBy", "" + homeLsitModel.getCreatedBy());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                    }
                });

                viewHolder.event_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (videoCallback != null) {
                                videoCallback.ClickOnThumbShouldCloseVideo();
                            }
                            Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                            intent.putExtra("Url_Stream", APIHandler.Instance().BuildLiveStreamWatchURL(homeLsitModel.getLiveStreamName()));
//                            intent.putExtra("Url_video", APIHandler.Instance().BuildLiveStreamVideoRecorded(homeLsitModel.getVideoName()));
                            intent.putExtra("Url_video", homeLsitModel.getVideoName());
                            intent.putExtra("PostType", homeLsitModel.getPostType());

                            intent.putExtra("is_360", !homeLsitModel.getVideoType().contentEquals("normal"));
                            intent.putExtra("is_Live", homeLsitModel.isPostStreaming());
                            intent.putExtra("postID", homeLsitModel.getID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);

                            int viewCOunt = Integer.parseInt(mDataset.get(position).getView());
                            mDataset.get(position).setView("" + (viewCOunt + 1));
                            notifyDataSetChanged();

                        } catch (Exception e) {

                        }
                    }
                });
                viewHolder.tv_name.setText(homeLsitModel.getCreatedByName());
                viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, OtherProfileActivity.class);
                        intent.putExtra("user_id", "" + homeLsitModel.getCreatedBy());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
                viewHolder.tv_posttime.setText(APIHandler.getTimeAgo(homeLsitModel.getCreatedAt()));
                viewHolder.tv_watching.setText("" + homeLsitModel.getView());
                viewHolder.tv_like.setText("" + homeLsitModel.getLike());
                viewHolder.tv_comment.setText("" + homeLsitModel.getComment());
                viewHolder.even_title.setText(homeLsitModel.getPostName());
                viewHolder.event_description.setText(homeLsitModel.getPostDescription());
                viewHolder.row_video_icon.setVisibility(View.VISIBLE);

                if (homeLsitModel.isLiked())
                    viewHolder.like_unlike.setImageResource(R.drawable.like_active);
                else
                    viewHolder.like_unlike.setImageResource(R.drawable.like_deactive);

                //-------------------For profile pic--------------------
                if (!homeLsitModel.getCreatedByAvatar().equalsIgnoreCase("")) {
                    Picasso.with(mContext)
                            .load(homeLsitModel.getCreatedByAvatar())
                            .placeholder(R.drawable.user_avater)
                            .error(R.drawable.user_avater)
                            .into(viewHolder.profilePic);
                } else {
                    viewHolder.profilePic.setImageResource(R.drawable.user_avater);
                }
                //-----------------------------------------------

                if (homeLsitModel.isFlagAdd()) {

                    viewHolder.add_image.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.add_image.setVisibility(View.GONE);
                }
                viewHolder.ic_live.setVisibility(View.INVISIBLE);
                viewHolder.ic_video_type.setVisibility(View.INVISIBLE);

                if (homeLsitModel.isPostStreaming()) {
                    viewHolder.ic_live.setVisibility(View.VISIBLE);
                    Random r = new Random();
                    thumbURl = APIHandler.Instance().StorageURL + "live/thumbs/" + homeLsitModel.getLiveStreamApp()
                            + "." + homeLsitModel.getLiveStreamName() + "." + homeLsitModel.getID() + "_best.png?r=" + r.nextInt(125);
                } else {
//
                    thumbURl = homeLsitModel.getPostThumbUrl();
                }

                if (homeLsitModel.getVideoType().contentEquals("360")) {
                    viewHolder.ic_video_type.setVisibility(View.VISIBLE);
                }


                Picasso.with(mContext).load(thumbURl).into(viewHolder.event_image);
            } catch (Exception e) {

            }


        }
    }

    public void webCallForAdsLike(final int position, String urlData) {
        //api/feeds/10/like
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN(urlData, param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codePost = mJsonObject.getInt("code");
                            String msg = mJsonObject.getString("msg");
                            if (codePost == 1) {
                                if (mDataset.get(position).isLiked()) {
                                    int like = Integer.parseInt(mDataset.get(position).getLike());
                                    mDataset.get(position).setLike("" + (like - 1));
                                    mDataset.get(position).setLiked(false);
                                    notifyDataSetChanged();

                                } else {
                                    int like = Integer.parseInt(mDataset.get(position).getLike());
                                    mDataset.get(position).setLike("" + (like + 1));
                                    mDataset.get(position).setLiked(true);
                                    notifyDataSetChanged();
                                }

                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }


    public void addItem(HomeLsitModel dataObj, int index) {
        mDataset.add(dataObj);
//        notifyItemInserted(index);
    }

    public void addnewItem(HomeLsitModel dataObj) {
        mDataset.add(dataObj);
    }

    public int topFeedsID() {
        if (mDataset.size() > 0)
            return mDataset.get(0).getID();
        else
            return -1;
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

        private ImageView menu, profilePic;
        private LinearLayout ll_like, ll_comment;
        private TextView vote;
        private ImageView like_unlike;
        private ImageView row_video_icon;
        private ImageView event_image, ic_live, ic_video_type;
        private TextView tv_name, tv_posttime, tv_watching, even_title, event_description, tv_like, tv_comment;
        private ImageView add_image;

        public DataObjectHolder(View itemView) {
            super(itemView);

            ll_like = (LinearLayout) itemView.findViewById(R.id.ll_like);
            ll_comment = (LinearLayout) itemView.findViewById(R.id.ll_comment);
            menu = (ImageView) itemView.findViewById(R.id.sidemenu);
            vote = (TextView) itemView.findViewById(R.id.vote);
            profilePic = (ImageView) itemView.findViewById(R.id.img_profile);
            row_video_icon = (ImageView) itemView.findViewById(R.id.row_video_icon);
            event_image = (ImageView) itemView.findViewById(R.id.event_image);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_posttime = (TextView) itemView.findViewById(R.id.tv_PostTime);
            tv_watching = (TextView) itemView.findViewById(R.id.tv_count);
            tv_like = (TextView) itemView.findViewById(R.id.txt_like);
            tv_comment = (TextView) itemView.findViewById(R.id.txt_comment);
            even_title = (TextView) itemView.findViewById(R.id.event_title);
            event_description = (TextView) itemView.findViewById(R.id.event_description);
            ic_live = (ImageView) itemView.findViewById(R.id.ic_live);
            ic_video_type = (ImageView) itemView.findViewById(R.id.ic_video_type);
            add_image = (ImageView) itemView.findViewById(R.id.add_image);
            like_unlike = (ImageView) itemView.findViewById(R.id.like_unlike);


            row_video_icon.setVisibility(View.GONE);
            itemView.setTag(getPosition());
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getPosition());
        }
    }

    public void sharePopupShow(final String title, final String thumbURl) {
        final Dialog d = new Dialog((HomebaseActivity) mContext, android.R.style.Theme_Translucent);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.share_popup);
        d.setCancelable(false);

        TextView tv_share = (TextView) d.findViewById(R.id.tv_share);
        LinearLayout ll_main = (LinearLayout) d.findViewById(R.id.ll_main);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTo(mContext, title, thumbURl);
                d.dismiss();

            }
        });

        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }


    public static void shareTo(Context mContext, String title, String thumbURl) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "" + thumbURl);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "" + title);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via Headsup7"));
    }

}



