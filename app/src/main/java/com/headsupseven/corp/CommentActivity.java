package com.headsupseven.corp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.headsupseven.corp.adapter.CommentslistAdapter;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.customview.Channgecustomview;
import com.headsupseven.corp.customview.EndlessRecyclerViewScrollListener;
import com.headsupseven.corp.customview.SimpleDividerItemDecoration;
import com.headsupseven.corp.model.CommentList;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


public class CommentActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private TextView text_title;
    private ListView listview;
    private int Postid = 0;
    private ImageView send_comment;
    private EditText edit_comment;
    private RecyclerView recyclerView;
    private Vector<CommentList> allCommentLists = new Vector<>();
    private CommentslistAdapter mCommentslistAdapter;
    private String PostType ="" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_coment_notification);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Postid = getIntent().getIntExtra("Postid", 0);
        PostType = getIntent().getStringExtra("PostType");

        initUI();

    }

    private void initUI() {
        edit_comment = (EditText) this.findViewById(R.id.edit_comment);
        text_title = (TextView) this.findViewById(R.id.text_title);
        text_title.setText("Comment");
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.this.finish();
            }
        });
        recyclerView = (RecyclerView) this.findViewById(R.id.listView_comment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mCommentslistAdapter = new CommentslistAdapter(mContext, allCommentLists);
        recyclerView.setAdapter(mCommentslistAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(CommentActivity.this));
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.w("page", "are: " + page);
            }
        });
        send_comment = (ImageView) this.findViewById(R.id.send_comment);
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_comment.getText().toString().trim().length() > 0) {
                    hideSoftKeyboard();
                    comment(edit_comment.getText().toString().trim());
                } else {
                    PopupAPI.showToast(mContext, "Please Enter Your Comment");
                    return;
                }

            }
        });
        getCommnentList(0);
    }

    public void getCommnentList(int page) {
        String urlData = "";
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("max", "10");
        param.put("page", "" + page);
        if (PostType.equalsIgnoreCase("ads")) {
            urlData = "ads/" + Postid + "/get-comments";
        } else {
            urlData = "feeds/" + Postid + "/get-comments";
        }

        APIHandler.Instance().POST_BY_AUTHEN(urlData, param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                Log.w("code", "are" + code);
                Log.w("response", "are" + response);

                CommentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int codeServer = mJsonObject.getInt("code");
                            if (codeServer == 1) {
                                JSONArray msg = mJsonObject.getJSONArray("msg");
                                for (int index = 0; index < msg.length(); index++) {
                                    JSONObject mObject = msg.getJSONObject(index);
                                    CommentList mCategoryList = new CommentList();
                                    mCategoryList.setID(mObject.getString("ID"));
                                    mCategoryList.setCreatedAt(mObject.getString("CreatedAt"));
                                    mCategoryList.setUserId(mObject.getString("UserId"));
                                    mCategoryList.setPostId(mObject.getString("PostId"));
                                    mCategoryList.setContent(mObject.getString("Content"));
                                    mCategoryList.setUserNmae(mObject.getString("UserName"));
                                    mCategoryList.setUserPic(mObject.getString("AvatarUrl"));
                                    mCommentslistAdapter.addnewItem(mCategoryList);
                                }
                            }

                        } catch (Exception e) {
                            PopupAPI.make(mContext, "Error", "Can't connect to server");

                        }

                    }
                });


            }
        });

    }

    public void comment(final String text) {
        String urlData = "";
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("content", text);
        if (PostType.equalsIgnoreCase("ads")) {
            urlData = "ads/" + Postid + "/add-comment";
        } else {
            urlData = "feeds/" + Postid + "/add-comment";
        }
        APIHandler.Instance().POST_BY_AUTHEN(urlData, param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                CommentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.w("response", "are" + response);
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {

                                if (Channgecustomview.commentTextView != null) {
                                    int currentCounnet = Integer.parseInt(Channgecustomview.commentTextView.getText().toString().trim());
                                    currentCounnet = currentCounnet + 1;
                                    Channgecustomview.commentTextView.setText("" + currentCounnet);
                                    Channgecustomview.homeLsitModel.setComment("" + currentCounnet);
                                }
//                                PopupAPI.showToast(mContext, mcode.getString("msg"));
                                CommentList mCategoryList = new CommentList();
                                mCategoryList.setID("" + APIHandler.Instance().user.userID);
                                //mCategoryList.setCreatedAt(mObject.getString("CreatedAt"));
                                mCategoryList.setUserId("" + APIHandler.Instance().user.userID);
                                //mCategoryList.setPostId(mObject.getString("PostId"));
                                String name = APIHandler.Instance().user.full_name;
                                mCategoryList.setUserNmae("" + name);
                                mCategoryList.setContent(text);
                                mCategoryList.setUserPic(APIHandler.Instance().user.avatar_url);
                                mCommentslistAdapter.addnewItem(mCategoryList);
                                edit_comment.setText("");

                            } else {
                                PopupAPI.make(mContext, "Error", "" + mcode.getString("msg"));

                            }

                        } catch (Exception ex) {
                            PopupAPI.make(mContext, "Error", "" + ex.getMessage());

                        }
                    }
                });

//
            }
        });
    }

    //hide the keyborad
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
