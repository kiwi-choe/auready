package com.kiwi.auready_ver2.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.local.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get number of new notifications
 */
public class GetNewNotificationsCount extends UseCase<GetNewNotificationsCount.RequestValues, GetNewNotificationsCount.ResponseValue> {

    private final NotificationLocalDataSource mLocalDataSource;

    public GetNewNotificationsCount(@NonNull NotificationLocalDataSource localDataSource) {
        mLocalDataSource = checkNotNull(localDataSource, "localDataSource cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mLocalDataSource.getNewNotificationsCount(new NotificationDataSource.GetNewCountCallback() {
            @Override
            public void onLoaded(int newNotificationCount) {
                getUseCaseCallback().onSuccess(new ResponseValue(newNotificationCount));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {    }

    public static class ResponseValue implements UseCase.ResponseValue {
        private final int mNewNotificationsCount;

        public ResponseValue(@NonNull int newNotificationsCount) {
            mNewNotificationsCount = checkNotNull(newNotificationsCount, "newNotificationCount cannot be null");
        }

        public int getNewCount() {
            return mNewNotificationsCount;
        }
    }

}
