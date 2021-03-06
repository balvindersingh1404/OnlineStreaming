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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.headsupseven.corp.ChatnewActivity;
import com.headsupseven.corp.CommentActivity;
import com.headsupseven.corp.DonateActivity;
import com.headsupseven.corp.FollowerActivity;
import com.headsupseven.corp.FollowingActivity;
import com.headsupseven.corp.LiveVideoPlayerActivity;
import com.headsupseven.corp.OtherProfileActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.Channgecustomview;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.HomeLsitModel;
import com.headsupseven.corp.utils.PopupAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Prosanto on 3/7/17.
 */
public class NewOtherProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private AQuery androidQuery;
    private String AvatarUrl = "";
    String thumbURl = "";


    Vector<HomeLsitModel> myDataset = new Vector<>();
    private Context mContext;
    private String response;
    private Boolean IsFollowing;
    private String user_id;

    private VideoViewShouldClose videoCallback = null;

    public void setVideoTapCallback(VideoViewShouldClose callback) {
        this.videoCallback = callback;
    }

    public NewOtherProfileAdapter(Context context, String puser_id, Vector<HomeLsitModel> myDataset) {
        mContext = context;
        this.myDataset = myDataset;
        user_id = puser_id;
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
//        notifyItemChanged(0);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_other_profile, viewGroup, false);
            return new HeaderViewHolder(view);

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            try {
                JSONObject mJsonObject = new JSONObject(response);
                int code = mJsonObject.getInt("code");
                if (code == 1) {
                    JSONObject msg = mJsonObject.getJSONObject("msg");
                    final int Follower = msg.getInt("Follower");
                    String Following = msg.getString("Following");
                    String PostCount = msg.getString("PostCount");
                    IsFollowing = msg.getBoolean("IsFollowing");


                    JSONObject userObject = msg.getJSONObject("UserModel");
                    int ID = userObject.getInt("ID");
                    String CreatedAt = userObject.getString("CreatedAt");
                    String UpdatedAt = userObject.getString("UpdatedAt");
                    String UserName = userObject.getString("UserName");
                    String FullName = userObject.getString("FullName");
                    String Email = userObject.getString("Email");
                    AvatarUrl = userObject.getString("AvatarUrl");
                    String BannerUrl = userObject.getString("BannerUrl");


                    if (IsFollowing)
                        holder.check_follow_unfollow.setChecked(true);
                    else
                        holder.check_follow_unfollow.setChecked(false);


                    holder.tv_number_post.setText("" + PostCount);

                    holder.tv_username.setText("" + UserName);
                    holder.tv_full_name.setText("" + FullName);

                    holder.tv_number_follower.setText("" + Follower);
                    holder.tv_number_follower.setTag("" + Follower);

                    holder.tv_number_following.setText("" + Following);
                    holder.tv_number_following.setTag("" + Following);
                    holder.img_profile.setTag("" + AvatarUrl);


                    holder.ll_follower.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, FollowerActivity.class);
                            intent.putExtra("user_id", "" + user_id);
                            mContext.startActivity(intent);

                        }
                    });

                    holder.ll_following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, FollowingActivity.class);
                            intent.putExtra("user_id", "" + user_id);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.user_msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent mIntent = new Intent(mContext, ChatnewActivity.class);
                            mIntent.putExtra("ChatUser", Integer.parseInt(user_id));
                            mContext.startActivity(mIntent);

                        }
                    });


                    holder.check_follow_unfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (IsFollowing) {
                                Log.w("Floow", "are " + user_id);
                                follow_unfollow("user/app/" + APIHandler.Instance().user.userID + "/un-follow", 0, user_id);
                                holder.tv_number_following.setText("" + (Follower - 1));
                                holder.check_follow_unfollow.setChecked(false);
                            } else {
                                Log.w("Floow", "are " + user_id);
                                follow_unfollow("user/app/" + APIHandler.Instance().user.userID + "/set-follow", 1, user_id);
                                holder.tv_number_following.setText("" + (Follower + 1));
                                holder.check_follow_unfollow.setChecked(true);
                            }
                        }
                    });


                    if (!AvatarUrl.equalsIgnoreCase("")) {
                        Picasso.with(mContext).load(AvatarUrl).placeholder(R.drawable.user_avater).error(R.drawable.user_avater).into(holder.img_profile);
                    } else {
                        holder.img_profile.setImageResource(R.drawable.user_avater);
                    }
                    Picasso.with(mContext)
                            .load(BannerUrl)
                            .into(holder.img_banner);


                }
            } catch (Exception ex) {

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
            holder.vote.setText("Gift");

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
                        Intent intent = new Intent(mContext, DonateActivity.class);
                        intent.putExtra("user_Name", "" + mHomeLsitModel.getCreatedByName());
                        intent.putExtra("CreatedBy", "" + mHomeLsitModel.getCreatedBy());
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
                        intent.putExtra("likecount", mHomeLsitModel.getLike());
                        intent.putExtra("liked", mHomeLsitModel.isLiked());

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


    public void follow_unfollow(String query, final int type, final String user_id) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("user", "" + user_id);
        APIHandler.Instance().POST_BY_AUTHEN(query, param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ((OtherProfileActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            Log.w("response", "are" + response);
                            JSONObject mJsonObject = new JSONObject(response);
                            int code2 = mJsonObject.getInt("code");
                            if (code2 == 1) {
                                if (type == 0) {
//                                    check_follow_unfollow.setChecked(false);
                                    IsFollowing = false;
                                } else {
                                    IsFollowing = true;
//                                    check_follow_unfollow.setChecked(true);


                                }
                                PopupAPI.showToast(mContext, mJsonObject.getString("msg"));
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        });
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
        private ImageView menu, profilePic;
        private LinearLayout ll_like, ll_comment;
        TextView vote;
        private ImageView row_video_icon;
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

    // The ViewHolders for Header, Item and Footer
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public View View;

        private LinearLayout ll_post, ll_follower, ll_following;
        private View viewleft, viewmiddle, viewright;
        private ImageView user_msg;
        private TextView tv_number_post, tv_number_follower, tv_number_following;
        private TextView tv_full_name, tv_username;
        private ImageView img_banner;
        private RoundedImageView img_profile;
        private AQuery androidQuery;
        private CheckBox check_follow_unfollow;
        private String user_id = "";
        private TextView profile_bio_data;
        private Vector<HomeLsitModel> allHomeLsitModels = new Vector<>();
        private String AvatarUrl = "";
        private boolean IsFollowing = false;


        public HeaderViewHolder(View itemView) {
            super(itemView);


            tv_full_name = (TextView) itemView.findViewById(R.id.tv_full_name);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            profile_bio_data = (TextView) itemView.findViewById(R.id.profile_bio_data);
            ll_post = (LinearLayout) itemView.findViewById(R.id.ll_post);
            ll_follower = (LinearLayout) itemView.findViewById(R.id.ll_follower);
            ll_following = (LinearLayout) itemView.findViewById(R.id.ll_folowing);
            tv_number_post = (TextView) itemView.findViewById(R.id.tv_number_post);
            tv_number_follower = (TextView) itemView.findViewById(R.id.tv_number_follower);
            tv_number_following = (TextView) itemView.findViewById(R.id.tv_number_following);
            img_profile = (RoundedImageView) itemView.findViewById(R.id.img_profile);
            img_banner = (ImageView) itemView.findViewById(R.id.img_banner);
            check_follow_unfollow = (CheckBox) itemView.findViewById(R.id.check_follow_unfollow);
            user_msg = (ImageView) itemView.findViewById(R.id.user_msg);


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
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via Headsup7"));
    }


}

