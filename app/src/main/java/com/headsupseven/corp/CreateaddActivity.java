package com.headsupseven.corp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.ConutryListparser;
import com.headsupseven.corp.utils.ImageFilePath;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Created by tmmac on 1/29/17.
 */

public class CreateaddActivity extends AppCompatActivity {


    private Context mContext;
    private EditText edt_video_title;
    private EditText edt_line;
    private CheckBox check_lifetime;
    private CheckBox check_perday;
    private RelativeLayout rl_start_date;
    private RelativeLayout rl_start_time;
    private RelativeLayout rl_end_date;
    private RelativeLayout rl_end_time;
    private TextView tv_start_date;
    private TextView tv_start_time;
    private TextView tv_end_date;
    private TextView tv_end_time;
    private TextView tv_post;
    private TextView show_date;

    private CheckBox checkbox_live;
    private CheckBox checkbox_degree;
    private CheckBox checkbox_Gallery;

    // for DateTeme
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    private static final int LOCATION_SELECTED = 1000;
    private int timeType = 0;
    private int dateType = 0;
    private int time_end = 0;
    public int year, month, day, hour, minute;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String timeSelect = "";
    private String Mode = "";
    private static final int GALLERY = 303;
    HashMap<String, String> authenPostData = new HashMap<String, String>();
    HashMap<String, String> filePostData = new HashMap<String, String>();
    private ImageView add_image;
    private AQuery androidQuery;
    public ArrayList<ConutryListparser> mArrayList = new ArrayList<ConutryListparser>();
    public ArrayList<String> mCountryList = new ArrayList<String>();
    private Spinner country_adapder;
    private LinearLayout layout_add_video;
    private String mVideoType = "normal";
    private String dataType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createadd);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        authenPostData.clear();
        filePostData.clear();
        androidQuery = new AQuery(mContext);
        initUI();

    }

    private void initUI() {

        country_adapder = (Spinner) this.findViewById(R.id.country_adapder);
        layout_add_video = (LinearLayout) this.findViewById(R.id.layout_add_video);
        layout_add_video.setVisibility(View.GONE);

        add_image = (ImageView) this.findViewById(R.id.add_image);
        edt_video_title = (EditText) this.findViewById(R.id.edt_video_title);
        edt_line = (EditText) this.findViewById(R.id.edt_line);
        check_lifetime = (CheckBox) this.findViewById(R.id.check_lifetime);
        check_lifetime.setOnClickListener(listener);
        check_perday = (CheckBox) this.findViewById(R.id.check_perday);
        check_perday.setOnClickListener(listener);
        rl_start_date = (RelativeLayout) this.findViewById(R.id.rl_start_date);
        rl_start_date.setOnClickListener(listener);
        rl_start_time = (RelativeLayout) this.findViewById(R.id.rl_start_time);
        rl_start_time.setOnClickListener(listener);
        rl_end_date = (RelativeLayout) this.findViewById(R.id.rl_end_date);
        rl_end_date.setOnClickListener(listener);
        rl_end_time = (RelativeLayout) this.findViewById(R.id.rl_end_time);
        rl_end_time.setOnClickListener(listener);
        tv_start_date = (TextView) this.findViewById(R.id.tv_start_date);
        tv_start_time = (TextView) this.findViewById(R.id.tv_start_time);
        tv_end_date = (TextView) this.findViewById(R.id.tv_end_date);
        tv_end_time = (TextView) this.findViewById(R.id.tv_end_time);
        tv_post = (TextView) this.findViewById(R.id.tv_post);
        tv_post.setOnClickListener(listener);
        show_date = (TextView) this.findViewById(R.id.show_date);

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


        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePostData.clear();
                GALLERYINTENT();
            }
        });
        LinearLayout ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateaddActivity.this.finish();
            }
        });

        showMobileData();
        checkBoxSelectUnselect(1);
        Mode = "perday";
        selectionVideoOption(0);

        showFirstTime();
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.check_lifetime:
                    Mode = "lifetime";
                    checkBoxSelectUnselect(0);
                    break;
                case R.id.check_perday:
                    checkBoxSelectUnselect(1);
                    Mode = "perday";
                    break;
                case R.id.rl_start_date:
                    DateAndTimePickerActivity();
                    dateType = 1;
                    showDialog(DATE_DIALOG_ID);
                    break;

                case R.id.rl_start_time:
                    DateAndTimePickerActivity();
                    timeType = 1;
                    showDialog(TIME_DIALOG_ID);
                    break;
                case R.id.rl_end_date:

                    DateAndTimePickerActivity();
                    dateType = 2;
                    showDialog(DATE_DIALOG_ID);
                    break;
                case R.id.rl_end_time:
                    DateAndTimePickerActivity();
                    timeType = 2;
                    showDialog(TIME_DIALOG_ID);
                    break;
                case R.id.tv_post:
                    setVerification();
                    break;

            }

        }
    };

    public void selectionVideoOption(int pos) {
        checkbox_Gallery.setChecked(false);
        checkbox_degree.setChecked(false);
        checkbox_live.setChecked(false);
        if (pos == 0) {
            checkbox_live.setChecked(true);
            mVideoType = "normal";
        } else if (pos == 1) {
            checkbox_degree.setChecked(true);
            mVideoType = "360";
        } else if (pos == 2) {
            checkbox_Gallery.setChecked(true);
            mVideoType = "vr";
        }
    }

    private String video_title = "";
    private String linek = "";
    private String startDate = "";
    private String startTime = "";
    private String endDate = "";
    private String endTime = "";


    public void setVerification() {


        video_title = edt_video_title.getText().toString();
        linek = edt_line.getText().toString();
        startDate = tv_start_date.getText().toString();
        startTime = tv_start_time.getText().toString();
        endDate = tv_end_date.getText().toString();
        endTime = tv_end_time.getText().toString();
        tv_post.setOnClickListener(listener);
        if (video_title.equalsIgnoreCase("")) {
            toast("Please Enter Video Title.");
            return;
        } else if (linek.equalsIgnoreCase("")) {
            toast("Please Enter Description.");
            return;
        } else if (Mode.equalsIgnoreCase("")) {
            toast("Please check Lifetime or per Day.");
            return;
        } else if (startDate.equalsIgnoreCase("")) {
            toast("Please Enter Start Date .");
            return;
        } else if (startTime.equalsIgnoreCase("")) {
            toast("Please Enter Start Time.");
            return;
        } else if (endDate.equalsIgnoreCase("")) {
            toast("Please Enter End Date.");
            return;
        } else if (endTime.equalsIgnoreCase("")) {
            toast("Please Enter End Time.");
            return;
        } else if (filePostData.size() == 0) {

            toast("Please Select Video");
            return;
        } else {


            try {

                authenPostData.clear();
                String dateOfStart = returunDateformate(startDate + " " + startTime);
                String dateOfEnd = returunDateformate(endDate + " " + endTime);


                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date1 = myFormat.parse(dateOfStart);
                Date date2 = myFormat.parse(dateOfEnd);
                long diff = date2.getTime() - date1.getTime();
                long dateRemail = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                if (0 >= dateRemail) {
                    toast("Start date & End date wrong!");
                    return;

                } else {
                    if (Mode.contains("perday")) {

                        authenPostData.put("Mode", Mode);
                        authenPostData.put("AdsName", video_title);
                        authenPostData.put("AdsDescription", linek);
                        authenPostData.put("StartDate", dateOfStart + "Z");
                        authenPostData.put("EndDate", dateOfEnd + "Z");
                        if (dataType.equalsIgnoreCase("image")) {

                            uploadVideo("ads//add-photo");

                        } else {
                            authenPostData.put("VideoType", mVideoType);
                            uploadVideo("ads//add-video");

                        }

                    } else {

                        authenPostData.put("Mode", Mode);
                        authenPostData.put("AdsName", video_title);
                        authenPostData.put("AdsDescription", linek);


                        if (dataType.equalsIgnoreCase("image")) {
                            uploadVideo("ads//add-photo");

                        } else {
                            authenPostData.put("VideoType", mVideoType);
                            uploadVideo("ads//add-video");

                        }

                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //""
        //2006-01-02T15:04:05.000Z


    }

    public void GALLERYINTENT() {
//
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("video/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
//        startActivityForResult(intent, GALLERY);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        startActivityForResult(intent, GALLERY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedUri = data.getData();
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                filePostData.put("videofile", selectedImagePath);

                String[] columns = {MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.MIME_TYPE};
                Cursor cursor = getContentResolver().query(selectedUri, columns, null, null, null);
                cursor.moveToFirst();
                int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);
                String mimeType = cursor.getString(mimeTypeColumnIndex);
                cursor.close();
                if (mimeType.startsWith("image")) {
                    layout_add_video.setVisibility(View.GONE);
                    addfileuploaddata(selectedImagePath, 1);
                    dataType = "image";
                } else if (mimeType.startsWith("video")) {
                    dataType = "video";

                    layout_add_video.setVisibility(View.VISIBLE);
                    addfileuploaddata(selectedImagePath, 2);

                }

            }
        }
    }

    public void addfileuploaddata(String path, int type) {
        File mFile = new File(path);
        if (mFile.exists()) {
            if (type == 1) {
                androidQuery.id(add_image).image(mFile, 300);
            } else if (type == 2) {
                try {
                    Bitmap mBitmap = retriveVideoFrameFromVideo(mFile.getAbsolutePath());
                    if (mBitmap != null) {
                        add_image.setImageBitmap(mBitmap);
                    }
                } catch (Throwable ex) {
                }

            }
        }

    }

    // bitmap return from sd card video path
    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
        return thumb;
    }

    ProgressDialog mProgressDialog;

    public void uploadVideo(String url) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        Log.w("authenPostData","are"+authenPostData.toString());

        APIHandler.Instance().UPLOAD_BY_AUTHEN(url, authenPostData, filePostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, final String response) {
                CreateaddActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        try {
                            Log.w("sd", "ae" + response);
                            JSONObject mJsonObject = new JSONObject(response);
                            int codeServer = mJsonObject.getInt("code");
                            if (codeServer < 0 && mJsonObject.getString("msg") == "not-enough-balance") {
                                PopupAPI.showToast(mContext, "user not enough balance to create ads");
                                Intent mIntent = new Intent();
                                mIntent.putExtra("response", response);
                                setResult(RESULT_OK, mIntent);
                                finish();
                            } else if (codeServer == 1) {
                                Intent mIntent = new Intent();
                                mIntent.putExtra("response", response);
                                setResult(RESULT_OK, mIntent);
                                finish();
                            }

                        } catch (Exception e) {
                            PopupAPI.showToast(mContext, e.getMessage());

                        }
                    }
                });
            }
        });

    }


    public String returunDateformate(String startDate_all) {
        String startDate = startDate_all;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            Date date = fmt.parse(startDate_all);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            startDate = fmtOut.format(date);
            return startDate;
        } catch (Exception ex) {

        }
        return startDate;
    }

    public void DateAndTimePickerActivity() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        // the callback received when the user "sets" the TimePickerDialog in
        // the dialog
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            String am_pm = "";
            if (minute < 10) {
                String inf = "0" + minute;
                minute = Integer.parseInt(inf);
                timeSelect = inf;
            } else {
                timeSelect = "" + minute;
            }
            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12"
                    : Integer.toString(datetime.get(Calendar.HOUR));
            String selectedTime = strHrsToShow + ":" + timeSelect + " " + am_pm;

            if (timeType == 1) {
                tv_start_time.setText(selectedTime);
            } else if (timeType == 2) {
                tv_end_time.setText(selectedTime);
            }

        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {


        public void onDateSet(DatePicker view, int yearSelected,
                              int monthOfYear, int dayOfMonth) {
            year = yearSelected;
            month = monthOfYear;
            day = dayOfMonth;
            String date = checkDigit(monthOfYear + 1) + "/" + checkDigit(dayOfMonth) + "/" + year;
            Date datee = new Date(date);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String format2 = format.format(datee);
            if (dateType == 1) {
                tv_start_date.setText(format2);
            } else {
                tv_end_date.setText(format2);
            }

        }
    };

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    public void checkBoxSelectUnselect(int flag) {
        if (flag == 0) {
            check_perday.setChecked(false);
            check_perday.setSelected(false);
            check_lifetime.setChecked(true);
            check_lifetime.setSelected(true);
        } else {
            check_lifetime.setChecked(false);
            check_lifetime.setSelected(false);
            check_perday.setChecked(true);
            check_perday.setSelected(true);
        }
    }

    public void toast(String massage) {
        Toast.makeText(mContext, "" + massage, Toast.LENGTH_LONG).show();

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
                        // optionally add some animations for fading out
                        setArrayAdapter();
                    }
                });
            }
        }, 2000);
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

    public void setArrayAdapter() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mCountryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_adapder.setAdapter(dataAdapter);

    }

    public void showFirstTime() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        String AM_PM = " AM";
        String mm_precede = "";
        if (mHour >= 12) {
            AM_PM = " PM";
            if (mHour >= 13 && mHour < 24) {
                mHour -= 12;
            } else {
                mHour = 12;
            }
        } else if (mHour == 0) {
            mHour = 12;
        }
        if (mMinute < 10) {
            mm_precede = "0";
        }

        tv_start_date.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
        tv_start_time.setText(mHour + ":" + mm_precede + mMinute + AM_PM);
        //=========next ================

        String dataCurrent=mYear + "-" + (mMonth + 1) + "-" + mDay;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            c.setTime(sdf.parse(dataCurrent));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String output = sdf1.format(c.getTime());

        tv_end_date.setText(output);
        tv_end_time.setText(mHour + ":" + mm_precede + mMinute + AM_PM);

    }

}
