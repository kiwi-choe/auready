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

    void getNotifications(@NonNull LoadNotificationsCallback callback);

    void editNotification(@NonNull Notification notification);

    void deleteNotification(@NonNull int id);
}
