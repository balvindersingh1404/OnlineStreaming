package com.headsupseven.corp.chatdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.github.library.bubbleview.BubbleImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.headsupseven.corp.FullScreenImaveViewActivity;
import com.headsupseven.corp.FullScreenVideoViewActivity;
import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Droidroid on 2016/4/25.
 */
public class ChatAdapterNew extends BaseAdapter {

    private Context mContext;
    private List<ChatBean> mData;
    private AQuery androidQuery;
    private Target mTarget;

    public ChatAdapterNew(Context context, List<ChatBean> data) {
        mContext = context;
        this.androidQuery = new AQuery(mContext);
        mData = data;
    }

    public void refreshList(List<ChatBean> items) {
        this.mData = items;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ChatBean getItem(int position) {
        try {
            return mData.get(position);
        } catch (Exception ex) {
            return mData.get(0);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolderLeftText {
        TextView timeTV;
        ImageView leftAvatar;
        TextView leftNickname;
        TextView leftText;
    }

    class ViewHolderLeftImage {
        TextView timeTV;
        ImageView leftAvatar;
        TextView leftNickname;
        BubbleImageView leftPhoto;
    }

    class ViewHolderLeftVideo {
        TextView timeTV;
        ImageView leftAvatar;
        TextView rightNickname;
        BubbleImageView leftVideoPhoto;
    }


    class ViewHolderRightText {
        TextView timeTV;
        ImageView rightAvatar;
        TextView rightNickname;
        TextView rightText;
    }

    class ViewHolderRightImage {
        TextView timeTV;
        ImageView rightAvatar;
        TextView rightNickname;
        BubbleImageView rightPhoto;
        TextView photoProgress;
        ProgressBar progressBar_chat_image_right;
    }

    class ViewHolderRightVideo {
        TextView timeTV;
        ImageView rightAvatar;
        TextView leftNickname;
        BubbleImageView rightVideoPhoto;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatBean item = getItem(position);
        ViewHolderRightText holderRightText = null;
        ViewHolderRightImage holderRightImg = null;
        ViewHolderRightVideo holderRightVideo = null;

        ViewHolderLeftText holderLeftText = null;
        ViewHolderLeftImage holderLeftImg = null;
        ViewHolderLeftVideo holderLeftVideo = null;

        try {
            if (convertView == null) {
                switch (item.type) {
                    case ChatBean.LEFT_TEXT:
                        holderLeftText = new ViewHolderLeftText();
                        convertView = View.inflate(mContext, R.layout.item_text_left, null);
                        holderLeftText.leftText = (TextView) convertView.findViewById(R.id.tv_text_left);
                        holderLeftText.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                        displayLeftText(item, holderLeftText);
                        convertView.setTag(holderLeftText);


                        break;
                    case ChatBean.LEFT_IMAGE:
                        holderLeftImg = new ViewHolderLeftImage();
                        convertView = View.inflate(mContext, R.layout.item_image_left, null);
                        holderLeftImg.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                        holderLeftImg.leftPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_left);
                        displayLeftImage(item, holderLeftImg);

                        convertView.setTag(holderLeftImg);
                        break;

                    case ChatBean.LEFT_VIDEO:

                        holderLeftVideo = new ViewHolderLeftVideo();
                        convertView = View.inflate(mContext, R.layout.item_video_left, null);
                        holderLeftVideo.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                        holderLeftVideo.leftVideoPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_left_video);

                        displayLeftVideo(item, holderLeftVideo);
                        convertView.setTag(holderLeftImg);

                        break;

                    case ChatBean.RIGHT_TEXT:

                        holderRightText = new ViewHolderRightText();
                        convertView = View.inflate(mContext, R.layout.item_text_right, null);
                        holderRightText.rightText = (TextView) convertView.findViewById(R.id.tv_text_right);
                        holderRightText.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                        displayRightText(item, holderRightText);
                        convertView.setTag(holderLeftImg);


                        break;

                    case ChatBean.RIGHT_IMAGE:

                        holderRightImg = new ViewHolderRightImage();
                        convertView = View.inflate(mContext, R.layout.item_image_right, null);
                        holderRightImg.rightPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_right);
                        holderRightImg.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile_right);
                        holderRightImg.progressBar_chat_image_right = (ProgressBar) convertView.findViewById(R.id.progressBar_chat_image_right);


                        displayRightImage(item, holderRightImg);

                        convertView.setTag(holderRightImg);


                        break;

                    case ChatBean.RIGHT_VIDEO:

                        holderRightVideo = new ViewHolderRightVideo();
                        convertView = View.inflate(mContext, R.layout.item_video_right, null);
                        holderRightVideo.rightVideoPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_right_video);
                        holderRightVideo.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                        displayRightVideo(item, holderRightVideo);
                        convertView.setTag(holderRightVideo);

                        break;
                }
            } else {
                switch (item.type) {
                    case ChatBean.LEFT_TEXT:
                        if (convertView.getTag() instanceof ViewHolderLeftText) {
                            holderLeftText = (ViewHolderLeftText) convertView
                                    .getTag();
                            displayLeftText(item, holderLeftText);

                        } else {

                            holderLeftText = new ViewHolderLeftText();
                            convertView = View.inflate(mContext, R.layout.item_text_left, null);
                            holderLeftText.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);

                            holderLeftText.leftText = (TextView) convertView.findViewById(R.id.tv_text_left);
                            displayLeftText(item, holderLeftText);
                            convertView.setTag(holderLeftText);


                        }

                        break;
                    case ChatBean.LEFT_IMAGE:
                        if (convertView.getTag() instanceof ViewHolderLeftImage) {
                            holderLeftImg = (ViewHolderLeftImage) convertView
                                    .getTag();
                            displayLeftImage(item, holderLeftImg);

                        } else {

                            holderLeftImg = new ViewHolderLeftImage();
                            convertView = View.inflate(mContext, R.layout.item_image_left, null);
                            holderLeftImg.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);

                            holderLeftImg.leftPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_left);
                            convertView.setTag(holderLeftImg);
                            displayLeftImage(item, holderLeftImg);


                        }

                        break;

                    case ChatBean.LEFT_VIDEO:
                        if (convertView.getTag() instanceof ViewHolderLeftVideo) {
                            holderLeftVideo = (ViewHolderLeftVideo) convertView.getTag();
                            displayLeftVideo(item, holderLeftVideo);

                        } else {

                            holderLeftVideo = new ViewHolderLeftVideo();
                            convertView = View.inflate(mContext, R.layout.item_video_left, null);
                            holderLeftVideo.leftAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);
                            holderLeftVideo.leftVideoPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_left_video);
                            displayLeftVideo(item, holderLeftVideo);

                            convertView.setTag(holderLeftImg);


                        }

                        break;

                    case ChatBean.RIGHT_TEXT:
                        if (convertView.getTag() instanceof ViewHolderRightText) {
                            holderRightText = (ViewHolderRightText) convertView.getTag();
                            displayRightText(item, holderRightText);
                        } else {

                            holderRightText = new ViewHolderRightText();
                            convertView = View.inflate(mContext, R.layout.item_text_right, null);
                            holderRightText.rightText = (TextView) convertView.findViewById(R.id.tv_text_right);
                            holderRightText.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);

                            displayRightText(item, holderRightText);

                            convertView.setTag(holderLeftImg);


                        }

                        break;

                    case ChatBean.RIGHT_IMAGE:
                        if (convertView.getTag() instanceof ViewHolderRightImage) {
                            holderRightImg = (ViewHolderRightImage) convertView.getTag();
                            displayRightImage(item, holderRightImg);

                        } else {

                            holderRightImg = new ViewHolderRightImage();
                            convertView = View.inflate(mContext, R.layout.item_image_right, null);
                            holderRightImg.rightPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_right);
                            holderRightImg.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile_right);
                            holderRightImg.progressBar_chat_image_right = (ProgressBar) convertView.findViewById(R.id.progressBar_chat_image_right);

                            displayRightImage(item, holderRightImg);
                            convertView.setTag(holderRightImg);


                        }

                        break;

                    case ChatBean.RIGHT_VIDEO:
                        if (convertView.getTag() instanceof ViewHolderRightVideo) {
                            holderRightVideo = (ViewHolderRightVideo) convertView.getTag();
                            displayRightVideo(item, holderRightVideo);

                        } else {

                            holderRightVideo = new ViewHolderRightVideo();
                            convertView = View.inflate(mContext, R.layout.item_video_right, null);
                            holderRightVideo.rightVideoPhoto = (BubbleImageView) convertView.findViewById(R.id.chat_image_right_video);
                            holderRightVideo.rightAvatar = (RoundedImageView) convertView.findViewById(R.id.img_profile);

                            displayRightVideo(item, holderRightVideo);
                            convertView.setTag(holderRightVideo);
                        }

                        break;
                }


            }


//            }
        } catch (Exception ex) {

        }
        return convertView;
    }

    /**
     * thos content are righ side user  for text ,image & video
     *
     * @param mChatBean
     * @param mViewHolderRightText
     */
    private void displayRightText(ChatBean mChatBean, ViewHolderRightText mViewHolderRightText) {
        mViewHolderRightText.rightText.setText(mChatBean.text);
        if (!mChatBean.UserIcon.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(mViewHolderRightText.rightAvatar);
        }
    }

    //check local data ,
    private void displayRightImage(final ChatBean mChatBean, final ViewHolderRightImage mImageView) {

        mImageView.progressBar_chat_image_right.setVisibility(View.GONE);

        if (mChatBean.isLocalFile()) {
            File mFile = new File(mChatBean.text);
            if (mFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                mImageView.rightPhoto.setImageBitmap(myBitmap);
            }
        } else {
            File mFile = createfilefordata(mChatBean.text.toString());
            if (mFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                mImageView.rightPhoto.setImageBitmap(myBitmap);
            } else {
                String uri = APIHandler.ChatURL + "pub/" + mChatBean.text;

                mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        //Do somethin
                        mImageView.progressBar_chat_image_right.setVisibility(View.GONE);
                        mImageView.rightPhoto.setImageBitmap(bitmap);
                        saveImageFile(bitmap, createfilefordata(mChatBean.text));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mImageView.rightPhoto.setImageResource(R.drawable.gallery_change);
                        mImageView.progressBar_chat_image_right.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        mImageView.rightPhoto.setImageResource(R.drawable.gallery_change);


                    }
                };
                Picasso.with(mContext)
                        .load(uri)
                        .into(mTarget);
            }
        }
        mImageView.rightPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(mContext, FullScreenImaveViewActivity.class);
                mm.putExtra("imagePath", mChatBean.text);
                mm.putExtra("local", mChatBean.isLocalFile());
                mContext.startActivity(mm);
            }
        });
        if (!mChatBean.UserIcon.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(mImageView.rightAvatar);
        }

    }

    private void displayRightVideo(final ChatBean mChatBean, ViewHolderRightVideo mViewHolderRightVideo) {
        if (mChatBean.isLocalFile()) {
            File mFile = new File(mChatBean.text);
            if (mFile.exists()) {
                try {
                    Bitmap bitmap = retriveVideoFrameFromVideo(mChatBean.text);
                    mViewHolderRightVideo.rightVideoPhoto.setImageBitmap(bitmap);

                } catch (Exception ex) {

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

        } else {

            File mFile = createfilefordata(mChatBean.text.toString());
            if (mFile.exists()) {
                try {
                    Bitmap bitmap = retriveVideoFrameFromVideo(mFile.getAbsolutePath());
                    mViewHolderRightVideo.rightVideoPhoto.setImageBitmap(bitmap);

                } catch (Exception ex) {
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                String uri = APIHandler.ChatURL + "pub/" + mChatBean.text;
                downloadfile(uri, mFile);
            }
        }
        mViewHolderRightVideo.rightVideoPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(mContext, FullScreenVideoViewActivity.class);
                mm.putExtra("videoUri", mChatBean.text);
                mm.putExtra("local", mChatBean.isLocalFile());
                mContext.startActivity(mm);
            }
        });
        if (!mChatBean.UserIcon.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(mViewHolderRightVideo.rightAvatar);

        }
    }


    /***
     * this is for left text,image,video
     * who chat on by other user
     *
     * @param mChatBean
     * @param holderLeftText
     */
    private void displayLeftText(ChatBean mChatBean, ViewHolderLeftText holderLeftText) {

        holderLeftText.leftText.setText(mChatBean.text);
        if (!mChatBean.UserIcon.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(holderLeftText.leftAvatar);
        }
    }

    private void displayLeftImage(final ChatBean mChatBean, final ViewHolderLeftImage mViewHolderLeftImage) {


        File mFile = createfilefordata(mChatBean.text.toString());
        if (mFile.exists()) {
            Log.w("sds", "ae" + mFile.getAbsolutePath());
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                if (myBitmap != null)
                    mViewHolderLeftImage.leftPhoto.setImageBitmap(myBitmap);
                else
                    mViewHolderLeftImage.leftPhoto.setImageResource(R.drawable.gallery_change);

            } catch (Exception ex) {
                mViewHolderLeftImage.leftPhoto.setImageResource(R.drawable.gallery_change);

            }

        } else {
            Log.w("mChatBean", "serfvdfgdgml" + mChatBean.text);

            String uri = APIHandler.ChatURL + "pub/" + mChatBean.text;
            mTarget = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    //Do somethin
                    // mViewHolderLeftImage.progressBar_chat_image_right.setVisibility(View.GONE);
                    mViewHolderLeftImage.leftPhoto.setImageBitmap(bitmap);
                    saveImageFile(bitmap, createfilefordata(mChatBean.text));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    mViewHolderLeftImage.leftPhoto.setImageResource(R.drawable.gallery_change);
                    // mViewHolderLeftImage.progressBar_chat_image_right.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    mViewHolderLeftImage.leftPhoto.setImageResource(R.drawable.gallery_change);


                }
            };
            Picasso.with(mContext)
                    .load(uri)
                    .into(mTarget);
        }
        mViewHolderLeftImage.leftPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(mContext, FullScreenImaveViewActivity.class);
                mm.putExtra("imagePath", mChatBean.text);
                mm.putExtra("local", mChatBean.isLocalFile());
                mContext.startActivity(mm);
            }
        });
        if (!mChatBean.UserIcon.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(mViewHolderLeftImage.leftAvatar);
        }

    }

    private void displayLeftVideo(final ChatBean mChatBean, ViewHolderLeftVideo mViewHolderLeftVideo) {


        if (mChatBean.isLocalFile()) {
            File mFile = new File(mChatBean.text);
            if (mFile.exists()) {
                try {
                    Bitmap bitmap = retriveVideoFrameFromVideo(mChatBean.text);
                    mViewHolderLeftVideo.leftVideoPhoto.setImageBitmap(bitmap);

                } catch (Exception ex) {

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

        } else {

            File mFile = createfilefordata(mChatBean.text.toString());
            if (mFile.exists()) {
                try {
                    Bitmap bitmap = retriveVideoFrameFromVideo(mFile.getAbsolutePath());
                    mViewHolderLeftVideo.leftVideoPhoto.setImageBitmap(bitmap);

                } catch (Exception ex) {
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                String uri = APIHandler.ChatURL + "pub/" + mChatBean.text;
                downloadfile(uri, mFile);
            }
        }
        mViewHolderLeftVideo.leftVideoPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mm = new Intent(mContext, FullScreenVideoViewActivity.class);
                mm.putExtra("videoUri", mChatBean.text);
                mm.putExtra("local", mChatBean.isLocalFile());
                mContext.startActivity(mm);
            }
        });
        if (!mChatBean.text.equalsIgnoreCase("")) {
            Picasso.with(mContext)
                    .load(mChatBean.UserIcon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.user_avater)
                    .into(mViewHolderLeftVideo.leftAvatar);

        }

    }

    /**
     * Download vidoe from server & save into sd card
     *
     * @param url
     * @param mFile
     */
    public void downloadfile(String url, File mFile) {
        APIHandler.Instance().Downloadfilefromserver(url, mFile.getAbsolutePath(), new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, String response) {
                try {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });

                } catch (Exception ex) {

                }
            }
        });
    }

    /**
     * retunrn Bitmap from video on sd card
     *
     * @param videoPath
     * @return
     * @throws Throwable
     */
    public Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    public void saveImageFile(Bitmap bitmap, File mFile) {
        try {
            FileOutputStream out = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File createfilefordata(String nameOffile) {
        File file = createBaseDirctory();
        File saveFile = new File(file, nameOffile);
        return saveFile;
    }

    public File createBaseDirctory() {
        final String extStorageDirectory = Environment
                .getExternalStorageDirectory().toString();
        File directory = new File(extStorageDirectory + "/"
                + mContext.getString(R.string.app_name) + "/pub");
        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("Directory already created");
        }
        return directory;
    }

}
