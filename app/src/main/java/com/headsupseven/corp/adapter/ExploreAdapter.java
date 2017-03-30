package com.headsupseven.corp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.randomcolor.RandomColor;
import com.headsupseven.corp.CommentActivity;
import com.headsupseven.corp.DonateActivity;
import com.headsupseven.corp.LiveVideoPlayerActivity;
import com.headsupseven.corp.OtherProfileActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.Channgecustomview;
import com.headsupseven.corp.customview.VideoViewShouldClose;
import com.headsupseven.corp.model.CategoryList;
import com.headsupseven.corp.model.HomeLsitModel;
import com.headsupseven.corp.model.SearchTag;
import com.headsupseven.corp.utils.AdapterCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;


/**
 * Created by Prosanto on 3/7/17.
 */
//http://www.gadgetsaint.com/android/recyclerview-header-footer-pagination/#.WL5_EhKGOuU
public class ExploreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private String thumbURl = "";
    private Vector<HomeLsitModel> myDataset = new Vector<>();
    private Context mContext;
    private Vector<CategoryList> allCategoryList = new Vector<CategoryList>();
    private HashMap<String, String> mapCategory = new HashMap<>();
    private Vector<SearchTag> allSearchTag = new Vector<SearchTag>();
    private HashMap<String, String> mapTag = new HashMap<>();
    private VideoViewShouldClose videoCallback = null;
    private AdapterCallback mAdapterCallback = null;

    private Button lastCategorySelect = null;
    private Button lastTagSelect = null;

    public void setAdapterCallback(AdapterCallback callback) {
        this.mAdapterCallback = callback;
    }
    public void setVideoTapCallback(VideoViewShouldClose callback) {
        this.videoCallback = callback;
    }
    public HomeLsitModel getModelAt(int index) {
        return myDataset.get(index);
    }
    public ExploreAdapter(Context context, Vector<HomeLsitModel> myDataset) {
        mContext = context;
        this.allCategoryList.clear();
        this.allSearchTag.clear();
        this.mapTag.clear();
        this.mapCategory.clear();
        this.myDataset = myDataset;
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

    public void deleteAllItemTag() {
        this.allSearchTag.clear();
        this.mapTag.clear();
        notifyItemChanged(0);

    }
    public void deleteAllItem() {
        myDataset.removeAllElements();

    }


    public int topFeedsID() {
        if (myDataset.size() > 0)
            return myDataset.get(0).getID();
        else
            return -1;
    }

    public void addHeaderCategoryList(Vector<CategoryList> _allCategoryList) {
        this.allCategoryList.clear();
        this.allCategoryList.addAll(_allCategoryList);
    }

    public void addHeaderTag(Vector<SearchTag> allSearchTag) {
        this.allSearchTag.clear();
        this.allSearchTag.addAll(allSearchTag);
    }

    public HashMap<String, String> loadCategory() {
        return mapCategory;
    }

    public HashMap<String, String> loadTag() {
        return mapTag;
    }

    public void deleteAllItems() {
        myDataset.removeAllElements();
        //notifyDataSetChanged();
    }

    public void addnewItem(HomeLsitModel dataObj) {
        int currentPos = getItemCount();
        myDataset.add(dataObj);
//        notifyItemInserted(currentPos);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_explorer, viewGroup, false);
            return new HeaderViewHolder(view);

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            final HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            if (allCategoryList.size() == 0)
                holder.layout_category.setVisibility(View.GONE);
            else
                holder.layout_category.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams param;
            holder.ll_catories.removeAllViews();
            for (int i = 0; i < allCategoryList.size(); i++) {

                CategoryList mCategoryList=allCategoryList.get(i);

                final Button button = new Button(mContext);
                button.setBackgroundColor(Color.parseColor("#DEDEDE"));
                param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(5, 5, 5, 5);
                button.setLayoutParams(param);
                button.setId(i);
                final int id_ = button.getId();
                button.setText(mCategoryList.getName());
                button.setTag(mCategoryList);

                if (mapCategory.containsKey(allCategoryList.get(i).getName())) {
                    button.setBackgroundColor(Color.parseColor("#00C5C1"));

                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CategoryList mCategoryList = (CategoryList) view.getTag();
                        //NOTE: we search 1 by 1 for now
//                        if (mapCategory.containsKey(mCategoryList.getName())) {
//                            button.setBackgroundColor(Color.parseColor("#DEDEDE"));
//                            mapCategory.remove(mCategoryList.getName());
//
//                        } else {
//                            button.setBackgroundColor(Color.parseColor("#00C5C1"));
//                            mapCategory.put(mCategoryList.getName(), mCategoryList.getID());
//                        }

                        Log.w("mCategoryList","are"+mCategoryList.getName());

                        mapTag.clear();
                        mapCategory.clear();
                        mapCategory.put(mCategoryList.getName(), mCategoryList.getID());
                        notifyItemChanged(0);
                        mAdapterCallback.onMethodCallback(1, "");

                    }
                });
                holder.ll_catories.addView(button);
            }
            ///==================
            if (allSearchTag.size() == 0)
                holder.layout_Tags.setVisibility(View.GONE);
            else
                holder.layout_Tags.setVisibility(View.VISIBLE);
            holder.ll_tag.removeAllViews();
            for (int i = 0; i < allSearchTag.size(); i++) {
                param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final Button button = new Button(mContext);
                button.setId(i);
                button.setText(allSearchTag.get(i).getTagName());
                param.setMargins(5, 5, 5, 5);
                button.setLayoutParams(param);
                // int id_ = button.getId();
                //RandomColor randomColor = new RandomColor();
                //final int color = randomColor.randomColor();
                //button.setBackgroundColor(color);
                button.setBackgroundColor(Color.parseColor("#DEDEDE"));
                button.setTag(allSearchTag.get(i));

                if (mapTag.containsKey(allSearchTag.get(i).getTagName())) {
                    button.setBackgroundColor(Color.parseColor("#00C5C1"));

                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SearchTag mSearchTag = (SearchTag) view.getTag();
                        //NOTE: we search 1 by 1 for now.
//                        if (mapTag.containsKey(mSearchTag.getTagName())) {
//                            button.setBackgroundColor(color);
//                            mapTag.remove(mSearchTag.getTagName());
//                        } else {
//                            button.setBackgroundColor(Color.parseColor("#00C5C1"));
//                            mapTag.put(mSearchTag.getTagName(), mSearchTag.getID());
//                        }
//                        if (lastTagSelect != null) {
//                            lastTagSelect.setBackgroundColor(color);
//                        }
                        mapCategory.clear();
                        mapTag.clear();
                        mapTag.put(mSearchTag.getTagName(), mSearchTag.getID());
                        notifyItemChanged(0);
                        mAdapterCallback.onMethodCallback(2, "");

                    }
                });
                holder.ll_tag.addView(button);
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

            if (mHomeLsitModel.isLiked())
                holder.like_unlike.setImageResource(R.drawable.like_active);
            else
                holder.like_unlike.setImageResource(R.drawable.like_deactive);


            holder.vote.setText("Gift");
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
            holder.ll_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Channgecustomview.commentTextView = holder.tv_comment;
                    Channgecustomview.homeLsitModel = mHomeLsitModel;
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.putExtra("Postid", mHomeLsitModel.getID());
                    intent.putExtra("PostType", mHomeLsitModel.getPostType());
                    mContext.startActivity(intent);
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
                    Intent intent = new Intent(mContext, DonateActivity.class);
                    intent.putExtra("user_Name", "" + mHomeLsitModel.getCreatedByName());
                    intent.putExtra("CreatedBy", "" + mHomeLsitModel.getCreatedBy());
                    mContext.startActivity(intent);
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
//                        intent.putExtra("Url_video", APIHandler.Instance().BuildLiveStreamVideoRecorded(mHomeLsitModel.getVideoName()));
                        intent.putExtra("Url_video", mHomeLsitModel.getVideoName());
                        intent.putExtra("is_360", !mHomeLsitModel.getVideoType().contentEquals("normal"));
                        intent.putExtra("is_Live", mHomeLsitModel.isPostStreaming());
                        intent.putExtra("postID", mHomeLsitModel.getID());
                        intent.putExtra("PostType", mHomeLsitModel.getPostType());

                        mContext.startActivity(intent);

                        int viewCOunt = Integer.parseInt(myDataset.get(position).getView());
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
        private LinearLayout layout_category;
        private LinearLayout ll_catories;
        private LinearLayout layout_Tags;
        private LinearLayout ll_tag;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            layout_category = (LinearLayout) itemView.findViewById(R.id.layout_category);
            ll_catories = (LinearLayout) itemView.findViewById(R.id.ll_catories);
            layout_Tags = (LinearLayout) itemView.findViewById(R.id.layout_Tags);
            ll_tag = (LinearLayout) itemView.findViewById(R.id.ll_tag);
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

//    public void searchExplorerByTag() {
//        String tag = "";
//        for (String key : mapTag.keySet()) {
//            if (tag == "") {
//                tag = key;
//            } else {
//                tag = tag + "," + key;
//            }
//        }
//        if (tag.length() == 0)
//            return;
//
//        mProgressDialog = new ProgressDialog(mContext);
//        mProgressDialog.setCancelable(true);
//        mProgressDialog.setMessage("Uploading...");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.show();
//        HashMap<String, String> param = new HashMap<String, String>();
//        param.put("tags", "" + tag);
//        APIHandler.Instance().GET_BY_AUTHEN("feeds/search-by-tag", param, new APIHandler.RequestComplete() {
//            @Override
//            public void onRequestComplete(final int code, final String response) {
//                Log.w("Tag ", "Date: " + response);
//                ((HomebaseActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //responseDataShow(response);
//                        mProgressDialog.dismiss();
//                    }
//                });
//
//            }
//        });
//    }

//    public void responseDataShow(final String response) {
//        try {
//            myDataset.clear();
//            final JSONObject json_ob = new JSONObject(response);
//            final JSONArray json = json_ob.getJSONArray("msg");
//            for (int index = 0; index < json.length(); index++) {
//
//                HomeLsitModel model = new HomeLsitModel();
//                JSONObject mObject = json.getJSONObject(index);
//                model.setCreatedAt(mObject.getString("CreatedAt"));
//                model.setUpdatedAt(mObject.getString("UpdatedAt"));
//                model.setPublish(mObject.getString("Publish"));
//                model.setCreatedBy(mObject.getString("CreatedBy"));
//                model.setCreatedByName(mObject.getString("CreatedByName"));
//                model.setDeviceID(mObject.getString("DeviceID"));
//                model.setPostName(mObject.getString("PostName"));
//                model.setPostDescription(mObject.getString("PostDescription"));
//                model.setPostThumbUrl(mObject.getString("PostThumbUrl"));
//                model.setView(mObject.getString("View"));
//                model.setCreatedByAvatar(mObject.getString("CreatedByAvatar"));
//                model.setLike(mObject.getString("Like"));
//                model.setComment(mObject.getString("Comment"));
//                model.setRate(mObject.getString("Rate"));
//                model.setRateValue(mObject.getString("RateValue"));
//                model.setPostType(mObject.getString("PostType"));
//                model.setLiveStreamApp(mObject.getString("LiveStreamApp"));
//                model.setLiveStreamName(mObject.getString("LiveStreamName"));
//                model.setPostStreaming(mObject.getBoolean("IsPostStreaming"));
//                model.setVideoType(mObject.getString("VideoType"));
//                model.setVideoName(mObject.getString("VideoName"));
//
//                String PostType = mObject.getString("PostType");
//                model.setLiked(mObject.getBoolean("Liked"));
//
//                model.setPostType(PostType);
//
//                if (PostType.contains("ads")) {
//                    model.setFlagAdd(true);
//                    model.setID(mObject.getInt("AdsID"));
//                } else {
//                    model.setID(mObject.getInt("ID"));
//                    model.setFlagAdd(false);
//                }
//                myDataset.add(model);
//            }
//
//        } catch (Exception ex) {
//
//        }
//        notifyDataSetChanged();
//    }


}

