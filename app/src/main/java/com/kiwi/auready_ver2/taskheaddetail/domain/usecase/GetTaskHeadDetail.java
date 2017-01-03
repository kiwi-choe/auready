package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves taskHeadDetail(a {@link TaskHead} and a {@link Member}) from the {@link TaskHeadDetailRepository}.
 */

public class GetTaskHeadDetail extends UseCase<GetTaskHeadDetail.RequestValues, GetTaskHeadDetail.ResponseValue> {
    private final TaskHeadDetailRepository mTaskHeadDetailRepository;

    public GetTaskHeadDetail(TaskHeadDetailRepository taskHeadDetailRepository) {
        mTaskHeadDetailRepository = checkNotNull(taskHeadDetailRepository, "taskHeadDetailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadDetailRepository.getTaskHeadDetail(requestValues.getTaskHeadId(), new TaskHeadDetailDataSource.GetTaskHeadDetailCallback() {
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

        public RequestValues(String taskHeadId) {
            mTaskHeadId = checkNotNull(taskHeadId, "taskHeadId cannot be null");
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
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