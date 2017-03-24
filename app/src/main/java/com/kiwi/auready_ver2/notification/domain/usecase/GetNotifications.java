package com.kiwi.auready_ver2.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.local.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get Notifications
 */
public class GetNotifications extends UseCase<GetNotifications.RequestValues, GetNotifications.ResponseValue> {

    private final NotificationLocalDataSource mLocalDataSource;

    public GetNotifications(@NonNull NotificationLocalDataSource localDataSource) {
        mLocalDataSource = checkNotNull(localDataSource, "localDataSource cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mLocalDataSource.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
            @Override
            public void onLoaded(List<Notification> notifications) {
                ResponseValue responseValue = new ResponseValue(notifications);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {    }

    public static class ResponseValue implements UseCase.ResponseValue {
        private final List<Notification> mNotifications;

        public ResponseValue(@NonNull List<Notification> notifications) {
            mNotifications = checkNotNull(notifications, "notifications cannot be null");
        }

        public List<Notification> getNotifications() {
            return mNotifications;
        }
    }

}
