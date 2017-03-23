package com.headsupseven.corp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.utils.PersistentUser;
import com.headsupseven.corp.utils.PopupAPI;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class SignupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private LinearLayout li_signup;
    private TextView text_terms_condition;
    private ImageView im_back;
    private CircularImageView profile_avatar;
    private static final int GALLERY = 303;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private EditText edittext_fullname;
    private EditText edittext_username;
    private EditText edittext_email;
    private EditText edittext_password;
    private CheckBox checkbox_Regular;
    private CheckBox checkbox_Content;
    private CheckBox checkbox_terms;
    private String AcountType = "";
    private SharedPreferences sp;
    private ProgressDialog mProgressDialog;
    private ImageView imgFacebook;
    private ImageView imgGooglePlus;
    private ImageView img_twitter;

    // Facebook
    CallbackManager callbackManager;
    //for google+
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int SIGN_IN_REQUEST_CODE = 10;
    private static final int ERROR_DIALOG_REQUEST_CODE = 11;
    private boolean mIntentInProgress;
    //for twitter
    private TwitterAuthClient client;
    private static final String TWITTER_KEY = "WFr2rsX2lo8pkvq9O2fNE5mFF";
    private static final String TWITTER_SECRET = "ogvWK1cB9l0fcUMwAkmZsJU7kfCxlyXxksfYNc2Bc15DMpEPMC";

    private static final int RC_SIGN_IN = 007;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(SignupActivity.this);
        FacebookSdk.setIsDebugEnabled(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        mContext = this;
        getFacebookHashKey();
        facebookSDKInitialize();


        sp = getSharedPreferences(APIHandler.SHARED_KEY, Context.MODE_PRIVATE);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        startService(intent);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        initUi();
    }

    private void initUi() {

        edittext_fullname = (EditText) this.findViewById(R.id.edittext_fullname);
        edittext_username = (EditText) this.findViewById(R.id.edittext_username);
        edittext_email = (EditText) this.findViewById(R.id.edittext_email);
        edittext_password = (EditText) this.findViewById(R.id.edittext_password);
        checkbox_Regular = (CheckBox) this.findViewById(R.id.checkbox_Regular);
        checkbox_Content = (CheckBox) this.findViewById(R.id.checkbox_Content);
        checkbox_terms = (CheckBox) this.findViewById(R.id.checkbox_terms);
        imgFacebook = (ImageView) this.findViewById(R.id.imgFacebook);
        imgGooglePlus = (ImageView) this.findViewById(R.id.imgGooglePlus);
        img_twitter = (ImageView) this.findViewById(R.id.img_twitter);

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile().getCurrentProfile();
                if (profile != null) {
                    HashMap<String, String> authenPostData = new HashMap<String, String>();
                    authenPostData.put("id", profile.getId());
                    authenPostData.put("name", profile.getName());
                    authenPostData.put("email", PersistentUser.getSocialLoginEmal(mContext));
                    socialLoginAccess("authen/facebook", authenPostData, 1);
                } else {
                    LoginManager.getInstance().logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile, email"));
                }

            }
        });
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        Log.w("Facebook Response", "are" + response);

                                        try {
                                            String id = object.getString("id");
                                            String name = object.getString("name");
                                            String email = object.getString("email");
                                            HashMap<String, String> authenPostData = new HashMap<String, String>();
                                            authenPostData.put("id", id);
                                            authenPostData.put("name", name);
                                            authenPostData.put("email", email);
                                            PersistentUser.setSocialLoginEmal(mContext, email);
                                            socialLoginAccess("authen/facebook", authenPostData, 1);

                                            try {
                                                Profile.fetchProfileForCurrentAccessToken();

                                            } catch (NullPointerException e) {

                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,email,picture.width(300),last_name");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SignupActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SignupActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        imgGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gPlusSignIn();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        img_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.authorize(SignupActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        TwitterSession session = twitterSessionResult.data;
                        String userId = "" + session.getUserId();
                        String UserName = "" + session.getUserName();

                        HashMap<String, String> authenPostData = new HashMap<String, String>();
                        authenPostData.put("id", userId);
                        authenPostData.put("name", UserName);
                        authenPostData.put("email", "");
                        socialLoginAccess("authen/tweeter", authenPostData, 2);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(SignupActivity.this, "Twitter Login Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        li_signup = (LinearLayout) this.findViewById(R.id.li_signip);
        li_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();

            }
        });
        text_terms_condition = (TextView) this.findViewById(R.id.text_terms_condition);
        text_terms_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(mContext, TermsandconditionActivity.class);
                startActivity(mm);
            }
        });
        im_back = (ImageView) this.findViewById(R.id.im_back);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.finish();

            }
        });

        profile_avatar = (CircularImageView) this.findViewById(R.id.profile_avatar);
        checkAcountType(0);
        checkbox_Regular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAcountType(0);

            }
        });
        checkbox_Content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAcountType(1);

            }
        });


    }

    protected void facebookSDKInitialize() {
        callbackManager = CallbackManager.Factory.create();
    }

    public void validation() {
        if (edittext_fullname.getText().toString().trim().equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please Enter FullName");
            return;
        } else if (edittext_username.getText().toString().trim().equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please Enter UserName");
            return;
        } else if (edittext_email.getText().toString().trim().equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please Enter Email Address");
            return;
        } else if (edittext_password.getText().toString().trim().equalsIgnoreCase("")) {
            PopupAPI.showToast(mContext, "Please Enter Password");
            return;
        } else if (edittext_password.getText().toString().trim().length() < 6) {
            PopupAPI.showToast(mContext, "Please Enter Password Min 6 Char");
            return;
        } else if (!checkbox_terms.isChecked()) {
            PopupAPI.showToast(mContext, "Please Read Terms & Condition");
            return;
        } else {
            webCallRegistraion();
        }
    }

    public void webCallRegistraion() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("UserName", edittext_username.getText().toString());
        authenPostData.put("FullName", edittext_fullname.getText().toString());
        authenPostData.put("Email", edittext_email.getText().toString());
        authenPostData.put("Password", edittext_password.getText().toString());
        authenPostData.put("AccountType", AcountType);
        APIHandler.Instance().POST("user/register", authenPostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, final String response) {
                Log.w("register", "register" + response);
                SignupActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject reader = new JSONObject(response);
                            String msg = reader.getString("msg");
                            int code2 = reader.getInt("code");
                            if (code2 == 1) {
                                registrationUser();
                            } else {
                                PopupAPI.make(mContext, "Error", msg);
                                mProgressDialog.dismiss();
                            }

                        } catch (Exception ex) {
                            PopupAPI.make(mContext, "Error", ex.getMessage());
                            mProgressDialog.dismiss();
                        }
                    }
                });


            }
        });
    }

    /**
     * Login on new registraion user
     */
    public void registrationUser() {
        HashMap<String, String> authenPostData = new HashMap<String, String>();
        authenPostData.put("UserName", edittext_username.getText().toString());
        authenPostData.put("Password", edittext_password.getText().toString());

        APIHandler.Instance().POST("authen/", authenPostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, String response) {

                mProgressDialog.dismiss();
                if (code == 200 && response.length() > 0) {
                    try {
                        JSONObject reader = new JSONObject(response);
                        int resultCode = reader.getInt("code");
                        if (resultCode == 1) {

                            PersistentUser.setPassword(mContext, edittext_password.getText().toString());
                            PersistentUser.setUserName(mContext, edittext_username.getText().toString());
                            PersistentUser.setUserDetails(mContext, response);


                            JSONObject msgData = reader.getJSONObject("msg");
                            APIHandler.Instance().user.SetAuthenData(msgData);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(APIHandler.SHARED_USERNAME, edittext_username.getText().toString());
                            editor.apply();
                            APIHandler.Instance().InitChatClient();


                            if (AcountType.equalsIgnoreCase("regular")) {
                                webCallPush();
                            } else {
                                onBuyPressed();
                            }
                        } else {
                            final String resultMessage = reader.getString("msg");
                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();

                                    PopupAPI.make(mContext, "Error", resultMessage);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                } else {
                    SignupActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            PopupAPI.make(mContext, "Error", "Can't connect to server");
                        }
                    });
                }
            }
        });
    }

    public void webCallPush() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("device-token", PersistentUser.GetPushkey(mContext));
        APIHandler.Instance().POST_BY_AUTHEN("push/register-android", param, new APIHandler.RequestComplete() {
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
                                PersistentUser.setLogin(mContext);
                                Intent mm = new Intent(mContext, SurveySignupActivity.class);
                                mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mm);
                                SignupActivity.this.finish();
                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    public void checkAcountType(final int position) {
        checkbox_Regular.setChecked(false);
        checkbox_Content.setChecked(false);
        if (position == 0) {
            checkbox_Regular.setChecked(true);
            AcountType = "regular";
        } else {
            checkbox_Content.setChecked(true);
            AcountType = "content-creator";

        }
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

    private static final int REQUEST_CODE_PAYMENT = 1;

    public void onBuyPressed() {
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(SignupActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, APIHandler.Instance().config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("15.5"), "USD", "For 1 Year",
                paymentIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paylem = confirm.toJSONObject().toString(4);
                        Log.w("paylem", "are" + paylem);

                        JSONObject mJsonObject = new JSONObject(paylem);
                        JSONObject response = mJsonObject.getJSONObject("response");
                        String id = response.getString("id");

                        webserviceupgrade(id);
                    } catch (Exception e) {
                        Log.w("Exception", "are" + e.getMessage());
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                webCallPush();
            }
        } else if (requestCode == SIGN_IN_REQUEST_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void webserviceupgrade(String paypal_d) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("paypal-id", paypal_d);
        APIHandler.Instance().POST_BY_AUTHEN("payment/add-balance-upgrade", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                SignupActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.w("response", "are" + response);
                            JSONObject mcode = new JSONObject(response);
                            int code2 = mcode.getInt("code");
                            if (code2 == 1) {
                                PopupAPI.showToast(mContext, mcode.getString("msg"));
                                webCallPush();
                            } else {
                                PopupAPI.make(mContext, "Error", mcode.getString("msg"));

                            }

                        } catch (Exception ex) {
                            Log.w("webserviceupgrade", "are" + ex.getMessage());

                        }
                    }
                });

//
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void getFacebookHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.headsupseven.corp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    //=================== social login web-service================================

    public void socialLoginAccess(final String url, HashMap<String, String> authenPostData, final int type) {
        APIHandler.Instance().POST(url, authenPostData, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, String response) {
                Log.w("response", "are: " + response);
                Log.w("code", "are: " + code);

                if (code == 200 && response.length() > 0) {
                    try {
                        JSONObject reader = new JSONObject(response);
                        int resultCode = reader.getInt("code");
                        if (resultCode == 1) {
                            JSONObject msgData = reader.getJSONObject("msg");
                            APIHandler.Instance().user.SetAuthenData(msgData);
                            PersistentUser.setUserDetails(mContext, response);
                            APIHandler.Instance().InitChatClient();
                            webCallPushLogin(type);

                        } else {
                            final String resultMessage = reader.getString("msg");
                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PopupAPI.make(mContext, "Error", resultMessage);
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                } else {
                    SignupActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PopupAPI.make(mContext, "Error", "Can't connect to server");
                        }
                    });
                }
            }
        });
    }

    // ===============================google sing end ===============================//
    private static final String TAG = LoginActivity.class.getSimpleName();

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        PersistentUser.setLogin(mContext);
                        Intent mm = new Intent(mContext, HomebaseActivity.class);
                        mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mm);
                        SignupActivity.this.finish();

                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            String id = acct.getId();
            HashMap<String, String> authenPostData = new HashMap<String, String>();
            authenPostData.put("id", id);
            authenPostData.put("name", personName);
            authenPostData.put("email", email);
            socialLoginAccess("authen/google", authenPostData, 3);

        } else {
//            Toast.makeText(LoginActivity.this, "Google Login Fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void webCallPushLogin(final int type) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("device-token", PersistentUser.GetPushkey(mContext));
        APIHandler.Instance().POST_BY_AUTHEN("push/register-android", param, new APIHandler.RequestComplete() {
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
                                if (type == 3) {
                                    signOut();
                                } else {
                                    PersistentUser.setLogin(mContext);
                                    Intent mm = new Intent(mContext, HomebaseActivity.class);
                                    mm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mm);
                                    SignupActivity.this.finish();
                                }

                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }
}
