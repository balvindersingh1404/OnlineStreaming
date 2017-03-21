package com.headsupseven.corp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.ConutryListparser;
import com.headsupseven.corp.utils.CacheFile;
import com.headsupseven.corp.utils.ImageFilePath;
import com.headsupseven.corp.utils.PopupAPI;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by tmmac on 1/30/17.
 */

public class EditProfileActivity extends AppCompatActivity {
    private LinearLayout back;
    Spinner spCountry;
    TextView tv_save;
    private static final int GALLERY_avatar = 303;
    private static final int GALLERY_Banner = 503;
    private AQuery androidQuery;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Context mContext;
    private RoundedImageView image_avatar;
    private ImageView imageview_banner;
    private EditText edt_fullname, edt_email, edt_address;
    int widthPixels = 0;
    private EditText edt_baio;
    public ArrayList<ConutryListparser> mArrayList = new ArrayList<ConutryListparser>();
    public ArrayList<String> mCountryList = new ArrayList<String>();
    private Spinner country_spinner;
    private EditText zip_code;
    private EditText sstate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;
        androidQuery = new AQuery(mContext);
        widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;

        initUI();
    }

    public void initUI() {
        country_spinner = (Spinner) this.findViewById(R.id.country_spinner);
        edt_baio = (EditText) this.findViewById(R.id.edt_baio);
        imageview_banner = (ImageView) this.findViewById(R.id.imageview_banner);
        back = (LinearLayout) this.findViewById(R.id.ll_silding);
        spCountry = (Spinner) this.findViewById(R.id.country_spinner);
        sstate = (EditText) this.findViewById(R.id.sstate);
        zip_code = (EditText) this.findViewById(R.id.zip_code);
        edt_fullname = (EditText) this.findViewById(R.id.edt_fullname);
        edt_email = (EditText) this.findViewById(R.id.edt_email);
        edt_address = (EditText) this.findViewById(R.id.edt_address);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(1111, intent);
                finish();

            }
        });
        tv_save = (TextView) this.findViewById(R.id.tv_save);
        tv_save.setEnabled(false);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                velidation();
            }
        });
        image_avatar = (RoundedImageView) this.findViewById(R.id.image_avatar);
        image_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent2 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent2, GALLERY_avatar);
            }
        });
        imageview_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent2 = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent2, GALLERY_Banner);
            }
        });

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                showInformationIntoUi(response);
            }
        });
    }
    public void showInformationIntoUi(final String response) {
        mProgressDialog.dismiss();
        EditProfileActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.w("response","are"+response);

                    JSONObject mJsonObject = new JSONObject(response);
                    int code = mJsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject msg = mJsonObject.getJSONObject("msg");
                        String Follower = msg.getString("Follower");
                        String Following = msg.getString("Following");
                        String PostCount = msg.getString("PostCount");
                        JSONObject userObject = msg.getJSONObject("UserModel");
                        int ID = userObject.getInt("ID");
                        String CreatedAt = userObject.getString("CreatedAt");
                        String UpdatedAt = userObject.getString("UpdatedAt");
                        String UserName = userObject.getString("UserName");
                        String FullName = userObject.getString("FullName");
                        String Email = userObject.getString("Email");
                        String AccountType = userObject.getString("AccountType");
                        String AvatarUrl = userObject.getString("AvatarUrl");
                        String BannerUrl = userObject.getString("BannerUrl");
                        String ShortBio = userObject.getString("ShortBio");

                        String Address = userObject.getString("Address");
                        String Country = userObject.getString("Country");
                        String ZipCode = userObject.getString("ZipCode");
                        String State = userObject.getString("State");

                        edt_baio.setText(ShortBio);
                        edt_fullname.setText("" + FullName);
                        edt_email.setText("" + Email);
                        edt_address.setText(""+Address);
                        sstate.setText(""+State);
                        zip_code.setText(""+ZipCode);
                        spCountry.setTag(""+Country);

                        showMobileData();


                        Picasso.with(mContext)
                                .load(AvatarUrl)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .error(R.drawable.user_avater)
                                .into(image_avatar);


                        androidQuery.id(imageview_banner).image(BannerUrl, true, true, 0, 0, new BitmapAjaxCallback() {
                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                iv.setImageBitmap(bm);
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.w("exception", ex.toString());
                }
            }
        });
    }

    private void velidation() {
        String ShortBio = edt_baio.getText().toString().trim();
        String Address = edt_address.getText().toString().trim();
        String Country = spCountry.getSelectedItem().toString().trim();
        String ZipCode = zip_code.getText().toString().trim();
        String State = sstate.getText().toString().trim();
        String FullName = edt_fullname.getText().toString().trim();
        String Email = edt_email.getText().toString().trim();
        if (ShortBio.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter bio ");
        } else if (Address.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter address ");
        } else if (ZipCode.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter zipCode ");
        } else if (State.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter state ");
        } else if (FullName.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter fullname ");
        } else if (FullName.equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please enter email ");
        } else {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();

            HashMap<String, String> param = new HashMap<String, String>();
            param.put("ShortBio", ShortBio);
            param.put("Address", Address);
            param.put("Country", Country);
            param.put("ZipCode", ZipCode);
            param.put("State", State);
            param.put("FullName", FullName);
            param.put("Email", Email);
            APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/update-details", param, new APIHandler.RequestComplete() {
                @Override
                public void onRequestComplete(final int code, final String response) {
                    EditProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            try {

                                JSONObject mJsonObject = new JSONObject(response);
                                String msg = mJsonObject.getString("msg");
                                int code = mJsonObject.getInt("code");
                                if (code == 1) {
                                    PopupAPI.make(mContext, "Success", msg);
                                    myProfiledetails();
                                } else {
                                    PopupAPI.make(mContext, "Fail", msg);

                                }
                            } catch (Exception ex) {
                                PopupAPI.make(mContext, "Error", ex.getMessage());
                            }
                        }
                    });

                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_avatar) {
                Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                    Log.w("selectedImagePath", "are: " + selectedImagePath);
                    File mFile = new File(selectedImagePath);
                    androidQuery.id(image_avatar).image(mFile, 100);
                    HashMap<String, String> paramFile = new HashMap<String, String>();
                    paramFile.put("Avatar", mFile.getAbsolutePath());
                    String query = "user/app/" + APIHandler.Instance().user.userID + "/change-avatar";
                    uploadcall(query, paramFile);
                }

            } else if (requestCode == GALLERY_Banner) {
                Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    String[] columns = {MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.MIME_TYPE};
                    Cursor cursor = getContentResolver().query(selectedUri, columns, null, null, null);
                    cursor.moveToFirst();
                    int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);
                    String mimeType = cursor.getString(mimeTypeColumnIndex);
                    String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                    cursor.close();
                    File mFile = new File(selectedImagePath);
                    androidQuery.id(imageview_banner).image(mFile, widthPixels);

                    HashMap<String, String> paramFile = new HashMap<String, String>();
                    paramFile.put("Banner", mFile.getAbsolutePath());
                    String query = "user/app/" + APIHandler.Instance().user.userID + "/change-banner";
                    uploadcall(query, paramFile);
                }


            }
        }
    }

    private ProgressDialog mProgressDialog;

    public void uploadcall(String query, HashMap<String, String> paramFile) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().UPLOAD_BY_AUTHEN(query, param, paramFile, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                EditProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are: " + response);
                        mProgressDialog.dismiss();
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            String msg = mJsonObject.getString("msg");
                            int code = mJsonObject.getInt("code");
                            if (code == 1) {
                                PopupAPI.make(mContext, "Success", msg);
                                myProfiledetails();
                            } else {
                                PopupAPI.make(mContext, "Fail", msg);

                            }
                        } catch (Exception ex) {
                            PopupAPI.make(mContext, "Error", ex.getMessage());
                        }
                    }
                });

            }

        });
    }

    public void showMobileData() {
        try {
            InputStream input = getAssets().open("country.txt");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String readtext = new String(buffer);
            add(readtext);
        } catch (Exception e) {
            // TODO: handle exception
        }
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setArrayAdapter();
                    }
                });
            }
        }, 2000);

    }

    public void setArrayAdapter() {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mCountryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_spinner.setAdapter(dataAdapter);
        country_spinner.setSelection(getConutryListparser(country_spinner.getTag().toString()));
        tv_save.setEnabled(true);

    }

    public void add(String json) {
        try {
            ConutryListparser mConutryListparser = null;
            JSONObject mJsonObject = new JSONObject(json);
            int success = mJsonObject.getInt("success");

            if (success == 1) {
                JSONArray results = mJsonObject.getJSONArray("results");

                for (int index = 0; index < results.length(); index++) {
                    JSONObject resultsArray = results.getJSONObject(index);
                    mConutryListparser = new ConutryListparser();
                    mConutryListparser.setId("" + resultsArray.getString("id"));
                    mConutryListparser.setCountryFlag(""
                            + resultsArray.getString("countryFlag"));
                    mConutryListparser.setCountryCode(""
                            + resultsArray.getString("countryCode"));
                    mConutryListparser.setCountryName(""
                            + resultsArray.getString("countryName"));
                    mConutryListparser.setShortName(""
                            + resultsArray.getString("shortName"));
                    mArrayList.add(mConutryListparser);
                    mCountryList.add("" + resultsArray.getString("countryName"));
                    mConutryListparser = null;
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.w("Exception", "ae" + e.getMessage());
        }

    }

    public int getConutryListparser(String shortName) {
        int indexCount = 0;
        for (int index = 0; index < mArrayList.size(); index++) {
            if (shortName
                    .equalsIgnoreCase(mArrayList.get(index).getCountryName())) {
                indexCount = index;
                break;
            }
        }
        return indexCount;
    }

    public void setPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            String readstrogePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
            String strogePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
            int hasReadStrogePermission = checkSelfPermission(readstrogePermission);
            int hasStrogePermission = checkSelfPermission(strogePermission);
            List<String> permissions = new ArrayList<String>();
            if (hasReadStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readstrogePermission);
            }

            if (hasStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(strogePermission);
            }

            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
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

    private void myProfiledetails() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                EditProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int code = mJsonObject.getInt("code");
                            if (code == 1) {
                                CacheFile.Cache(mContext).put("myprofile", response);
                            }
                        } catch (Exception ex) {

                        }
                    }
                });

            }
        });

    }
}
