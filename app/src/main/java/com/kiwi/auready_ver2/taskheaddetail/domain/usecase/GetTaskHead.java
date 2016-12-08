package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves a {@link TaskHead} from the {@link TaskHeadRepository}.
 */

public class GetTaskHead extends UseCase<GetTaskHead.RequestValues, GetTaskHead.ResponseValue> {
    private final TaskHeadRepository mTaskHeadRepository;

    public GetTaskHead(TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadRepository.getTaskHead(requestValues.getTaskHeadId(), new TaskHeadDataSource.GetTaskHeadCallback() {

            @Override
            public void onTaskHeadLoaded(TaskHead taskHead) {
                ResponseValue responseValue = new ResponseValue(taskHead);
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
        private TaskHead mTaskHead;

        public ResponseValue(@NonNull TaskHead taskHead) {
            mTaskHead = checkNotNull(taskHead, "taskHead cannot be null");
        }

        public TaskHead getTaskHead() {
            return mTaskHead;
        }
    }
}