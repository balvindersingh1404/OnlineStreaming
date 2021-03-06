package com.headsupseven.corp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private Context mContext;
    private LinearLayout li_login;
    private LinearLayout li_signup;
    private TextView text_forgotpassword;

    private EditText txtUserName;
    private EditText txtPassword;
    private ImageView img_facebook;
    private ImageView imgGooglePlus;
    private ImageView image_twitter;
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
        FacebookSdk.sdkInitialize(LoginActivity.this);
        FacebookSdk.setIsDebugEnabled(true);
        setContentView(R.layout.activity_login);
        mContext = this;

        facebookSDKInitialize();
//        mGoogleApiClient = buidNewGoogleApiClient();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        client = new TwitterAuthClient();


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
        RandomKeyhash();
        li_login = (LinearLayout) this.findViewById(R.id.li_login);
        li_signup = (LinearLayout) this.findViewById(R.id.li_signup);
        img_facebook = (ImageView) this.findViewById(R.id.img_facebook);
        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile().getCurrentProfile();
                if (profile != null) {
                    HashMap<String, String> authenPostData = new HashMap<String, String>();
                    authenPostData.put("id", profile.getId());
                    authenPostData.put("name", profile.getName());
                    authenPostData.put("email", PersistentUser.getSocialLoginEmal(mContext));
                    socialLoginAccess("authen/facebook", authenPostData,1);
                    authenPostData.put("Platform", "mobile");

                } else {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile, email"));
                }

            }
        });
        imgGooglePlus = (ImageView) this.findViewById(R.id.imgGooglePlus);
        imgGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gPlusSignIn();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        image_twitter = (ImageView) this.findViewById(R.id.image_twitter);
        image_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        TwitterSession session = twitterSessionResult.data;
                        String userId = "" + session.getUserId();
                        String UserName = "" + session.getUserName();

                        HashMap<String, String> authenPostData = new HashMap<String, String>();
                        authenPostData.put("id", userId);
                        authenPostData.put("name", UserName);
                        authenPostData.put("email", "");
                        authenPostData.put("Platform", "mobile");
                        socialLoginAccess("authen/tweeter", authenPostData,2);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(LoginActivity.this, "Twitter Login Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        txtUserName = (EditText) this.findViewById(R.id.txtUserName);
        txtPassword = (EditText) this.findViewById(R.id.txtPassword);

        final SharedPreferences sp = getSharedPreferences(APIHandler.SHARED_KEY, Context.MODE_PRIVATE);
        String userName = sp.getString(APIHandler.SHARED_USERNAME, "");
//        txtUserName.setText(userName);

        text_forgotpassword = (TextView) this.findViewById(R.id.text_forgotpassword);
        text_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(mContext, FortgotpasswordActivity.class);
                startActivity(mm);
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
                                            authenPostData.put("Platform", "mobile");
                                            PersistentUser.setSocialLoginEmal(mContext, email);
                                            socialLoginAccess("authen/facebook", authenPostData,1);

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
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        //////////////////////////////////
//        txtUserName.setText("userprosanto");
//        txtPassword.setText("123456");
        /////////////////////////////////

        li_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    //TODO: send request to API
                    HashMap<String, String> authenPostData = new HashMap<String, String>();
                    authenPostData.put("UserName", txtUserName.getText().toString());
                    authenPostData.put("Password", txtPassword.getText().toString());
                    authenPostData.put("Platform", "mobile");

                    APIHandler.Instance().POST("authen/", authenPostData, new APIHandler.RequestComplete() {
                        @Override
                        public void onRequestComplete(int code, String response) {

                            if (code == 200 && response.length() > 0) {
                                try {
                                    JSONObject reader = new JSONObject(response);
                                    int resultCode = reader.getInt("code");
                                    if (resultCode == 1) {
                                        JSONObject msgData = reader.getJSONObject("msg");
                                        APIHandler.Instance().user.SetAuthenData(msgData);

                                        PersistentUser.setPassword(mContext, txtPassword.getText().toString());
                                        PersistentUser.setUserName(mContext, txtUserName.getText().toString());
                                        PersistentUser.setUserDetails(mContext, response);

                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(APIHandler.SHARED_USERNAME, txtUserName.getText().toString());
                                        editor.apply();

                                        APIHandler.Instance().InitChatClient();
                                        webCallPush(0);

                                    } else {
                                        final String resultMessage = reader.getString("msg");
                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                PopupAPI.make(mContext, "Error", resultMessage);
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                }
                            } else {
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        PopupAPI.make(mContext, "Error", "Can't connect to server");
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        li_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(mContext, SignupActivity.class);
                startActivity(mm);
            }
        });
    }


    protected void facebookSDKInitialize() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public void webCallPush(final int type) {
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
                                    LoginActivity.this.finish();
                                }

                            }
                        } catch (Exception e) {

                        }
                    }
                });

            }
        });
    }

    private Boolean validateFields() {
        String userName = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();

        if (userName.trim().isEmpty()) {
            if (txtUserName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            PopupAPI.make(mContext, "Error", "You need enter User Name");
            return false;
        }

        if (password.trim().isEmpty()) {
            if (txtPassword.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            PopupAPI.make(mContext, "Error", "You need enter Password");
            return false;
        }

        if (password.length() < 6) {
            PopupAPI.make(mContext, "Error", "Password too short. Need 6 or more characters");
            return false;
        }

        return true;
    }

    public void RandomKeyhash() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.headsupseven.corp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    //=============================onActivityResult=======================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);

        Log.w("requestCode", "are" + requestCode);
        Log.w("resultCode", "are" + resultCode);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
                Log.w("mGoogleApiClient", "mGoogleApiClient");

            }
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
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
                            webCallPush(type);

                        } else {
                            final String resultMessage = reader.getString("msg");
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PopupAPI.make(mContext, "Error", resultMessage);
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                } else {
                    LoginActivity.this.runOnUiThread(new Runnable() {
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
                        LoginActivity.this.finish();

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
            authenPostData.put("Platform", "mobile");
            socialLoginAccess("authen/google", authenPostData, 3);

        } else {
//            Toast.makeText(LoginActivity.this, "Google Login Fail", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}