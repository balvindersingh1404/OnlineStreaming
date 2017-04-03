package com.headsupseven.corp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LockScreenService extends Service {
    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
