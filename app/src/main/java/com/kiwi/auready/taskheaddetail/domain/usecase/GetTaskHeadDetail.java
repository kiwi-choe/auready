package com.kiwi.auready.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.TaskHead;
import com.kiwi.auready.data.TaskHeadDetail;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves taskHeadDetail(a {@link TaskHead} and a {@link Member}) from the {@link TaskRepository}.
 */

public class GetTaskHeadDetail extends UseCase<GetTaskHeadDetail.RequestValues, GetTaskHeadDetail.ResponseValue> {
    private final TaskRepository mRepository;

    public GetTaskHeadDetail(TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        if(requestValues.isForceUpdate()) {
            mRepository.forceUpdateLocalATaskHeadDetail();
        }

        mRepository.getTaskHeadDetail(requestValues.getTaskHeadId(), new TaskDataSource.GetTaskHeadDetailCallback() {
            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                ResponseValue responseValue = new ResponseValue(taskHeadDetail);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;
        private final boolean mForceUpdate;

        public RequestValues(String taskHeadId, boolean forceUpdate) {
            mTaskHeadId = checkNotNull(taskHeadId, "taskHeadId cannot be null");
            mForceUpdate = forceUpdate;
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {
        private TaskHeadDetail mTaskHeadDetail;

        public ResponseValue(@NonNull TaskHeadDetail taskHeadDetail) {
            mTaskHeadDetail = checkNotNull(taskHeadDetail, "taskHeadDetail cannot be null");
        }

        public TaskHeadDetail getTaskHeadDetail() {
            return mTaskHeadDetail;
        }
    }
}