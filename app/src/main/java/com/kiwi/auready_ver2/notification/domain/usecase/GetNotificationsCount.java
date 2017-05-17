package com.kiwi.auready_ver2.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.NotificationRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get number of notifications
 */
public class GetNotificationsCount extends UseCase<GetNotificationsCount.RequestValues, GetNotificationsCount.ResponseValue> {

    private final NotificationRepository mRepository;

    public GetNotificationsCount(@NonNull NotificationRepository repository) {
        mRepository = checkNotNull(repository, "repository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.getNotificationsCount(new NotificationDataSource.GetCountCallback() {
            @Override
            public void onSuccessGetCount(int notificationsCount) {
                getUseCaseCallback().onSuccess(new ResponseValue(notificationsCount));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {    }

    public static class ResponseValue implements UseCase.ResponseValue {
        private final int mNotificationsCount;

        public ResponseValue(@NonNull int notificationsCount) {
            mNotificationsCount = checkNotNull(notificationsCount, "notificationCount cannot be null");
        }

        public int getNotificationsCount() {
            return mNotificationsCount;
        }
    }

}
