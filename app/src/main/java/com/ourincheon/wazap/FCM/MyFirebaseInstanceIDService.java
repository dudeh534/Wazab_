package com.ourincheon.wazap.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by YS on 2016-06-24.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    String TAG = "MyFirebaseInstanceIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String str) {
        Log.d(TAG, "센드 실행");
    }
}
