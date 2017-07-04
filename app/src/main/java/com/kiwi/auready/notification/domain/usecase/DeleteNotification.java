package com.kiwi.auready.notification.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.source.NotificationRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delete Notification
 */
public class DeleteNotification extends UseCase<DeleteNotification.RequestValues, DeleteNotification.ResponseValue> {

    private final NotificationRepository mRepository;

    public DeleteNotification(@NonNull NotificationRepository repository) {
        mRepository = checkNotNull(repository, "repository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.deleteNotification(requestValues.getId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues {

        private final int mId;

        public RequestValues(int id) {
            mId = id;
        }

        public int getId() {
            return mId;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue { }

}
