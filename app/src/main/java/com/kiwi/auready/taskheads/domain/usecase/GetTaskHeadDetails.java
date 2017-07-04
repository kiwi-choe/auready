package com.kiwi.auready.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.TaskHeadDetail;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of taskHeadDetail.
 */
public class GetTaskHeadDetails extends UseCase<GetTaskHeadDetails.RequestValues, GetTaskHeadDetails.ResponseValue> {


    private final TaskRepository mTaskRepository;

    public GetTaskHeadDetails(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

        if(values.isForceUpdate()) {
            mTaskRepository.forceUpdateLocalTaskHeadDetails();
        }

        mTaskRepository.getTaskHeadDetails(new TaskDataSource.LoadTaskHeadDetailsCallback() {
            @Override
            public void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails) {
                ResponseValue responseValue = new ResponseValue(taskHeadDetails);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final boolean mForceUpdate;
        public RequestValues(boolean forceUpdate) {
            mForceUpdate = forceUpdate;
        }

        boolean isForceUpdate() {
            return mForceUpdate;
        }
    }

    public class ResponseValue implements UseCase.ResponseValue {

        private final List<TaskHeadDetail> mTaskHeadDetails;

        public ResponseValue(@NonNull List<TaskHeadDetail> taskHeadDetails) {
            mTaskHeadDetails = checkNotNull(taskHeadDetails, "taskHeads cannot be null");
        }

        public List<TaskHeadDetail> getTaskHeadDetails() {
            return mTaskHeadDetails;
        }
    }
}
