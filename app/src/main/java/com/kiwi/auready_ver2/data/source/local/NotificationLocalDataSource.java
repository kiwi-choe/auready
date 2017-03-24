package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Notification;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Local Data Source of Notification
 */

public class NotificationLocalDataSource implements NotificationDataSource {

    private static NotificationLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;

    private NotificationLocalDataSource(Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static NotificationLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if(INSTANCE == null) {
            INSTANCE = new NotificationLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {

    }

    @Override
    public void loadNotifications(@NonNull LoadNotificationsCallback callback) {

    }

    @Override
    public void readNotification() {

    }

    @Override
    public void deleteNotification(@NonNull int id) {

    }

    @Override
    public void getNewNotificationsCount(@NonNull GetNewCountCallback callback) {

    }
}
