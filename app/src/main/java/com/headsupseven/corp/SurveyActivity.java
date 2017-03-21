package com.headsupseven.corp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.ServayModel;
import com.headsupseven.corp.utils.Logcate;
import com.headsupseven.corp.utils.PopupAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by Prosanto on 2/15/17.
 */

public class SurveyActivity extends AppCompatActivity {
    private Context mContext;
    private LinearLayout ll_silding;
    private LinearLayout other_layout;
    private RadioGroup radioGroup1;
    private ProgressDialog mProgressDialog;
    private ServayModel model;
    private Vector<ServayModel> allServayQuestion = new Vector<ServayModel>();
    private int numberQuestion = 0;
    private TextView tv_question;
    private RadioButton radio_1, radio_2, radio_3, radio_4, radio_5, radio_6;
    private TextView btn_Next;
    private JSONArray jsonArray = null;
    private EditText comment_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        numberQuestion = 0;

        jsonArray = new JSONArray();
        initializeActivity();

    }

    private void initializeActivity() {
        radioGroup1 = (RadioGroup) this.findViewById(R.id.radioGroup1);
        radioGroup1.setVisibility(View.GONE);

        allServayQuestion.clear();
        comment_text = (EditText) this.findViewById(R.id.comment_text);
        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurveyActivity.this.finish();
            }
        });
        btn_Next = (TextView) this.findViewById(R.id.btn_Next);
        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ansCalculation();
            }
        });
        radio_1 = (RadioButton) this.findViewById(R.id.radio_1);
        radio_2 = (RadioButton) this.findViewById(R.id.radio_2);
        radio_3 = (RadioButton) this.findViewById(R.id.radio_3);
        radio_4 = (RadioButton) this.findViewById(R.id.radio_4);
        radio_5 = (RadioButton) this.findViewById(R.id.radio_5);
        radio_6 = (RadioButton) this.findViewById(R.id.radio_6);


        other_layout = (LinearLayout) this.findViewById(R.id.other_layout);
        tv_question = (TextView) this.findViewById(R.id.tv_question);
        RadioGroup yourRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        other_layout.setVisibility(View.GONE);

        yourRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_1:
                        // TODO Something
                        other_layout.setVisibility(View.GONE);

                        break;
                    case R.id.radio_2:
                        other_layout.setVisibility(View.GONE);

                        // TODO Something
                        break;
                    case R.id.radio_3:
                        other_layout.setVisibility(View.GONE);

                        // TODO Something
                        break;
                    case R.id.radio_4:
                        other_layout.setVisibility(View.GONE);

                        // TODO Something
                        break;
                    case R.id.radio_5:
                        other_layout.setVisibility(View.GONE);
                        // TODO Something
                        break;
                    case R.id.radio_6:
                        other_layout.setVisibility(View.VISIBLE);
                        // TODO Something
                        break;
                }
            }
        });
        btn_Next.setEnabled(false);
        loadQuestion();

    }

    public void ansCalculation() {

        int index_selected = radioGroup1.indexOfChild(radioGroup1
                .findViewById(radioGroup1.getCheckedRadioButtonId()));
        if ((numberQuestion + 1) == allServayQuestion.size())
            btn_Next.setText("Submit");

        //======For select input box
        if (index_selected == 5) {
            if (comment_text.getText().toString().trim().equalsIgnoreCase("")) {
                PopupAPI.showToast(mContext, "Please Enter Your Answare");
                return;
            } else {

                try {
                    ServayModel newmodel = allServayQuestion.get(numberQuestion);
                    JSONObject mJsonObject = new JSONObject();
                    mJsonObject.put("question", newmodel.getID());
                    mJsonObject.put("answer", (index_selected + 1));
                    mJsonObject.put("data", comment_text.getText().toString());
                    jsonArray.put(mJsonObject);
                    numberQuestion++;
                } catch (Exception ex) {

                }
            }
        } else {

            RadioButton mRadioButton = (RadioButton) radioGroup1.findViewById(radioGroup1.getCheckedRadioButtonId());
            try {
                if (mRadioButton.getVisibility() == View.VISIBLE) {
                    ServayModel newmodel = allServayQuestion.get(numberQuestion);
                    JSONObject mJsonObject = new JSONObject();
                    mJsonObject.put("question", newmodel.getID());
                    mJsonObject.put("answer", (index_selected + 1));
                    mJsonObject.put("data", "");
                    jsonArray.put(mJsonObject);
                    numberQuestion++;

                } else {
                    PopupAPI.showToast(mContext, "Please Select Your Answare");
                    return;
                }


            } catch (Exception ex) {

            }


        }

        if (btn_Next.getText().toString().equalsIgnoreCase("Submit")) {
            try {
                JSONObject mJsonObject = new JSONObject();
                mJsonObject.put("survey", 1);
                mJsonObject.put("answers", jsonArray);
                answeareQuestion(mJsonObject.toString());
            } catch (Exception ex) {

            }
        } else {
            getQuestionDetails();

        }
    }

    public void answeareQuestion(String query) {

        Logcate.logcateW("Servery And", query);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("data", query);
        APIHandler.Instance().POST_BY_AUTHEN("survey/submit", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                SurveyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Log.w("survay response is", response);
                        try {

                            JSONObject mJsonObject = new JSONObject(response);
                            int coderesults = mJsonObject.getInt("code");
                            if (coderesults == 1) {
                                PopupAPI.showToast(mContext, "Thank you for your response.");
                                finish();
                            }
                        } catch (Exception ex) {

                        }
                    }
                });

            }
        });
    }


    public void loadQuestion() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("survey", "1");
        APIHandler.Instance().GET_BY_AUTHEN("survey/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                SurveyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        radioGroup1.setVisibility(View.VISIBLE);

                        Log.w("survay response is", response);
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int code = mJsonObject.getInt("code");
                            if (code == 1) {
                                JSONArray array = mJsonObject.getJSONArray("msg");
                                for (int i = 0; i < array.length(); i++) {
                                    model = new ServayModel();
                                    JSONObject arrayObject = array.getJSONObject(i);

                                    model.setID(arrayObject.getInt("ID"));
                                    model.setSurveyID(arrayObject.getInt("SurveyID"));
                                    model.setAnswer1(arrayObject.getString("Answer1"));
                                    model.setAnswer2(arrayObject.getString("Answer2"));
                                    model.setAnswer3(arrayObject.getString("Answer3"));
                                    model.setAnswer4(arrayObject.getString("Answer4"));
                                    model.setAnswer5(arrayObject.getString("Answer5"));
                                    model.setCustomAnswer(arrayObject.getBoolean("CustomAnswer"));
                                    model.setCreatedAt(arrayObject.getString("CreatedAt"));
                                    model.setQuestion(arrayObject.getString("Question"));
                                    model.setUpdatedAt(arrayObject.getString("UpdatedAt"));

                                    allServayQuestion.add(model);


                                }


                                getQuestionDetails();
                                btn_Next.setEnabled(true);

                            }
                        } catch (Exception ex) {

                        }
                    }
                });

            }
        });
    }

    public void getQuestionDetails() {


        if (allServayQuestion.size() == 0 || numberQuestion >= allServayQuestion.size()) {
            return;
        } else {
            ServayModel newmodel = allServayQuestion.get(numberQuestion);
            tv_question.setText(newmodel.getQuestion() + "");

            radio_1.setVisibility(View.VISIBLE);
            radio_2.setVisibility(View.VISIBLE);
            radio_3.setVisibility(View.VISIBLE);
            radio_4.setVisibility(View.VISIBLE);
            radio_5.setVisibility(View.VISIBLE);
            radio_6.setVisibility(View.VISIBLE);


            radio_1.setText(newmodel.getAnswer1() + "");
            radio_2.setText(newmodel.getAnswer2() + "");
            radio_3.setText(newmodel.getAnswer3() + "");
            radio_4.setText(newmodel.getAnswer4() + "");
            radio_5.setText(newmodel.getAnswer5() + "");

            radio_1.setChecked(true);
            other_layout.setVisibility(View.GONE);

            if (newmodel.getAnswer1().equalsIgnoreCase(""))
                radio_1.setVisibility(View.GONE);

            if (newmodel.getAnswer2().equalsIgnoreCase(""))
                radio_2.setVisibility(View.GONE);

            if (newmodel.getAnswer3().equalsIgnoreCase(""))
                radio_3.setVisibility(View.GONE);

            if (newmodel.getAnswer4().equalsIgnoreCase(""))
                radio_4.setVisibility(View.GONE);

            if (newmodel.getAnswer5().equalsIgnoreCase(""))
                radio_5.setVisibility(View.GONE);

            if (newmodel.isCustomAnswer()) {
                radio_6.setVisibility(View.VISIBLE);
            } else {
                radio_6.setVisibility(View.GONE);
            }


            comment_text.setText("");

        }

    }

}
