package com.kiwi.auready.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Notification;
import com.kiwi.auready.data.source.NotificationDataSource;
import com.kiwi.auready.data.source.NotificationRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Save Notification
 */
public class SaveNotification extends UseCase<SaveNotification.RequestValues, SaveNotification.ResponseValue> {

    private final NotificationRepository mRepository;

    public SaveNotification(@NonNull NotificationRepository repository) {
        mRepository = checkNotNull(repository, "localDataSource cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.saveNotification(requestValues.getNotification(), new NotificationDataSource.SaveCallback() {
            @Override
            public void onSaveSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onSaveFailed() {
                getUseCaseCallback().onError();
            }
        });
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
