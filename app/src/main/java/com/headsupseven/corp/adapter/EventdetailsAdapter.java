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

import com.androidquery.AQuery;
import com.headsupseven.corp.CommentActivity;
import com.headsupseven.corp.DonateActivity;
import com.headsupseven.corp.HomebaseActivity;
import com.headsupseven.corp.LiveVideoPlayerActivity;
import com.headsupseven.corp.OtherProfileActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.VoteActivity;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.Channgecustomview;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.HomeLsitModel;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Prosanto on 3/7/17.
 */
public class EventdetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private AQuery androidQuery;
    Vector<HomeLsitModel> myDataset = new Vector<>();
    private Context mContext;
    private String response = "";
    private VideoViewShouldClose videoCallback = null;
    String thumbURl = "";
    private int EventId =0;

    public void setVideoTapCallback(VideoViewShouldClose callback) {
        this.videoCallback = callback;
    }

    public EventdetailsAdapter(Context context, Vector<HomeLsitModel> myDataset,final int EventId) {
        mContext = context;
        this.myDataset = myDataset;
        this.EventId=EventId;
        androidQuery = new AQuery(mContext);
    }

    public HomeLsitModel getModelAt(int index) {
        return myDataset.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;

        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public void deleteAllItem() {
        myDataset.removeAllElements();

    }

    public void addJSONboject(String responsee) {
        this.response = responsee;
        notifyItemChanged(0);
    }

    public void addnewItem(HomeLsitModel dataObj) {
        myDataset.add(dataObj);


    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position > myDataset.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_headsup, viewGroup, false);
            return new ItemViewHolder(view);

        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_event, viewGroup, false);
            return new HeaderViewHolder(view);

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            if (!response.equalsIgnoreCase("")) {
                try {
                    JSONObject mJsonObject = new JSONObject(response);
                    holder.event_title.setText(mJsonObject.getString("Title"));
                    holder.event_details.setText(mJsonObject.getString("Description"));

                    String CoverUrl = mJsonObject.getString("CoverUrl");
                    String ProfileUrl = mJsonObject.getString("ProfileUrl");
                    String StartDate = mJsonObject.getString("StartDate");
                    String EndDate = mJsonObject.getString("EndDate");
                    if (!CoverUrl.equalsIgnoreCase("")) {
                        Picasso.with(mContext)
                                .load(CoverUrl)
                                .into(holder.event_cover_image);
                    }

                    if (!ProfileUrl.equalsIgnoreCase("")) {
                        Picasso.with(mContext)
                                .load(ProfileUrl)
                                .into(holder.event_image);
                    }
                    //========= calculation of start date
                    //2017-03-22T11:04:03
                    String dateStr=StartDate;//.substring(0,(StartDate.length()-1));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE hh:mm a");

                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");
                    Date date = currentDateFormat.parse(dateStr);

                    String dateStrDate = dateFormat.format(date);
                    String dateStrTime = timeFormat.format(date);
                    holder.Start_date.setText(dateStrDate);
                    holder.Start_time.setText(dateStrTime);
                    //2017-03-21T15:04:05Z

                    String dateEnd=EndDate;//.substring(0,(EndDate.length()-1));
                    Date dateEndw = currentDateFormat.parse(dateEnd);

                    String dateEndDate = dateFormat.format(dateEndw);
                    String dateEndTime = timeFormat.format(dateEndw);
                    holder.End_date.setText(dateEndDate);
                    holder.End_time.setText(dateEndTime);



                } catch (Exception e) {
                    Log.w("Exception","wre"+e.getMessage());
                }


            }

        } else if (viewHolder instanceof ItemViewHolder) {

            final ItemViewHolder holder = (ItemViewHolder) viewHolder;
            final HomeLsitModel mHomeLsitModel = myDataset.elementAt(position - 1);


            holder.tv_name.setText(mHomeLsitModel.getCreatedByName());
            holder.tv_posttime.setText(APIHandler.getTimeAgo(mHomeLsitModel.getCreatedAt()));
            holder.tv_watching.setText("" + mHomeLsitModel.getView());
            holder.tv_like.setText("" + mHomeLsitModel.getLike());
            holder.tv_comment.setText("" + mHomeLsitModel.getComment());
            holder.even_title.setText(mHomeLsitModel.getPostName());
            holder.event_description.setText(mHomeLsitModel.getPostDescription());
            holder.row_video_icon.setVisibility(View.VISIBLE);
            holder.ic_live.setVisibility(View.INVISIBLE);
            holder.ic_video_type.setVisibility(View.INVISIBLE);
            holder.vote.setText("VOTE");

            String post_type = mHomeLsitModel.getPostType();
            if (post_type.equalsIgnoreCase("news")) {
                holder.row_video_icon.setVisibility(View.VISIBLE);
                holder.row_video_icon.setImageResource(R.drawable.news_icon);
            } else if (post_type.equalsIgnoreCase("event")) {
                holder.row_video_icon.setVisibility(View.VISIBLE);
                holder.row_video_icon.setImageResource(R.drawable.events_icon);
            } else {
                String videoType = mHomeLsitModel.getVideoType();
                if (videoType.equalsIgnoreCase("photo")) {
                    holder.row_video_icon.setVisibility(View.VISIBLE);
                    holder.row_video_icon.setImageResource(R.drawable.photos_icon);
                }
                if (videoType.contentEquals("360")) {
                    holder.row_video_icon.setVisibility(View.VISIBLE);
                    holder.row_video_icon.setImageResource(R.drawable.video_icon_360);
                } else {
                    holder.row_video_icon.setVisibility(View.VISIBLE);
                    holder.row_video_icon.setImageResource(R.drawable.ic_play);
                }
            }
            ///
            holder.ll_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mHomeLsitModel.getPostType().equalsIgnoreCase("ads")) {
                        String urlData = "ads/" + mHomeLsitModel.getID() + "/like";
                        webCallForAdsLike(position - 1, urlData);
                    } else {
                        String urlData = "feeds/" + mHomeLsitModel.getID() + "/like";
                        webCallForAdsLike(position - 1, urlData);

                    }

                }
            });
            if (mHomeLsitModel.isLiked())
                holder.like_unlike.setImageResource(R.drawable.like_active);
            else
                holder.like_unlike.setImageResource(R.drawable.like_deactive);

            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mHomeLsitModel.getPostType().equalsIgnoreCase("ads")) {

                    } else {
                        Channgecustomview.homeLsitModel = mHomeLsitModel;
                        Channgecustomview.commentTextView = holder.tv_comment;
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra("Postid", mHomeLsitModel.getID());
                        intent.putExtra("PostType", mHomeLsitModel.getPostType());
                        mContext.startActivity(intent);
                    }

                }
            });
            holder.profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, OtherProfileActivity.class);
                    intent.putExtra("user_id", "" + mHomeLsitModel.getCreatedBy());
                    mContext.startActivity(intent);
                }
            });
            holder.vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mHomeLsitModel.getPostType().equalsIgnoreCase("ads")) {

                    } else {
                        Intent intent = new Intent(mContext, VoteActivity.class);
                        intent.putExtra("voteId", EventId);
                        intent.putExtra("post_id", mHomeLsitModel.getID());
                        mContext.startActivity(intent);
                    }
                }
            });

            holder.event_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        if (videoCallback != null) {
                            videoCallback.ClickOnThumbShouldCloseVideo();
                        }

                        Intent intent = new Intent(mContext, LiveVideoPlayerActivity.class);
                        intent.putExtra("Url_Stream", APIHandler.Instance().BuildLiveStreamWatchURL(mHomeLsitModel.getLiveStreamName()));
                        intent.putExtra("Url_video", mHomeLsitModel.getVideoName());
                        intent.putExtra("PostType", mHomeLsitModel.getPostType());

                        intent.putExtra("is_360", !mHomeLsitModel.getVideoType().contentEquals("normal"));
                        intent.putExtra("is_Live", mHomeLsitModel.isPostStreaming());
                        intent.putExtra("postID", mHomeLsitModel.getID());
                        mContext.startActivity(intent);

                        int viewCOunt = Integer.parseInt(mHomeLsitModel.getView());
                        myDataset.get(position).setView("" + (viewCOunt + 1));
                        notifyDataSetChanged();


                    } catch (Exception e) {

                    }
                }
            });
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    sharePopupShow(mHomeLsitModel.getCreatedByName(), thumbURl);

                }
            });
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, OtherProfileActivity.class);
                    intent.putExtra("user_id", "" + mHomeLsitModel.getCreatedBy());
                    mContext.startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                }
            });
            if (!mHomeLsitModel.getCreatedByAvatar().equalsIgnoreCase("")) {
                Picasso.with(mContext)
                        .load(mHomeLsitModel.getCreatedByAvatar())
                        .placeholder(R.drawable.user_avater)
                        .error(R.drawable.user_avater)
                        .into(holder.profilePic);
            } else {
                holder.profilePic.setImageResource(R.drawable.user_avater);
            }


            if (mHomeLsitModel.isPostStreaming()) {
                holder.ic_live.setVisibility(View.VISIBLE);
                Random r = new Random();
                thumbURl = APIHandler.Instance().StorageURL + "live/thumbs/" + mHomeLsitModel.getLiveStreamApp()
                        + "." + mHomeLsitModel.getLiveStreamName() + "." + mHomeLsitModel.getID() + "_best.png?r=" + r.nextInt(125);
            } else {
                thumbURl = mHomeLsitModel.getPostThumbUrl();

            }

            if (mHomeLsitModel.getVideoType().contentEquals("360")) {
                holder.ic_video_type.setVisibility(View.VISIBLE);
            }
            Picasso.with(mContext)
                    .load(thumbURl)
                    .into(holder.event_image);

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
                                if (myDataset.get(position).isLiked()) {
                                    int like = Integer.parseInt(myDataset.get(position).getLike());
                                    myDataset.get(position).setLike("" + (like - 1));
                                    myDataset.get(position).setLiked(false);
                                    notifyDataSetChanged();

                                } else {
                                    int like = Integer.parseInt(myDataset.get(position).getLike());
                                    myDataset.get(position).setLike("" + (like + 1));
                                    myDataset.get(position).setLiked(true);
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


    @Override
    public int getItemCount() {
        return this.myDataset.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView menu;
        private LinearLayout ll_like, ll_comment;
        TextView vote;
        private ImageView row_video_icon, profilePic;
        private ImageView event_image, ic_live, ic_video_type;
        private TextView tv_name, tv_posttime, tv_watching, even_title, event_description, tv_like, tv_comment;
        private ImageView like_unlike;

        public ItemViewHolder(View v) {
            super(v);
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
            row_video_icon.setVisibility(View.GONE);
            like_unlike = (ImageView) itemView.findViewById(R.id.like_unlike);
            itemView.setTag(getPosition());


        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView event_title;
        private TextView event_details;
        private TextView Start_date;
        private TextView Start_time;
        private TextView End_date;
        private TextView End_time;
        private ImageView event_cover_image;
        private ImageView event_image;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            event_title = (TextView) itemView.findViewById(R.id.event_title);
            event_details = (TextView) itemView.findViewById(R.id.event_details);
            Start_date = (TextView) itemView.findViewById(R.id.Start_date);
            Start_time = (TextView) itemView.findViewById(R.id.Start_time);
            End_date = (TextView) itemView.findViewById(R.id.End_date);
            End_time = (TextView) itemView.findViewById(R.id.End_time);
            event_image = (ImageView) itemView.findViewById(R.id.event_image);
            event_cover_image = (ImageView) itemView.findViewById(R.id.event_cover_image);

        }
    }

    public void sharePopupShow(final String title, final String thumbURl) {
        final Dialog d = new Dialog(mContext, android.R.style.Theme_Translucent);
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
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }




}

