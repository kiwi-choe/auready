package com.kiwi.auready.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Notification;
import com.kiwi.auready.data.source.NotificationRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Update Notification (isNew to false)
 */
public class ReadNotification extends UseCase<ReadNotification.RequestValues, ReadNotification.ResponseValue> {

    private final NotificationRepository mRepository;

    public ReadNotification(@NonNull NotificationRepository repository) {
        mRepository = checkNotNull(repository, "repository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
    }

    public static class RequestValues implements UseCase.RequestValues {

        private final Notification mNotification;

        public RequestValues(@NonNull Notification notification) {
            mNotification = checkNotNull(notification, "notification cannot be null");
        }

        public Notification getNotification() {
            return mNotification;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {
    }

}
