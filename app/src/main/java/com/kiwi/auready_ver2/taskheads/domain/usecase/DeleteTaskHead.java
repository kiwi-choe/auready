package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes a TaskHead from the TaskHeadRepository.
 */
public class DeleteTaskHead extends UseCase<DeleteTaskHead.RequestValues, DeleteTaskHead.ResponseValue>{

    private final TaskHeadRepository mTaskHeadRepository;

    public DeleteTaskHead(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadRepository.deleteTaskHead(requestValues.getTaskHeadId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;

        public RequestValues(@NonNull String taskHeadId) {
            mTaskHeadId = checkNotNull(taskHeadId, "taskHeadId cannot be null");
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {}
}
