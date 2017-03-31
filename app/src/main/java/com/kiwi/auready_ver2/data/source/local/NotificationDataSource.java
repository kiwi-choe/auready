package com.kiwi.auready_ver2.data.source.local;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Notification;

import java.util.List;

/**
 * entry point for accessing notification data.
 */
public interface NotificationDataSource {

    interface SaveCallback {
        void onSaveSuccess();
        void onSaveFailed();
    }

    void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback);

    interface LoadNotificationsCallback {
        void onLoaded(List<Notification> notifications);
        void onDataNotAvailable();
    }

    void loadNotifications(@NonNull LoadNotificationsCallback callback);

    void readNotification(@NonNull int id);

    void deleteNotification(@NonNull int id);

    interface GetNewCountCallback {
        void onLoaded(int newNotificationCount);
        void onDataNotAvailable();
    }
    void getNewNotificationsCount(@NonNull GetNewCountCallback callback);
}
