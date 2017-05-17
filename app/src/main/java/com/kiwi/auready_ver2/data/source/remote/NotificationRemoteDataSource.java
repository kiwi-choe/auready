package com.kiwi.auready_ver2.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.HttpStatusCode;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.notification.INotificationService;
import com.kiwi.auready_ver2.rest_service.notification.PendingRequestList;
import com.kiwi.auready_ver2.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Notification Remote data source
 */

public class NotificationRemoteDataSource implements NotificationDataSource {

    private static final String TAG = "Tag_NotiRemoteData";

    private static NotificationRemoteDataSource INSTANCE;
    private Context mContext;
    private final AccessTokenStore mAccessTokenStore;
    private String mAccessToken;

    private NotificationRemoteDataSource(@NonNull Context context) {
        mContext = context.getApplicationContext();
        mAccessTokenStore = AccessTokenStore.getInstance(context);
    }

    public static NotificationRemoteDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new NotificationRemoteDataSource(context);
        }
        return INSTANCE;
    }


    private boolean readyToRequestAPI() {
        // Check network
        if (!NetworkUtils.isOnline(mContext)) {
            return false;
        }

        // Check accessToken
        mAccessToken = mAccessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
        if (TextUtils.isEmpty(mAccessToken)) {
            Log.d(TAG, "no accessToken");
            return false;
        }
        return true;
    }

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {

    }

    @Override
    public void loadNotifications(@NonNull final LoadNotificationsCallback callback) {
        if(!readyToRequestAPI()) {
            callback.onDataNotAvailable();
        }

        INotificationService notificationService =
                ServiceGenerator.createService(INotificationService.class, mAccessToken);

        Call<PendingRequestList> call = notificationService.getFriendRequestPending();
        call.enqueue(new Callback<PendingRequestList>() {
            @Override
            public void onResponse(Call<PendingRequestList> call, Response<PendingRequestList> response) {
                if(response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    List<Notification> notifications = makeNotifications(response.body().getFromUsers());
                    callback.onLoaded(notifications);
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<PendingRequestList> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }

    private List<Notification> makeNotifications(List<Friend> fromUsers) {
        List<Notification> notifications = new ArrayList<>();
        for(Friend friend:fromUsers) {
            Notification notification = new Notification(
                    Notification.TYPES.friend_request.name(),
                    friend.getUserId(),
                    friend.getName(), "");
            notifications.add(notification);
        }
        return notifications;
    }

    @Override
    public void readNotification(@NonNull int id) {

    }

    @Override
    public void deleteNotification(@NonNull int id) {

    }

    @Override
    public void getNotificationsCount(@NonNull GetCountCallback callback) {

    }
}

