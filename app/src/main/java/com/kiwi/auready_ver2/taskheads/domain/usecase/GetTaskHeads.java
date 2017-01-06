package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of taskHead.
 */
public class GetTaskHeads extends UseCase<GetTaskHeads.RequestValues, GetTaskHeads.ResponseValue> {


    private final TaskRepository mTaskRepository;

    public GetTaskHeads(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

        mTaskRepository.getTaskHeads(new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                ResponseValue responseValue = new ResponseValue(taskHeads);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {    }

    public class ResponseValue implements UseCase.ResponseValue {

        private final List<TaskHead> mTaskHeads;


        public ResponseValue(@NonNull List<TaskHead> taskHeads) {
            mTaskHeads = checkNotNull(taskHeads, "taskHeads cannot be null");
        }

        public List<TaskHead> getTaskHeads() {
            return mTaskHeads;
        }
    }
}
