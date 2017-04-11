package com.kiwi.auready_ver2.notification;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.notification.INotificationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of Notification service
 */

public class NotificationService extends FirebaseInstanceIdService {

    private static final String TAG = "Tag_NotificationService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     * <p>
     * if this client is logged in the app server, doing registration.
     *
     * @param token The new token.
     */
    public static void sendRegistrationToServer(String token) {
        // condition 1
        if (token == null) {
            Log.d(TAG, "instanceId is null");
            return;
        }
        // condition 2
        // AccessTokenStore should be instantiated before this method.
        String accessToken = AccessTokenStore.getInstance().getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
        Log.d(TAG, "accessToken - " + accessToken);
        if (!TextUtils.isEmpty(accessToken)) {
            Log.d(TAG, "entered into MyFirebaseInstanceIDService");
            sendRegistrationToServer(token, accessToken);
        }
    }

    private static void sendRegistrationToServer(String instanceId, @NonNull String accessToken) {

        INotificationService service = ServiceGenerator.createService(INotificationService.class, accessToken);
        Call<Void> call = service.sendRegistration(instanceId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "success to send instanceID");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "fail to send instanceID, ", t);
            }
        });
    }
}
