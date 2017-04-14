package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes taskHeads from the TaskRepository.
 */
public class DeleteTaskHeads extends UseCase<DeleteTaskHeads.RequestValues, DeleteTaskHeads.ResponseValue> {

    private final TaskRepository mRepository;

    public DeleteTaskHeads(@NonNull TaskRepository taskRepository) {
        mRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mRepository.deleteTaskHeads(requestValues.getTaskHeadIds(), new TaskDataSource.DeleteTaskHeadsCallback() {
            @Override
            public void onDeleteSuccess() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onDeleteFail() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final List<String> mTaskHeadIds;

        public RequestValues(@NonNull List<String> taskHeadIds) {
            mTaskHeadIds = checkNotNull(taskHeadIds, "taskHeadIds cannot be null");
        }

        public List<String> getTaskHeadIds() {
            return mTaskHeadIds;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
