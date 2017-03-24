package com.kiwi.auready_ver2.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.local.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Save Notification
 */
public class SaveNotification extends UseCase<SaveNotification.RequestValues, SaveNotification.ResponseValue> {

    private final NotificationLocalDataSource mLocalDataSource;

    public SaveNotification(@NonNull NotificationLocalDataSource localDataSource) {
        mLocalDataSource = checkNotNull(localDataSource, "localDataSource cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mLocalDataSource.saveNotification(requestValues.getNotification(), new NotificationDataSource.SaveCallback() {
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
