package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Notification;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Notification Repository
 */

public class NotificationRepository implements NotificationDataSource {

    private static NotificationRepository INSTANCE = null;

    private NotificationDataSource mLocalDataSource;
    private NotificationDataSource mRemoteDataSource;

    private NotificationRepository(@NonNull NotificationDataSource remoteDataSource,
                                   @NonNull NotificationDataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    public static NotificationRepository getInstance(@NonNull NotificationDataSource remoteDataSource,
                                                     @NonNull NotificationDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new NotificationRepository(checkNotNull(remoteDataSource), checkNotNull(localDataSource));
        }
        return INSTANCE;
    }

    /*
    * Used to force {@link #getInstance} to create a new instance
    * next time it's called.
    * */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {

    }

    @Override
    public void loadNotifications(@NonNull final LoadNotificationsCallback callback) {
        mLocalDataSource.loadNotifications(new LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                callback.onLoaded(notifications);
            }

            @Override
            public void onDataNotAvailable() {
                loadNotificationsFromRemote(callback);
            }
        });
    }

    private void loadNotificationsFromRemote(final LoadNotificationsCallback callback) {
        mRemoteDataSource.loadNotifications(new LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                refreshLocalDataSource(notifications);
                callback.onLoaded(notifications);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<Notification> notifications) {
        for(Notification notification:notifications) {
            mLocalDataSource.saveNotification(notification, new SaveCallback() {
                @Override
                public void onSaveSuccess() {

                }

                @Override
                public void onSaveFailed() {

                }
            });
        }
    }

    @Override
    public void readNotification(@NonNull int id) {

    }

    @Override
    public void deleteNotification(@NonNull int id) {
        checkNotNull(id);
        mLocalDataSource.deleteNotification(id);
    }

    @Override
    public void getNotificationsCount(@NonNull final GetCountCallback callback) {
        mLocalDataSource.getNotificationsCount(new GetCountCallback() {
            @Override
            public void onSuccessGetCount(int newNotificationCount) {
                callback.onSuccessGetCount(newNotificationCount);
            }

            @Override
            public void onDataNotAvailable() {
                getNotificationsCountFromRemote(callback);
            }
        });
    }

    private void getNotificationsCountFromRemote(final GetCountCallback callback) {
        mRemoteDataSource.loadNotifications(new LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                refreshLocalDataSource(notifications);

                int notificationsCount = notifications.size();
                callback.onSuccessGetCount(notificationsCount);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }
}
