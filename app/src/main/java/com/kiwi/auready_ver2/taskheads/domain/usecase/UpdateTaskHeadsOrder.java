package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class UpdateTaskHeadsOrder extends UseCase<UpdateTaskHeadsOrder.RequestValues, UpdateTaskHeadsOrder.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;

    public UpdateTaskHeadsOrder(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        List<TaskHead> taskHeads = requestValues.getTaskHeads();
        mTaskHeadRepository.updateTaskHeads(taskHeads);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final List<TaskHead> mTaskHeads;

        public RequestValues(@NonNull List<TaskHead> taskHeads) {
            mTaskHeads = checkNotNull(taskHeads, "taskHeads cannot be null");
        }

        public List<TaskHead> getTaskHeads() {
            return mTaskHeads;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {    }

}
