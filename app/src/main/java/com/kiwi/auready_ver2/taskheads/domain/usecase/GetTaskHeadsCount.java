package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Get count of taskHeads
 */
public class GetTaskHeadsCount extends UseCase<GetTaskHeadsCount.RequestValues, GetTaskHeadsCount.ResponseValue> {


    private final TaskHeadRepository mTaskHeadRepository;

    public GetTaskHeadsCount(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

//        int taskHeadsCount = mTaskHeadRepository.getTaskHeadsCount();
//        ResponseValue responseValue = new ResponseValue(taskHeadsCount);
//        getUseCaseCallback().onSuccess(responseValue);
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
