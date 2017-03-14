package com.kiwi.auready_ver2.notification;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.notification.INotificationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of Notification service
 */

public class NotificationService {

    private static final String TAG = "Tag_NotificationService";

    private static NotificationService INSTANCE = null;

    private final String mAccessToken;

    public static NotificationService getInstance(@NonNull String accessToken) {
        if(INSTANCE == null) {
            INSTANCE = new NotificationService(
                    checkNotNull(accessToken, "accessToken is null"));
        }
        return INSTANCE;
    }
    private NotificationService(@NonNull String accessToken) {
        mAccessToken = accessToken;
    }

    public void sendRegistrationToServer(String token) {
        INotificationService service = ServiceGenerator.createService(INotificationService.class, mAccessToken);
        Call<Void> call = service.sendRegistration(token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "success to send instanceID");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "fail to send instanceID");
            }
        });
    }
}
