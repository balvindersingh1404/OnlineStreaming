package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headsupseven.corp.application.MyApplication;
import com.headsupseven.corp.customview.LineView;
import com.headsupseven.corp.model.AddmanagerList;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by tmmac on 1/29/17.
 */

public class AddmanagerdetailsActivity extends AppCompatActivity {
    public Context mContext;
    private LinearLayout ll_silding;
    private TextView view_text;
    private TextView money_spent;
    private TextView money_budget;
    private TextView video_commnet;
    private TextView video_like;
    private TextView text_title;
    private LinearLayout country_status;
    private LineView lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmanage_details);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();

    }

    private String yTitle = "";
    private String xTitle = "";
    private int[] data = {70, 80, 90, 60, 80, 70, 40};
    private String[] lables = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private void initUI() {

        AddmanagerList mAddmanagerList = MyApplication.mAddmanagerList;
        if (mAddmanagerList == null)
            return;

        country_status = (LinearLayout) this.findViewById(R.id.country_status);
        text_title = (TextView) this.findViewById(R.id.text_title);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddmanagerdetailsActivity.this.finish();
            }
        });
        view_text = (TextView) this.findViewById(R.id.view_text);
        money_spent = (TextView) this.findViewById(R.id.money_spent);
        money_budget = (TextView) this.findViewById(R.id.money_budget);
        video_commnet = (TextView) this.findViewById(R.id.video_commnet);
        video_like = (TextView) this.findViewById(R.id.video_like);


        text_title.setText(mAddmanagerList.getPostName());
        view_text.setText("" + mAddmanagerList.getViewCount());
        video_commnet.setText("" + mAddmanagerList.getCommentCount());
        video_like.setText("" + mAddmanagerList.getLikeCount());


        lineView = (LineView) findViewById(R.id.lineView);

        lineView.setData(data);
        lineView.setLables(lables);
        lineView.setxTitle(xTitle);
        lineView.setyTitle(yTitle);
        lineView.setDataFactor(10);

        country_status.removeAllViews();

        try {

            JSONArray mJsonArray = new JSONArray(mAddmanagerList.getCountryData());
            for (int index = 0; index < mJsonArray.length(); index++) {
                JSONObject mJsonObject = mJsonArray.getJSONObject(index);
                String Name = mJsonObject.getString("Name");
                String Percent = mJsonObject.getString("Percent");
                LayoutInflater l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = l_Inflater.inflate(R.layout.row_coutrystatus, null, false);
                final TextView tvname = (TextView) convertView.findViewById(R.id.tvname);
                final TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                tvname.setText(Name);
                tv_status.setText(Percent + "%");
                country_status.addView(convertView);

            }
        } catch (Exception ex) {
            Log.w("Exception", "adse" + ex.getMessage());
        }


    }
}
