package com.kiwi.auready.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.TaskHead;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Update taskHeads orders
 */
public class UpdateTaskHeadOrders extends UseCase<UpdateTaskHeadOrders.RequestValues, UpdateTaskHeadOrders.ResponseValue> {

    private final TaskRepository mRepository;

    public UpdateTaskHeadOrders(@NonNull TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        List<TaskHead> taskHeads = requestValues.getTaskHeads();
        mRepository.updateTaskHeadOrders(taskHeads, new TaskDataSource.UpdateTaskHeadOrdersCallback() {
            @Override
            public void onUpdateSuccess() {

                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onUpdateFailed() {
                getUseCaseCallback().onError();
            }
        });
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
