package com.headsupseven.corp.api.chat;

import android.util.Log;

import com.headsupseven.corp.api.APIHandler;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by Nam Nguyen on 2/7/2017.
 */

public class ChatManager implements WebSocketListener{
    private static final String TAG = "CHAT MANAGER";
    private static final String WS_HOST_NAME = "ws://"+ APIHandler.HeadsUpServerIP+":1250/chat";

    private static final String MSG_TYPE_TEXT = "ws-text";
    private static final String MSG_TYPE_LOGIN = "ws-login";
    private static final String MSG_TYPE_IMAGE = "ws-image";
    private static final String MSG_TYPE_VIDEO = "ws-video";
    private static final String MSG_TYPE_FRIENDS = "ws-listuser";
    private static final String MSG_TYPE_SESSIONS = "ws-session";
    private static final String MSG_TYPE_SESSION_DETAILS = "ws-session-detail";

    private static final int MSG_CODE_CONFIRM_SENT = 100;
    private static final int MSG_CODE_MESSAGE_ERROR = -100;
    private static final int MSG_CODE_AUTHEN_SUCCESS = 1;
    private static final int MSG_CODE_AUTHEN_FAIL = -1;

    private static final int MSG_CODE_GET_FRIEND_SUCCESS = 15;
    private static final int MSG_CODE_GET_OLD_SESISON_SUCCESS = 20;
    private static final int MSG_CODE_GET_SESSION_DETAILS_SUCCESS = 25;


    private WebSocketFactory mFactory = null;
    private WebSocket mSocket = null;

    private Boolean mConnected = false;
    private Boolean mAuthented = false;

    public interface GetDataComplete {
        void onDataComplete(final String response);
    }

    public ChatManager(){
        mFactory = new WebSocketFactory();
        //mFactory.setConnectionTimeout(500);
        //TODO: should be use with SSL

        try {
            mSocket = mFactory.createSocket(WS_HOST_NAME);
            mSocket.addListener(this);
            mSocket.connect();
        }catch (Exception e){
            Log.d(TAG, "ChatManager: " + e.getMessage());
        }
    }

    public void SendLogin(String token, int userID) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userID);
            jo.put("to", 0);
            jo.put("session-id", "");
            jo.put("content-type", MSG_TYPE_LOGIN);
            //----------------------------------------------
            JSONObject loginObj = new JSONObject();
            loginObj.put("id", userID);
            loginObj.put("token", token);
            jo.put("content", loginObj.toString());
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }

    GetDataComplete mFriendListCallback = null;
    public void GetFriendsList( int userID, GetDataComplete callback){
        this.mFriendListCallback = callback;

        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userID);
            jo.put("to", 0);
            jo.put("session-id", "");
            jo.put("content-type", MSG_TYPE_FRIENDS);
            jo.put("content", "");
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }

    GetDataComplete mGetSessionsCallback = null;
    public void GetOldSessions( int userID, GetDataComplete callback){
        this.mGetSessionsCallback = callback;

        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userID);
            jo.put("to", 0);
            jo.put("session-id", "");
            jo.put("content-type", MSG_TYPE_SESSIONS);
            jo.put("content", "");
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }

    GetDataComplete mGetSessionDetailCallback = null;
    public void GetSessionDetails(int userID, GetDataComplete callback){
        this.mGetSessionDetailCallback = callback;

        JSONObject jo = new JSONObject();
        try {
            Log.w("sd","as"+APIHandler.Instance().user.userID);
            jo.put("from", APIHandler.Instance().user.userID);
            jo.put("to", userID);
            jo.put("session-id", "");
            jo.put("content-type", MSG_TYPE_SESSION_DETAILS);
            jo.put("content", "asc");
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }

    GetDataComplete mSendMessageComplete = null;
    public void SendMessage(int userA, int userB, String sessionID, String message,GetDataComplete callback)
    {
        this.mSendMessageComplete = callback;

        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userA);
            jo.put("to", userB);
            jo.put("session-id", sessionID);
            jo.put("content-type", MSG_TYPE_TEXT);
            jo.put("content", message);
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }

    GetDataComplete mGetMessageCallback = null;
    public void RegisterGetMessage(GetDataComplete callback)
    {
        this.mGetMessageCallback = callback;
    }

    public void SendMessage(String content){

    }
    public void UploadAndSendPhoto(int userA, int userB, String sessionID, String message,GetDataComplete callback)
    {
        this.mSendMessageComplete = callback;

        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userA);
            jo.put("to", userB);
            jo.put("session-id", sessionID);
            jo.put("content-type", MSG_TYPE_IMAGE);
            jo.put("content", message);
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }
    public void UploadAndSendVideo(int userA, int userB, String sessionID, String message,GetDataComplete callback)
    {
        this.mSendMessageComplete = callback;

        JSONObject jo = new JSONObject();
        try {
            jo.put("from", userA);
            jo.put("to", userB);
            jo.put("session-id", sessionID);
            jo.put("content-type", MSG_TYPE_VIDEO);
            jo.put("content", message);
            //-----------------------------------------
            mSocket.sendText(jo.toString());
        }catch (Exception e)
        {

        }
    }
    public void Reconnect() {
        mConnected = false;
        try {
            mSocket.recreate().connectAsynchronously();
        }catch (IOException e)
        {
            Log.d(TAG, "Reconnect: ");
        }
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
        Log.d(TAG, "onStateChanged: " + newState.toString());
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        mConnected = true;
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.d(TAG, "onConnectError: " + cause.getMessage());
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        mConnected = false;
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        Log.d(TAG, "onTextMessage: " + text);

        JSONObject msgObj = new JSONObject(text);
        if (msgObj.has("code")){
            // TODO: process code message
            int code = msgObj.getInt("code");
            switch (code) {
                case MSG_CODE_CONFIRM_SENT:{
                    if(this.mSendMessageComplete != null) {
                        this.mSendMessageComplete.onDataComplete(text);
                        this.mSendMessageComplete = null;
                    }
                    Log.d(TAG, "onTextMessage: Message was sent");
                    break;
                }
                case MSG_CODE_MESSAGE_ERROR:{
                    Log.d(TAG, "onTextMessage: Message Error");
                    break;
                }
                case MSG_CODE_AUTHEN_FAIL:{
                    Log.d(TAG, "onTextMessage: Authentication fail");
                    break;
                }
                case MSG_CODE_GET_FRIEND_SUCCESS:{ // get friend list result
                    if(this.mFriendListCallback != null) {
                        this.mFriendListCallback.onDataComplete(text);
                        this.mFriendListCallback = null;
                    }
                    Log.d(TAG, "onTextMessage:Get user success");
                    break;
                }
                case MSG_CODE_GET_OLD_SESISON_SUCCESS:{ // get sessions list result
                    if(this.mGetSessionsCallback != null) {
                        this.mGetSessionsCallback.onDataComplete(text);
                        this.mGetSessionsCallback = null;
                    }
                    break;
                }
                case MSG_CODE_GET_SESSION_DETAILS_SUCCESS:{ // get sessions list result
                    if(this.mGetSessionDetailCallback != null) {
                        this.mGetSessionDetailCallback.onDataComplete(text);
                        this.mGetSessionDetailCallback = null;
                    }
                    break;
                }
                case MSG_CODE_AUTHEN_SUCCESS:{
                    this.mAuthented = true;
                    Log.d(TAG, "onTextMessage: Authentication Success");
                    break;
                }
            }
        }else{
            /*
            JSONObject msgObj = new JSONObject(text);
            String sessionID = msgObj.getString("session-id");
            int from = msgObj.getInt("from");
            int to = msgObj.getInt("to");
            String contentType = msgObj.getString("content-type");
            String content = msgObj.getString("content");
             */
            //------------------------------------------------------
            // update message here
            if (this.mGetMessageCallback != null)
            {
                this.mGetMessageCallback.onDataComplete(text);
            }
        }
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        Log.d(TAG, "onError: " + cause.getMessage());
    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

    }
}
