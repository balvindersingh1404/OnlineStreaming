package com.headsupseven.corp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.headsupseven.corp.adapter.CategoryAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.model.Categoryaddvideo;
import com.headsupseven.corp.utils.ImageFilePath;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddvideoActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private LinearLayout btnStartLive;
    private TextView txtVideoName, txtVideoDesc;
    private CheckBox checkbox_live;
    private CheckBox checkbox_degree;
    private CheckBox checkbox_Gallery;
    private static final int GALLERY = 303;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Spinner category_spinner;
    private Spinner post_spinner;
    private Spinner spinner_Contact;
    private CategoryAdapter mCategoryAdapter;
    private ArrayList<Categoryaddvideo> categorySpinnerArray = new ArrayList<>();

    private int selection = 0;
    private RelativeLayout layout_spinner_Contact;
    private int mPostType = 0;
    private String mVideoType = "normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvideo);
        mContext = this;
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        selection = 0;
        categorySpinnerArray.clear();
        setPermission();
        initUI();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication.uploadDataFile = false;
    }

    private void initUI() {

        if (APIHandler.Instance().user.account_type.contains("regular")) {
            showsureaydialog();
            return;
        }

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddvideoActivity.this.finish();
                MyApplication.uploadDataFile = false;

            }
        });
        layout_spinner_Contact = (RelativeLayout) this.findViewById(R.id.layout_spinner_Contact);
        layout_spinner_Contact.setVisibility(View.GONE);

        category_spinner = (Spinner) this.findViewById(R.id.category_spinner);
        post_spinner = (Spinner) this.findViewById(R.id.post_spinner);
        spinner_Contact = (Spinner) this.findViewById(R.id.spinner_Contact);
        spinner_Contact.setVisibility(View.GONE);

        //for category spinner


        mCategoryAdapter = new CategoryAdapter(this, R.layout.spinner_item, R.id.item_name, categorySpinnerArray);
//        categorySpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorySpinnerArray); //selected item will look like a spinner set from XML
//        categorySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(mCategoryAdapter);
        mCategoryAdapter.notifyDataSetChanged();

        //for Post type spinner

        ArrayList<String> postTypeSpinnerArray = new ArrayList<String>();
        postTypeSpinnerArray.add("Live Streaming");
        postTypeSpinnerArray.add("Gallery");


        ArrayAdapter<String> postTypeSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, postTypeSpinnerArray); //selected item will look like a spinner set from XML
        postTypeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        post_spinner.setAdapter(postTypeSpinnerArrayAdapter);


        ArrayList<String> contentTypeSpinnerArray = new ArrayList<String>();
        contentTypeSpinnerArray.add("Contact & payment details");

        ArrayAdapter<String> contentTypeSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contentTypeSpinnerArray); //selected item will look like a spinner set from XML
        contentTypeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Contact.setAdapter(contentTypeSpinnerArrayAdapter);


        txtVideoName = (TextView) this.findViewById(R.id.txt_video_title_add_video);
        txtVideoDesc = (TextView) this.findViewById(R.id.txt_video_description_add_video);

        btnStartLive = (LinearLayout) this.findViewById(R.id.btn_start_live_add_video);
        btnStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPermission();
                if (permissions.size() == 0) {
                    MyApplication.uploadDataFile = true;
                    String videoType = post_spinner.getSelectedItem().toString();
                    if (videoType.equalsIgnoreCase("Gallery")) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("video/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
                        startActivityForResult(intent, GALLERY);
                    } else {
                        HashMap<String, String> authenPostData = new HashMap<String, String>();
                        authenPostData.put("PostName", txtVideoName.getText().toString());
                        authenPostData.put("PostDescription", txtVideoDesc.getText().toString());
                        authenPostData.put("VideoType", mVideoType);

                        Categoryaddvideo mCategoryaddvideo = categorySpinnerArray.get(category_spinner.getSelectedItemPosition());
                        if (mCategoryaddvideo.isFlaABoolean()) {
                            authenPostData.put("PostType", "event-upload");
                            String Category_ID = mCategoryaddvideo.getID();
                            Log.w("AdditionData", "are" + Category_ID);
                            authenPostData.put("AdditionData", Category_ID);
                        } else {
                            authenPostData.put("PostType", "live");
                            String Category_ID = mCategoryaddvideo.getID();
                            authenPostData.put("Category", Category_ID);
                        }


                        APIHandler.Instance().POST_BY_AUTHEN("feeds//add-video-feed", authenPostData, new APIHandler.RequestComplete() {
                            @Override
                            public void onRequestComplete(int code, String response) {
                                if (code == 200 && response.length() > 0) {
                                    try {
                                        JSONObject reader = new JSONObject(response);
                                        int resultCode = reader.getInt("code");
                                        if (resultCode == 1) {
                                            Intent it = new Intent(mContext, LiveStreamBroadcasterActivity.class);
                                            String url = APIHandler.Instance().BuildLiveStreamUrl();
                                            it.putExtra("URL", url);
                                            startActivity(it);
                                            AddvideoActivity.this.finish();
                                        } else {

                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        });
                    }
                }


            }
        });
        checkbox_Gallery = (CheckBox) this.findViewById(R.id.checkbox_Gallery);
        checkbox_degree = (CheckBox) this.findViewById(R.id.checkbox_degree);
        checkbox_live = (CheckBox) this.findViewById(R.id.checkbox_live);
        checkbox_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionVideoOption(0);

            }
        });
        checkbox_degree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionVideoOption(1);

            }
        });
        checkbox_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionVideoOption(2);
            }
        });
        APIHandler.Instance().GET_BY_AUTHEN("category", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (response.length() > 0) {
                    AddvideoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {
                                    JSONArray msg = mJsonObject.getJSONArray("msg");
                                    for (int index = 0; index < msg.length(); index++) {
                                        JSONObject mObject = msg.getJSONObject(index);
                                        Categoryaddvideo mCategoryaddvideo = new Categoryaddvideo();
                                        mCategoryaddvideo.setName(mObject.getString("Name"));
                                        mCategoryaddvideo.setID(mObject.getString("ID"));
                                        mCategoryaddvideo.setFlaABoolean(false);
                                        categorySpinnerArray.add(mCategoryaddvideo);


                                    }
                                }
                                mCategoryAdapter.notifyDataSetChanged();

                                addListContest();

                            } catch (Exception e) {
                                PopupAPI.make(mContext, "Error", "Can't connect to server");

                            }

                        }
                    });

                }

            }
        });

        selectionVideoOption(0);

    }

    public void addListContest() {
        APIHandler.Instance().GET_BY_AUTHEN("contest/joined-events", new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("response", "are" + response);
                if (response.length() > 0) {
                    AddvideoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {
                                    JSONArray msg = mJsonObject.getJSONArray("msg");
                                    for (int index = 0; index < msg.length(); index++) {
                                        JSONObject mObject = msg.getJSONObject(index);
                                        Categoryaddvideo mCategoryaddvideo = new Categoryaddvideo();
                                        mCategoryaddvideo.setName(mObject.getString("ContestShortName"));
                                        mCategoryaddvideo.setID(mObject.getString("ContestID"));
                                        mCategoryaddvideo.setFlaABoolean(true);
                                        boolean ContestActive = mObject.getBoolean("ContestActive");
                                        boolean ContestUploaded = mObject.getBoolean("ContestUploaded");
                                        if (ContestActive && !ContestUploaded) {
                                            categorySpinnerArray.add(mCategoryaddvideo);

                                        }

                                    }
                                }

                                mCategoryAdapter.notifyDataSetChanged();


                            } catch (Exception e) {
                                PopupAPI.make(mContext, "Error", "Can't connect to server");

                            }

                        }
                    });

                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                if (selectedImagePath != null || !selectedImagePath.equalsIgnoreCase("")) {
                    MyApplication.uploadDataFile = true;
                    uploadVideo(selectedImagePath);
                }

            }
        }
    }

    private ProgressDialog mProgressDialog;

    public void uploadVideo(String selectedImagePath) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();


        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("PostName", txtVideoName.getText().toString());
        authenPostData.put("PostDescription", txtVideoDesc.getText().toString());
        authenPostData.put("VideoType", mVideoType);

        Categoryaddvideo mCategoryaddvideo = categorySpinnerArray.get(category_spinner.getSelectedItemPosition());
        Log.w("mCategoryaddvideo", "are" + mCategoryaddvideo.getName());
        Log.w("isFlaABoolean", "are" + mCategoryaddvideo.isFlaABoolean());

        if (mCategoryaddvideo.isFlaABoolean()) {
            authenPostData.put("PostType", "event-upload");
            String Category_ID = mCategoryaddvideo.getID();
            Log.w("AdditionData", "are" + Category_ID);
            authenPostData.put("AdditionData", Category_ID);
        } else {
            authenPostData.put("PostType", "live");
            String Category_ID = mCategoryaddvideo.getID();
            authenPostData.put("Category", Category_ID);
        }

        HashMap<String, String> filePostData = new HashMap<String, String>();
        filePostData.put("videofile", selectedImagePath);
        APIHandler.Instance().UPLOAD_BY_AUTHEN("feeds//add-recorded", authenPostData, filePostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, final String response) {
                AddvideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are" + response);
                        MyApplication.uploadDataFile = false;
                        mProgressDialog.dismiss();
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codeServer = mJsonObject.getInt("code");
                            String msg = mJsonObject.getString("msg");
                            PopupAPI.showToast(mContext, msg);
                            finish();

                        } catch (Exception e) {
                            PopupAPI.showToast(mContext, e.getMessage());

                        }
                    }
                });
            }
        });

    }

    public void selectionVideoOption(int pos) {
        selection = pos;
        checkbox_Gallery.setChecked(false);
        checkbox_degree.setChecked(false);
        checkbox_live.setChecked(false);
        if (pos == 0) {
            mPostType = 0;
            checkbox_live.setChecked(true);
            mVideoType = "normal";
        } else if (pos == 1) {
            mPostType = 0;
            checkbox_degree.setChecked(true);
            mVideoType = "360";
        } else if (pos == 2) {
            mPostType = 1;
            checkbox_Gallery.setChecked(true);
            mVideoType = "vr";
        }
    }

    List<String> permissions = new ArrayList<String>();

    public void setPermission() {
        permissions.clear();
        if (Build.VERSION.SDK_INT > 22) {
            String readstrogePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
            String strogePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String recordPermission = android.Manifest.permission.RECORD_AUDIO;
            String cameraPermission = android.Manifest.permission.CAMERA;
            int hasRecordPermission = checkSelfPermission(recordPermission);
            int hasCameraPermission = checkSelfPermission(cameraPermission);


            int hasReadStrogePermission = checkSelfPermission(readstrogePermission);
            int hasStrogePermission = checkSelfPermission(strogePermission);

            if (hasReadStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readstrogePermission);
            }

            if (hasStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(strogePermission);
            }
            if (hasRecordPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(recordPermission);
            }

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(cameraPermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    public void showsureaydialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_viodeupload);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ImageView cancelDialog = (ImageView) dialog.findViewById(R.id.cross_btn);
        TextView tv_Start = (TextView) dialog.findViewById(R.id.tv_Start);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        tv_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Handle permission granted
                } else {
                    // Handle permission denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
