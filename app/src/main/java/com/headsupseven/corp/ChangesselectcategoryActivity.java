package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.adapter.SelectCategoryAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.CategoryList;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tmmac on 1/26/17.
 */

public class ChangesselectcategoryActivity extends AppCompatActivity {
    private GridView gridView;
    private SelectCategoryAdapter adapter;
    private Context mContext;
    private ImageView back;
    private TextView btn_submit;
    private ArrayList<CategoryList> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        mContext = this;
        initUI();
    }

    public void initUI() {

        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangesselectcategoryActivity.this.finish();
            }
        });
        gridView = (GridView) this.findViewById(R.id.gridview);
        adapter = new SelectCategoryAdapter(mContext, arrayList);
        gridView.setAdapter(adapter);

        btn_submit = (TextView) this.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<CategoryList> arrayList = adapter.getselectedObject();
                if (arrayList.size() == 0) {
                    PopupAPI.showToast(mContext, "Select min 1 category");
                    return;
                } else {
                    addCategory(arrayList);
                }

//
            }
        });
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("page", "0");
        param.put("max", "100");
        APIHandler.Instance().GET_BY_AUTHEN("category", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                if (response.length() > 0) {
                    ChangesselectcategoryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject mJsonObject = new JSONObject(response);
                                int codeServer = mJsonObject.getInt("code");
                                if (codeServer == 1) {
                                    JSONArray msg = mJsonObject.getJSONArray("msg");
                                    for (int index = 0; index < msg.length(); index++) {
                                        JSONObject mObject = msg.getJSONObject(index);
                                        CategoryList mCategoryList = new CategoryList();
                                        mCategoryList.setID(mObject.getString("ID"));
                                        mCategoryList.setName(mObject.getString("Name"));
                                        mCategoryList.setThumbnailUrl(mObject.getString("ThumbnailUrl"));
                                        mCategoryList.setDescription(mObject.getString("Description"));
                                        adapter.addnewObject(mCategoryList);
                                    }
                                }

                            } catch (Exception e) {
                                PopupAPI.make(mContext, "Error", "Can't connect to server");

                            }

                        }
                    });

                }

            }
        });
    }

    public void addCategory(ArrayList<CategoryList> arrayList) {
        String categoriesId = "";
        for (int index = 0; index < arrayList.size(); index++) {
            categoriesId = categoriesId + "," + arrayList.get(index).getID();
        }
        if (categoriesId.length() > 0)
            categoriesId = categoriesId.substring(1, categoriesId.length());

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("categories", categoriesId);
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + APIHandler.Instance().user.userID + "/set-category", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                ChangesselectcategoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {
                                PopupAPI.showToast(mContext, "Categrory updated !");
                                ChangesselectcategoryActivity.this.finish();
                            } else {
                                PopupAPI.make(mContext, "Error", mcode.getString("msg"));

                            }


                        } catch (Exception ex) {
                            PopupAPI.make(mContext, "Error", "Can't connect to server");
                        }
                    }
                });

//
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChangesselectcategoryActivity.this.finish();
    }
}
