package com.headsupseven.corp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.api.chat.ChatManager;
import com.headsupseven.corp.chatdata.ChatBean;
import com.headsupseven.corp.chatdata.CommonValue;
import com.headsupseven.corp.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ChatingBase extends AppCompatActivity {
    protected List<ChatBean> message_pool = new ArrayList<ChatBean>();
    public  int mFriendID = 0;
    public String mFriendName = "", AvatarUrl = "";
    private String sessionID = "";
    public Context mContext;
    private ProgressDialog mProgressDialog;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        message_pool.clear();
        mFriendID = getIntent().getIntExtra("ChatUser", 0);
        userDetails();
    }

    public void userDetails() {
        HashMap<String, String> param = new HashMap<String, String>();
        APIHandler.Instance().POST_BY_AUTHEN("user/app/" + mFriendID + "/details", param, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(final int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            int code = mJsonObject.getInt("code");
                            if (code == 1) {
                                JSONObject msg = mJsonObject.getJSONObject("msg");
                                JSONObject userObject = msg.getJSONObject("UserModel");
                                mFriendName = userObject.getString("UserName");
                                String FullName = userObject.getString("FullName");
                                AvatarUrl = userObject.getString("AvatarUrl");
                                userInformation(0, mFriendName);

                            }
                        } catch (Exception ex) {

                        }
                        previousChatList();

                    }
                });
            }
        });

    }

    private void previousChatList() {

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
        APIHandler.Instance().Chat().GetSessionDetails(mFriendID, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {
                mProgressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatDataParsing(response);
                    }
                });
            }
        });
    }

    public void chatDataParsing(String response) {
        try {
            Log.w("response", "are: " + response);

            JSONObject obj = new JSONObject(response);
            int code = obj.getInt("code");
            if (code == 25) {
                final JSONArray data = new JSONArray(obj.getString("content"));
                for (int index = 0; index < data.length(); index++) {
                    ChatBean mChatBean = new ChatBean();
                    JSONObject dw = data.getJSONObject(index);
                    Log.w("dw", "arew" + dw);
                    int receive = dw.getInt("UserReceive");
                    int MessageType = dw.getInt("MessageType");
                    String ms = dw.getString("MessageContent");
                    mChatBean.setText(ms);


                    //I send the message
                    //1>text,4>image,5>>video
                    //
                    if (receive != APIHandler.Instance().user.userID) {
                        mChatBean.setUserName(APIHandler.Instance().user.userName);
                        mChatBean.setUserIcon(APIHandler.Instance().user.avatar_url);

                        if (MessageType == 1)
                            mChatBean.setType(5);
                        else if (MessageType == 4)
                            mChatBean.setType(6);
                        else if (MessageType == 5)
                            mChatBean.setType(7);
                    } else {
                        //receiver,
                        mChatBean.setUserName(mFriendName);
                        mChatBean.setUserIcon(AvatarUrl);
                        if (MessageType == 1)
                            mChatBean.setType(0);
                        else if (MessageType == 4)
                            mChatBean.setType(1);
                        else if (MessageType == 5)
                            mChatBean.setType(2);
                    }
                    message_pool.add(mChatBean);

                }
                refreshMessage(message_pool);

            }

        } catch (Exception e) {
        }
    }

    public void showData(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject(message);
                    int from = object.getInt("from");
                    String from_name = object.getString("from-name");
                    String content = object.getString("content");
                    String content_type = object.getString("content-type");
                    if (content_type.contains("ws-text")) {
                        ChatBean mChatBean = new ChatBean();
                        mChatBean.setText(content);
                        mChatBean.setType(0);
                        mChatBean.setUserName(mFriendName);
                        mChatBean.setUserIcon(AvatarUrl);
                        message_pool.add(mChatBean);


                    } else if (content_type.contains("ws-image")) {
                        ChatBean mChatBean = new ChatBean();
                        mChatBean.setText(content);
                        mChatBean.setType(1);
                        mChatBean.setUserName(mFriendName);
                        mChatBean.setUserIcon(AvatarUrl);
                        message_pool.add(mChatBean);


                    } else if (content_type.contains("ws-video")) {
                        ChatBean mChatBean = new ChatBean();
                        mChatBean.setText(content);
                        mChatBean.setType(2);
                        mChatBean.setUserName(mFriendName);
                        mChatBean.setUserIcon(AvatarUrl);
                        message_pool.add(mChatBean);


                    }
                    refreshMessage(message_pool);

                } catch (Exception ex) {
                }
            }
        });

    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.NEW_MESSAGE_ACTION);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (CommonValue.NEW_MESSAGE_ACTION.equals(action)) {
                String message = intent.getStringExtra(CommonValue.messageData);
                showData(message);
            }
        }

    };

    protected abstract void userInformation(int position, String tex);

    protected abstract void receiveNewMessage(ChatBean message);

    protected abstract void moreDataRecive(boolean flag);

    protected abstract void refreshMessage(List<ChatBean> messages);

    protected abstract void refreshMessage(List<ChatBean> messages, int selection);

    protected List<ChatBean> getMessages() {
        return message_pool;
    }

    protected void sendMessage(String content) throws Exception {

        ChatBean mChatBean = new ChatBean();
        mChatBean.setType(5);
        mChatBean.setUserName(APIHandler.Instance().user.userName);
        mChatBean.setUserIcon(APIHandler.Instance().user.avatar_url);

        mChatBean.setText(content);
        message_pool.add(mChatBean);
        refreshMessage(message_pool);
        sendTextMessage(content);
    }

    public void sendTextMessage(String content) {

        APIHandler.Instance().Chat().SendMessage(APIHandler.Instance().user.userID, mFriendID, sessionID, content, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {
                try {
                    Log.w("response", "are: " + response);

                } catch (Exception e) {

                }
            }
        });
    }

    protected int addNewMessage(int currentPage) {
        List<ChatBean> newMsgList = new ArrayList<>();
//        if (newMsgList != null && newMsgList.size() > 0) {
//            message_pool.addAll(newMsgList);
//            Collections.sort(message_pool);
//            return newMsgList.size();
//        }
        return 0;
    }



    public void addfileuploaddata(String selectedImagePath, int type) {

        if (type == 1) {
            ChatBean mChatBean = new ChatBean();
            mChatBean.setType(6);
            mChatBean.setText(selectedImagePath);
            mChatBean.setUserName(APIHandler.Instance().user.userName);
            mChatBean.setUserIcon(APIHandler.Instance().user.avatar_url);

            mChatBean.setLocalFile(true);
            mChatBean.setUploadingfile(true);
            message_pool.add(mChatBean);
            refreshMessage(message_pool);
        } else if (type == 2) {
            ChatBean mChatBean = new ChatBean();
            mChatBean.setType(7);
            mChatBean.setUserName(APIHandler.Instance().user.userName);
            mChatBean.setUserIcon(APIHandler.Instance().user.avatar_url);

            mChatBean.setLocalFile(true);
            mChatBean.setUploadingfile(true);
            mChatBean.setText(selectedImagePath);
            message_pool.add(mChatBean);
            refreshMessage(message_pool);
        }

        uploadfile(selectedImagePath, type);
    }

    private Bitmap bit;
    private byte photoFileData[] = new byte[0];

    private void prepareImageData(Uri selectedImage) {
        // TODO Auto-generated method stub
        try {
            final InputStream is = mActivity.getContentResolver()
                    .openInputStream(selectedImage);
            try {
                if (is.available() > 0) {

                    photoFileData = new byte[is.available()];
                    is.read(photoFileData, 0, is.available());
                    is.close();
                    bit = BitmapUtils.getBitmapFromByteArray(photoFileData, 512);

                }

            } catch (final IOException e) {
            }

        } catch (final FileNotFoundException e) {
        } catch (final Exception e) {
        }

    }

    public void serverUploadAndSendVideo(String content) {
        APIHandler.Instance().Chat().UploadAndSendVideo(APIHandler.Instance().user.userID, mFriendID, sessionID, content, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are: " + response);

                    }
                });
            }
        });
    }
    public void serverUploadAndSendPhoto(String content) {
        APIHandler.Instance().Chat().UploadAndSendPhoto(APIHandler.Instance().user.userID, mFriendID, sessionID, content, new ChatManager.GetDataComplete() {
            @Override
            public void onDataComplete(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("response", "are: " + response);

                    }
                });

            }
        });
    }
    public void uploadfile(final String file_path, final int type) {

        APIHandler.Instance().Multipartupload("upload", file_path, new APIHandler.RequestComplete() {
            @Override
            public void onRequestComplete(int code, final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject mJsonObject = new JSONObject(response);
                            if (mJsonObject.getInt("code") == 1) {
                                String content = mJsonObject.getString("content");
                                if (type == 1) {
                                    serverUploadAndSendPhoto(content);
                                    Bitmap mBitmap = decodeFile(file_path);
                                    Log.w("mBitmap","are"+mBitmap);

                                    File mFile = createfilefordata(content);
                                    saveImageFile(mBitmap, mFile);

                                } else if (type == 2) {
                                    serverUploadAndSendVideo(content);
                                    File sourceLocation = new File(file_path);
                                    File targetLocation = createfilefordata(content);
                                    copyDirectoryOneLocationToAnotherLocation(sourceLocation, targetLocation);
                                }

                            }
                        } catch (Exception ex) {

                        }
                    }
                });
            }
        });
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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

        String extStorageDirectory = Environment
                .getExternalStorageDirectory().toString();
        File directory = new File(extStorageDirectory + "/"
                + mContext.getString(R.string.app_name) + "/pub");

        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("Directory already created");
        }
        return directory;
    }

    public Bitmap decodeFile(String photoPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        return bitmap;
    }

    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }
}
