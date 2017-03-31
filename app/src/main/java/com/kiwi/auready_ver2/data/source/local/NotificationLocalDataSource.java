package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Notification;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_CONTENTS;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_TYPE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.COLUMN_iSNEW;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.NotificationEntry.TABLE_NAME;

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
        if (INSTANCE == null) {
            INSTANCE = new NotificationLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {
        checkNotNull(notification);
        checkNotNull(callback);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, notification.getType());
        values.put(COLUMN_iSNEW, notification.isNew());
        values.put(COLUMN_CONTENTS, notification.getContents());

        long isSuccess = mDbHelper.insert(TABLE_NAME, null, values);
        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void loadNotifications(@NonNull LoadNotificationsCallback callback) {

        List<Notification> notifications = new ArrayList<>();

        Cursor c = mDbHelper.query(TABLE_NAME, null, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                int type = c.getType(c.getColumnIndexOrThrow(COLUMN_TYPE));
                int isNew = c.getInt(c.getColumnIndexOrThrow(COLUMN_iSNEW));
                String contents = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTENTS));

                Notification notification = new Notification(id, type, isNew, contents);
                notifications.add(notification);
            }
        }
        if (c != null) {
            c.close();
        }

        if (notifications.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onLoaded(notifications);
        }
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
