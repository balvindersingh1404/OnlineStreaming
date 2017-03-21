package com.headsupseven.corp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.headsupseven.corp.utils.PersistentUser;


/**
 * Created by Prosanto on 8/24/16.
 */
public class TokenService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            PersistentUser.SetPushkey(getApplicationContext(),refreshedToken);
            sendRegistrationToServer(refreshedToken);
            Log.w("refreshedToken", "are: "+refreshedToken);

        } catch (Exception e) {
            Log.w("Exception", "are: "+e.getMessage());

            e.printStackTrace();
        }
    }
    private void sendRegistrationToServer(String token) {
    }
}
