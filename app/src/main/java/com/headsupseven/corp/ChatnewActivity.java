package com.headsupseven.corp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.chatdata.ChatAdapterNew;
import com.headsupseven.corp.chatdata.ChatBean;
import com.headsupseven.corp.utils.ImageFilePath;
import com.headsupseven.corp.utils.PopupAPI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Prosanto on 2/9/17.
 */

public class ChatnewActivity extends ChatingBase {
    private ListView listview;
    private ChatAdapterNew mChatAdapterNew;
    private Button btn_send;
    private CheckBox image_text_btn;
    private CheckBox image_camera_btn;
    private CheckBox image_gellary_btn;
    private EditText et_content;
    private LinearLayout tv_back;
    private TextView text_title;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int CAMERA = 302;
    private static final int GALLERY = 303;
    private File file_image = null;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static String JPEG_FILE_PREFIX = "";
    private static final String JPEG_FILE_SUFFIX = ".jpg";// jpg
    private Context mContext;
    public static ChatnewActivity mChatnewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_newchat_2);
        mContext = this;
        JPEG_FILE_PREFIX = getString(R.string.app_name) + "_";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        if (APIHandler.Instance().user.account_type.contains("regular")) {
            showsureaydialog();
            return;
        }
        mChatnewActivity=this;
        initUI();
    }

    public static ChatnewActivity getChatnewActivity() {
        return mChatnewActivity;
    }
    public int getFriendID() {
        return mFriendID;
    }
    @Override
    protected void userInformation(int position, String tex) {
        text_title.setText(tex);
    }
    private void initUI() {
        listview = (ListView) this.findViewById(R.id.listview);
        mChatAdapterNew = new ChatAdapterNew(mContext, getMessages());
        listview.setAdapter(mChatAdapterNew);
        mChatAdapterNew.notifyDataSetChanged();

        text_title = (TextView) this.findViewById(R.id.text_title);
        tv_back = (LinearLayout) this.findViewById(R.id.tv_back);
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        image_text_btn = (CheckBox) this.findViewById(R.id.image_text_btn);
        image_camera_btn = (CheckBox) this.findViewById(R.id.image_camera_btn);
        image_gellary_btn = (CheckBox) this.findViewById(R.id.image_gellary_btn);
        btn_send.setOnClickListener(listener);
        tv_back.setOnClickListener(listener);

        text_title.setText(mFriendName);

        image_camera_btn.setOnClickListener(listener);
        image_gellary_btn.setOnClickListener(listener);


        et_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (et_content.getText().toString().trim().equalsIgnoreCase("")) {
                        PopupAPI.showToast(mContext, "Please Enter Your Message");
                    } else {
                        hideSoftKeyboard();
                        try {
                            et_content.setText("");
                            sendMessage(et_content.getText().toString().trim());

                        } catch (Exception ex) {

                        }
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public void showsureaydialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_viodeupload);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        final TextView chat_funcation = (TextView) dialog.findViewById(R.id.chat_funcation);
        chat_funcation.setText("Chat Feature");

        final TextView text_details = (TextView) dialog.findViewById(R.id.text_details);
        text_details.setText("Only Content creator\nable to Chat");

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

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_send:
                    if (et_content.getText().toString().trim().equalsIgnoreCase("")) {
                        PopupAPI.showToast(mContext, "Please Enter Your Message");
                        return;
                    } else {
                        hideSoftKeyboard();
                        try {
                            sendMessage(et_content.getText().toString().trim());

                        } catch (Exception ex) {

                        }
                    }
                    break;
                case R.id.image_text_btn:
                    break;
                case R.id.image_camera_btn:
                    showCamera();
                    break;
                case R.id.image_gellary_btn:
                    showGallery();
                    break;
                case R.id.tv_back:
                    ChatnewActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    public void showCamera() {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                String cameraPermission = android.Manifest.permission.CAMERA;
                int hasCameraPermission = checkSelfPermission(cameraPermission);
                List<String> permissions = new ArrayList<String>();
                if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(cameraPermission);
                }
                if (!permissions.isEmpty()) {
                    String[] params = permissions.toArray(new String[permissions.size()]);
                    requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    file_image = createImageFile();
                    final Intent intent = new Intent(
                            "android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_image));
                    startActivityForResult(intent, CAMERA);

                }

            } else {
                file_image = createImageFile();
                final Intent intent = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_image));
                startActivityForResult(intent, CAMERA);
            }

        } catch (Exception ex) {
            PopupAPI.showToast(mContext, "Exception" + ex.getMessage());

        }
    }

    public void showGallery() {
        if (Build.VERSION.SDK_INT > 22) {
            String readstrogePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
            String strogePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String cameraPermission = android.Manifest.permission.CAMERA;

            int hasReadStrogePermission = checkSelfPermission(readstrogePermission);
            int hasStrogePermission = checkSelfPermission(strogePermission);
            int hasCameraPermission = checkSelfPermission(cameraPermission);


            List<String> permissions = new ArrayList<String>();
            if (hasReadStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(readstrogePermission);
            }

            if (hasStrogePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(strogePermission);
            }

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(cameraPermission);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(intent, GALLERY);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            startActivityForResult(intent, GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                Uri selectedUri = data.getData();
                String[] columns = {MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.MIME_TYPE};
                Cursor cursor = getContentResolver().query(selectedUri, columns, null, null, null);
                cursor.moveToFirst();
                int mimeTypeColumnIndex = cursor.getColumnIndex(columns[1]);
                String mimeType = cursor.getString(mimeTypeColumnIndex);
                String selectedImagePath = ImageFilePath.getPath(mContext, selectedUri);
                cursor.close();

                if (mimeType.startsWith("image")) {
                    addfileuploaddata(selectedImagePath, 1);
                } else if (mimeType.startsWith("video")) {
                    addfileuploaddata(selectedImagePath, 2);
                }
            } else if (requestCode == CAMERA) {
                final Uri selectedImageUri = Uri.fromFile(file_image);
                if (selectedImageUri != null) {
                    String selectedImagePath = ImageFilePath.getPath(mContext,
                            selectedImageUri);
                    addfileuploaddata(selectedImagePath, 1);
                }

            }
        }
    }

    protected List<ChatBean> getMessages() {
        return message_pool;
    }

    @Override
    protected void receiveNewMessage(ChatBean message) {
    }

    @Override
    protected void moreDataRecive(boolean flag) {
    }

    @Override
    protected void refreshMessage(List<ChatBean> messages) {
        Log.w("messages", "are" + messages.size());
        mChatAdapterNew.refreshList(messages);
        mChatAdapterNew.notifyDataSetChanged();
        listview.setSelection(getMessages().size() - 1);
    }

    @Override
    protected void refreshMessage(List<ChatBean> messages, int selection) {
        mChatAdapterNew.refreshList(messages);
        mChatAdapterNew.notifyDataSetChanged();
//        listview.setSelection(selection);
    }

    private File createImageFile() throws IOException {
        final String imageFileName = "images";
        final File albumF = getAlbumDir();
        final File imageF = File.createTempFile(imageFileName,
                JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory
                    .getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        return null;
                    }
                }
            }
        }
        return storageDir;
    }

    private String getAlbumName() {
        return getString(R.string.app_name);
    }


}

