package com.kiwi.auready_ver2.data.source.local;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Notification;

/**
 * Local Data Source of Notification
 */

public class NotificationLocalDataSource implements NotificationDataSource {

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {

    }

    @Override
    public void getNotifications(@NonNull LoadNotificationsCallback callback) {

    }

    @Override
    public void editNotification(@NonNull Notification notification) {

    }

    @Override
    public void deleteNotification(@NonNull int id) {

    }
}
