package com.kiwi.auready.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Notification;
import com.kiwi.auready.data.source.NotificationDataSource;
import com.kiwi.auready.data.source.NotificationRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get Notifications
 */
public class GetNotifications extends UseCase<GetNotifications.RequestValues, GetNotifications.ResponseValue> {

    private final NotificationRepository mRepository;

    public GetNotifications(@NonNull NotificationRepository repository) {
        mRepository = checkNotNull(repository, "repository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.loadNotifications(new NotificationDataSource.LoadNotificationsCallback() {
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
