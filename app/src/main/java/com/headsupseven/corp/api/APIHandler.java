package com.headsupseven.corp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.headsupseven.corp.api.chat.ChatManager;
import com.headsupseven.corp.model.ForgetCodeModel;
import com.paypal.android.sdk.payments.PayPalConfiguration;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;


/**
 * Created by Nam Nguyen on 2/1/2017.
 */

public class APIHandler {
    //======================================================================
    // API Instance
    //======================================================================
    private static APIHandler mInstance = null;

    public static APIHandler Instance() {
        if (mInstance == null) {
            mInstance = new APIHandler();
        }
        return mInstance;
    }

    public APIHandler() {
        this.user = new User();
    }

    //======================================================================
    // Chat API
    //======================================================================
    private ChatManager mChat;

    public void InitChatClient() {
        mChat = new ChatManager();
        mChat.SendLogin(user.accessToken, user.userID);
    }

    public ChatManager Chat() {
        return this.mChat;
    }

    //======================================================================
    //
    //======================================================================
    public final static String HeadsUpServerIP = "54.254.199.253";
    //=========================================================================

    public final static String RootAPI = "http://" + HeadsUpServerIP + ":1234/api/";
    public final static String ChatURL = "http://" + HeadsUpServerIP + ":1250/";
    public final static String StorageURL = "http://" + HeadsUpServerIP + ":8080/";

    public User user = null;
    public ForgetCodeModel forgetMode = null;
    public final static String SHARED_KEY = "dwam0d128d1n";
    public final static String SHARED_USERNAME = "user_name";
    //======================================================================
    //
    //======================================================================
    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String CONFIG_CLIENT_ID = "AU1i9qo0QtxyehA3DQTyPfEfEDi7N8IqcUUfw85k-k4vrWkfRv9FysTfrLYNvblVLV7hE9bKosir04n1";
    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("HeadsUp7")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/privacy-full"))
            .merchantUserAgreementUri(Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full"));
    //==========================================================================

    HttpURLConnection urlConnection = null;

    public interface RequestComplete {
        void onRequestComplete(final int code, final String response);
    }

    //======================================================================
    // API Request
    //======================================================================
    public String BuildLiveStreamUrl() {
        String uri = Uri.parse("rtmp://" + HeadsUpServerIP + ":1935/live")
                .buildUpon()
                .appendQueryParameter("token", user.accessToken)
                .appendQueryParameter("id", user.userID + "")
                .build().toString();
        return uri + "/" + user.userName;
    }

    public String BuildLiveStreamWatchURL(String stream) {
        String uri = Uri.parse("rtmp://" + HeadsUpServerIP + ":1935/live")
                .buildUpon()
                .appendQueryParameter("token", user.accessToken)
                .appendQueryParameter("id", user.userID + "")
                .build().toString();
        return uri + "/" + stream;
    }

    public String BuildLiveStreamVideoRecorded(String videopath) {
        String uri = Uri.parse("http://" + HeadsUpServerIP + ":8080")
                .buildUpon()
                .build().toString();
        return uri + "/" + videopath;
    }

    public String refressTokenCall() {
        String responseData = "";

        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", "" + APIHandler.Instance().user.userID);
            params.put("refresh", "" + APIHandler.Instance().user.refreshToken);
            String query = "authen/refresh?id=" + this.user.userID + "&token=" + this.user.accessToken;
            String postDataString = postDataString(params);

            URL apiURL = new URL(RootAPI + query);
            urlConnection = (HttpURLConnection) apiURL.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            OutputStream outStream = urlConnection.getOutputStream();
            outStream.write(postDataString.getBytes("UTF-8"));
            outStream.flush();
            outStream.close();


            // get the data result
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    responseData += line;
                }
            }


        } catch (Exception ex) {

        }


        return responseData;
    }

    //======================================================================
    // Post Task
    //======================================================================
    class PostTask extends AsyncTask<String, Void, Void> {
        final RequestComplete mCallback;

        PostTask(RequestComplete callback) {
            this.mCallback = callback;
        }

        protected Void doInBackground(String... params) {
            try {
                URL apiURL = new URL(RootAPI + params[0]);
                urlConnection = (HttpURLConnection) apiURL.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // get post data if have params
                if (params.length > 1) {
                    OutputStream outStream = urlConnection.getOutputStream();
                    outStream.write(params[1].getBytes("UTF-8"));
                    outStream.flush();
                    outStream.close();
                }

                // get the data result
                int responseCode = urlConnection.getResponseCode();
                String responseBody = "";
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }

                // callback
                this.mCallback.onRequestComplete(responseCode, responseBody);

            } catch (IOException e) {
                this.mCallback.onRequestComplete(HttpURLConnection.HTTP_FORBIDDEN, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

    public void POST(String query, HashMap<String, String> params, RequestComplete callback) {
        new PostTask(callback).execute(query, postDataString(params));
    }

    public void POST_BY_AUTHEN(String query, HashMap<String, String> params, RequestComplete callback) {
        query += "?id=" + this.user.userID + "&token=" + this.user.accessToken;
        new PostTask(callback).execute(query, postDataString(params));
    }

    private String postDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        try {
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (Exception e) {

        }
        return result.toString();
    }

    private byte[] postDataByte(HashMap<String, String> params) {
        byte[] output = "".getBytes();
        try {
            String postData = postDataString(params);
            output = postData.getBytes("UTF-8");
        } catch (Exception e) {
            // TODO: check here
        }
        return output;
    }

    //======================================================================
    // Get Task
    //======================================================================
    class GetTask extends AsyncTask<String, Void, Void> {
        final RequestComplete mCallback;
        private URL apiURL = null;
        private int responseCode = -1;
        private String responseBody = "";

        GetTask(RequestComplete callback) {
            this.mCallback = callback;
        }

        protected Void doInBackground(String... params) {
            try {
                apiURL = new URL(RootAPI + params[0]);
                urlConnection = (HttpURLConnection) apiURL.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // get the data result
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }
                //============= Check token valid or invalid==============
//                JSONObject mresponseBody = new JSONObject(responseBody);
//                int code = mresponseBody.getInt("code");
//                if (code == -30) {
//                    String refressTokenCall = refressTokenCall();
//                    JSONObject refree = new JSONObject(refressTokenCall);
//                    if (refree.getInt("code") == 2) {
//
//                        JSONObject msg = refree.getJSONObject("msg");
//                        String refresh_token = msg.getString("refresh_token");
//                        String token = msg.getString("token");
//                        APIHandler.mInstance.user.refreshToken = refresh_token;
//                        APIHandler.mInstance.user.accessToken = token;
//
//
//                        URL apiURL = new URL(RootAPI + params[0]);
//                        urlConnection = (HttpURLConnection) apiURL.openConnection();
//                        urlConnection.setConnectTimeout(10000);
//                        urlConnection.setRequestMethod("GET");
//                        urlConnection.connect();
//                        // get the data result
//                        responseCode = urlConnection.getResponseCode();
//                        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
//                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                            String line;
//                            while ((line = br.readLine()) != null) {
//                                responseBody += line;
//                            }
//                        }
//                    }
//                }

                // callback
                this.mCallback.onRequestComplete(responseCode, responseBody);

            } catch (Exception e) {
                this.mCallback.onRequestComplete(HttpURLConnection.HTTP_FORBIDDEN, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

    public void GET(String query, RequestComplete callback) {
        new GetTask(callback).execute(query);
    }

    //=======================
    class GetTask_BY_AUTHEN extends AsyncTask<String, Void, Void> {
        final RequestComplete mCallback;
        private URL apiURL = null;
        private int responseCode = -1;
        private String responseBody = "";

        GetTask_BY_AUTHEN(RequestComplete callback) {
            this.mCallback = callback;
        }

        protected Void doInBackground(String... params) {
            try {
                String query = params[0] + "?id=" + user.userID + "&token=" + user.accessToken;
                apiURL = new URL(RootAPI + query);
                urlConnection = (HttpURLConnection) apiURL.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // get the data result
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }
                //============= Check token valid or invalid==============
                JSONObject mresponseBody = new JSONObject(responseBody);
                int code = mresponseBody.getInt("code");
                if (code == -30) {
                    String refressTokenCall = refressTokenCall();
                    JSONObject refree = new JSONObject(refressTokenCall);
                    if (refree.getInt("code") == 2) {

                        JSONObject msg = refree.getJSONObject("msg");
                        String refresh_token = msg.getString("refresh_token");
                        String token = msg.getString("token");
                        APIHandler.mInstance.user.refreshToken = refresh_token;
                        APIHandler.mInstance.user.accessToken = token;
                        query = params[0] + "?id=" + APIHandler.mInstance.user.userID + "&token=" + APIHandler.mInstance.user.accessToken;
                        apiURL = new URL(RootAPI + query);

                        URL apiURL = new URL(RootAPI + params[0]);
                        urlConnection = (HttpURLConnection) apiURL.openConnection();
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        // get the data result
                        responseCode = urlConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                responseBody += line;
                            }
                        }
                    }
                }
                // callback
                this.mCallback.onRequestComplete(responseCode, responseBody);

            } catch (Exception e) {
                this.mCallback.onRequestComplete(HttpURLConnection.HTTP_FORBIDDEN, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }


    public void GET_BY_AUTHEN(String query, RequestComplete callback) {
        new GetTask_BY_AUTHEN(callback).execute(query);
    }

    //=======================
    class GetTask_BY_AUTHEN_BY_PARAM extends AsyncTask<String, Void, Void> {
        final RequestComplete mCallback;
        private URL apiURL = null;
        private int responseCode = -1;
        private String responseBody = "";
        private HashMap<String, String> params_Return = null;

        GetTask_BY_AUTHEN_BY_PARAM(RequestComplete callback, HashMap<String, String> params) {
            this.mCallback = callback;
            this.params_Return = params;
        }

        protected Void doInBackground(String... params) {
            try {
                //query += "?id=" + this.user.userID + "&token=" + this.user.accessToken;
                //query = getDataString(query, params);

                String query = params[0] + "?id=" + user.userID + "&token=" + user.accessToken;
                query = getDataString(query, params_Return);

                apiURL = new URL(RootAPI + query);
                urlConnection = (HttpURLConnection) apiURL.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // get the data result
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }
                //============= Check token valid or invalid==============
                JSONObject mresponseBody = new JSONObject(responseBody);
                int code = mresponseBody.getInt("code");
                if (code == -30) {
                    String refressTokenCall = refressTokenCall();
                    JSONObject refree = new JSONObject(refressTokenCall);
                    if (refree.getInt("code") == 2) {

                        JSONObject msg = refree.getJSONObject("msg");
                        String refresh_token = msg.getString("refresh_token");
                        String token = msg.getString("token");
                        APIHandler.mInstance.user.refreshToken = refresh_token;
                        APIHandler.mInstance.user.accessToken = token;
                        query = params[0] + "?id=" + APIHandler.mInstance.user.userID + "&token=" + APIHandler.mInstance.user.accessToken;
                        apiURL = new URL(RootAPI + query);

                        URL apiURL = new URL(RootAPI + params[0]);
                        urlConnection = (HttpURLConnection) apiURL.openConnection();
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        // get the data result
                        responseCode = urlConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;
                            while ((line = br.readLine()) != null) {
                                responseBody += line;
                            }
                        }
                    }
                }
                // callback
                this.mCallback.onRequestComplete(responseCode, responseBody);

            } catch (Exception e) {
                this.mCallback.onRequestComplete(HttpURLConnection.HTTP_FORBIDDEN, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

    public void GET_BY_AUTHEN(String query, HashMap<String, String> params, RequestComplete callback) {
        new GetTask_BY_AUTHEN_BY_PARAM(callback, params).execute(query);
    }

    private String getDataString(String BaseUrl, HashMap<String, String> params) {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                BaseUrl = BaseUrl + "&" + entry.getKey() + "=" + entry.getValue();
            }
        } catch (Exception e) {

        }
        return BaseUrl.toString();
    }

    /**
     * image upload into server
     */
    public void UPLOAD_BY_AUTHEN(String query, HashMap<String, String> params, HashMap<String, String> paramsfile, RequestComplete callback) {
        query += "?id=" + this.user.userID + "&token=" + this.user.accessToken;
        new PostTaskforimageupload(callback, params, paramsfile).execute(query, postDataString(params));
    }

    private class PostTaskforimageupload extends AsyncTask<String, Integer, String> {

        final RequestComplete mCallback;
        private int responseCode = 0;
        private HashMap<String, String> paramsData;
        private HashMap<String, String> paramsfile;
        int maxBufferSize = 1 * 1024 * 1024; // read buffer

        PostTaskforimageupload(RequestComplete callback, HashMap<String, String> params, HashMap<String, String> paramsfile) {
            this.mCallback = callback;
            this.paramsData = params;
            this.paramsfile = paramsfile;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String asa = RootAPI + params[0];
                URL url = new URL(RootAPI + params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                //===========================================
                request = new DataOutputStream(urlConnection.getOutputStream());


                if (paramsData.size() > 0) {
                    for (Map.Entry<String, String> entry : paramsData.entrySet()) {
                        request.writeBytes(twoHyphens + boundary + newline);
                        request.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + newline + newline);
                        request.writeBytes(entry.getValue());
                        request.writeBytes(newline);
                    }
                }
                //===========================================

                if (paramsfile.size() > 0) {
                    for (Map.Entry<String, String> entry : paramsfile.entrySet()) {
                        String filePath = entry.getValue();

                        String[] pathSplits = filePath.split("/");
                        String fileName = pathSplits[pathSplits.length - 1];
                        request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
                        request.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileName + "\"" + newline);
                        request.writeBytes("Content-Type: " + newline); // set content type here ?
                        request.writeBytes(newline);
                        //-----------------------------------------
                        // get file data
                        //-----------------------------------------
                        FileInputStream fileInput = new FileInputStream(new File(filePath));
                        int bytesAvailable = fileInput.available();
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        //should check bytesAvailable > maxBufferSize here to know file is too big for upload
                        byte[] buffer = new byte[bufferSize];
                        int byteRead = fileInput.read(buffer, 0, bufferSize);
                        while (byteRead > 0) {
                            request.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInput.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            byteRead = fileInput.read(buffer, 0, bufferSize);
                        }
                        fileInput.close();
                        request.writeBytes(newline);


                    }
                }

                request.writeBytes(twoHyphens + boundary + twoHyphens + newline);
                //-----------------------------------------
                request.flush();
                request.close();
                // get the data result
                responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;
                    }
                }

            } catch (IOException e) {
                responseCode = HttpURLConnection.HTTP_FORBIDDEN;
                responseBody = e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... prova) {

        }

        @Override
        protected void onPostExecute(String result) {
            this.mCallback.onRequestComplete(responseCode, responseBody);
        }
    }

    public static String GetDeviceID(Context ctx) {
        final SharedPreferences prefs = ctx.getSharedPreferences(
                "Headsup7AppPreferences", Context.MODE_PRIVATE);
        String deviceID =  prefs.getString("unique_device_id", "");
        if(!deviceID.isEmpty()){
            return deviceID;
        }
        //-----------------------------------
        try {
            //------------------------------------------------
            //NOTE: get by serial
            String _serial = (String) Build.class.getField("SERIAL").get(
                    null);
            if (!_serial.isEmpty()) {
                deviceID = _serial;
            }
        }catch(Exception e){

        }
        //----------------------------------
        if (deviceID.isEmpty()){
            //------------------------------------------------
            //NOTE: we get it by Android ID
            String _serial = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!_serial.isEmpty()) {
                deviceID = _serial;
                // save
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString("unique_device_id", deviceID);
                editor.commit();
                // return
                return deviceID;
            }else{
                //------------------------------------------------
                //NOTE: last choice to generate ID is random it
                deviceID = RandomString(32);
                // save
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString("unique_device_id", deviceID);
                editor.commit();
                // return
                return deviceID;
            }
        }else{
            // save
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString("unique_device_id", deviceID);
            editor.commit();
            // return
            return deviceID;
        }
    }

    public static String RandomString(int length) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(length);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    //upload file into server
    String newline = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    DataOutputStream request;
    String responseBody = "";
    public boolean flagserverConnect = false;
    private UploadTask mUploadTask;

    public void Multipartupload(String query, String fileName, RequestComplete callback) {
        mUploadTask = new UploadTask(callback);
        mUploadTask.execute(query, fileName);
    }

    private class UploadTask extends AsyncTask<String, Integer, String> {

        final RequestComplete mCallback;
        int responseCode = 0;

        UploadTask(RequestComplete callback) {
            this.mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.w("onPreExecute", "onPreExecute");

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                flagserverConnect = true;

                String userID = "" + APIHandler.Instance().user.userID;
                String accessToken = APIHandler.Instance().user.accessToken;
                URL url = new URL(ChatURL + params[0]);
                String file_path = params[1];
                Log.w("file_path", ": " + file_path);

                String[] pathSplits = file_path.split("/");
                String fileName = pathSplits[pathSplits.length - 1];
                int maxBufferSize = 1 * 1024 * 1024; // read buffer


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                //===========================================
                request = new DataOutputStream(urlConnection.getOutputStream());

                // field : id
                //-----------------------------------------
                request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
                request.writeBytes("Content-Disposition: form-data; name=\"id\"" + newline + newline);
                request.writeBytes(userID); // << data here
                request.writeBytes(newline);
                //-----------------------------------------
                // field : access token
                //-----------------------------------------
                request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
                request.writeBytes("Content-Disposition: form-data; name=\"token\"" + newline + newline);
                request.writeBytes(accessToken); // << data here
                request.writeBytes(newline);
                //-----------------------------------------
                // field: files
                //-----------------------------------------
                request.writeBytes(twoHyphens + boundary + newline); //    v (key here)
                request.writeBytes("Content-Disposition: form-data; name=\"files\"; filename=\"" + fileName + "\"" + newline);
                request.writeBytes("Content-Type: " + newline); // set content type here ?
                request.writeBytes(newline);


                // get file data
                //-----------------------------------------
                FileInputStream fileInput = new FileInputStream(new File(file_path));
                int bytesAvailable = fileInput.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                Log.w("bytesAvailable", "are: " + bytesAvailable);

                //should check bytesAvailable > maxBufferSize here to know file is too big for upload
                int progress = 0;
                int totalRead = 0;
                byte[] buffer = new byte[bufferSize];
                int byteRead = fileInput.read(buffer, 0, bufferSize);
                while (byteRead > 0) {
                    request.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInput.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byteRead = fileInput.read(buffer, 0, bufferSize);
                    //for showthe progress
                    totalRead += byteRead;
                    progress = (int) (totalRead * (100 / (double) bytesAvailable));
                    publishProgress(progress);

                }
                fileInput.close();
                request.writeBytes(newline);
                //-----------------------------------------
                // mark end request
                //-----------------------------------------
                request.writeBytes(twoHyphens + boundary + twoHyphens + newline);
                //-----------------------------------------
                request.flush();
                request.close();
                // get the data result
                responseCode = urlConnection.getResponseCode();
                responseBody = "";
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 307) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody += line;

                    }
                }

            } catch (IOException e) {
                responseCode = HttpURLConnection.HTTP_FORBIDDEN;
                responseBody = e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... prova) {
            Log.w("progress", "upload.>>>" + prova[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("upload", "complete");
            flagserverConnect = false;
            this.mCallback.onRequestComplete(responseCode, responseBody);

        }
    }

    public void Downloadfilefromserver(String query, String fileName, RequestComplete callback) {
        new Downloadfilefromserver(callback).execute(query, fileName);
    }

    private class Downloadfilefromserver extends AsyncTask<String, String, String> {
        final RequestComplete mCallback;

        Downloadfilefromserver(RequestComplete callback) {
            this.mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String fileName = params[1];
            File myFile = new File(fileName);
            try {
                URL url = new URL(params[0]);
                // URLConnection connection = url.openConnection();
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();
                int fileSize = connection.getContentLength();
                if (fileSize == -1)
                    fileSize = connection.getHeaderFieldInt("Length", -1);

                InputStream is = new BufferedInputStream(url.openStream());
                OutputStream os = new FileOutputStream(myFile);
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = is.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileSize));
                    os.write(data, 0, count);
                }
                os.flush();
                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileName;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onCancelled() {

        }

        @Override
        protected void onPostExecute(String filename) {
            Log.w("filename", "are" + filename);
        }
    }


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");
            currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = currentDateFormat.parse(time);
            String dateStrTime = format.format(date);
            Date dateDifference = format.parse(dateStrTime);


            String CurrentDateAndTime = getCurrentDateAndTime();
            Date dateCurrent = format.parse(CurrentDateAndTime);

            return getTimeAgo(dateCurrent, dateDifference);

//            Date date = df.parse(time);
//            return getTimeAgo(date.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getTimeAgo(Date date1, Date date2) {

//        final long diff = now - time;
        final long diff = date1.getTime() - date2.getTime();


//        if (time < 1000000000000L) {
//            // if timestamp given in seconds, convert to millis
//            time *= 1000;
//        }
//
//        long now = System.currentTimeMillis();
//        if (time > now || time <= 0) {
//            return null;
//        }
//
//        // TODO: localize
//        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getCurrentDateAndTime() {
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        simple.setTimeZone(TimeZone.getDefault());
        return simple.format(new Date());
    }
}
