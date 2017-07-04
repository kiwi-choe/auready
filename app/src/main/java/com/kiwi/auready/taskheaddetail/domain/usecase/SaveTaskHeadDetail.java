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
 * Save taskHeadDetail ({@link TaskHead} and {@link Member})
 */
public class SaveTaskHeadDetail extends UseCase<SaveTaskHeadDetail.RequestValues, SaveTaskHeadDetail.ResponseValue> {

    private final TaskRepository mRepository;

    public SaveTaskHeadDetail(@NonNull TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.saveTaskHeadDetail(requestValues.getTaskHeadDetail(), new TaskDataSource.SaveCallback() {

            @Override
            public void onSaveSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onSaveFailed() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static class RequestValues implements UseCase.RequestValues {
        private final TaskHeadDetail mTaskHeadDetail;

        public RequestValues(@NonNull TaskHeadDetail taskHeadDetail) {
            mTaskHeadDetail = checkNotNull(taskHeadDetail, "taskHeadDetail cannot be null");
        }

        public TaskHeadDetail getTaskHeadDetail() {
            return mTaskHeadDetail;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {
    }

}
