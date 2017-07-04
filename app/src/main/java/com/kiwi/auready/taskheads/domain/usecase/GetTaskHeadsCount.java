package com.kiwi.auready.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get count of taskHeads
 */
public class GetTaskHeadsCount extends UseCase<GetTaskHeadsCount.RequestValues, GetTaskHeadsCount.ResponseValue> {


    private final TaskRepository mRepository;

    public GetTaskHeadsCount(@NonNull TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

        int taskHeadsCount = mRepository.getTaskHeadsCount();
        ResponseValue responseValue = new ResponseValue(taskHeadsCount);
        getUseCaseCallback().onSuccess(responseValue);
    }

    public static class RequestValues implements UseCase.RequestValues { }

    public class ResponseValue implements UseCase.ResponseValue {

        private final int mTaskHeadsCount;

        public ResponseValue(@NonNull int taskHeadsCount) {
            mTaskHeadsCount = checkNotNull(taskHeadsCount);
        }

        public int getTaskHeadsCount() {
            return mTaskHeadsCount;
        }
    }
}
