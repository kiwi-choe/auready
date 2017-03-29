package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit taskHeadDetail - taskHead and members
 */
public class EditTaskHeadDetail extends UseCase<EditTaskHeadDetail.RequestValues, EditTaskHeadDetail.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public EditTaskHeadDetail(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskRepository.editTaskHeadDetailInRepo(requestValues.getTaskHeadDetail(), new TaskDataSource.EditTaskHeadDetailCallback() {
            @Override
            public void onEditSuccess() {

                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onEditFailed() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {

        private TaskHeadDetail mTaskHeadDetail;

        public RequestValues(@NonNull TaskHeadDetail taskHeadDetail) {
            mTaskHeadDetail = checkNotNull(taskHeadDetail);
        }

        public TaskHeadDetail getTaskHeadDetail() {
            return mTaskHeadDetail;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {    }

}
