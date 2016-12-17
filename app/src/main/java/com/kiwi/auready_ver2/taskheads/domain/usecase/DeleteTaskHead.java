package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes a TaskHead from the TaskHeadRepository.
 * Before delete a taskHead, delete tasks of this taskHead
 */
public class DeleteTaskHead extends UseCase<DeleteTaskHead.RequestValues, DeleteTaskHead.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;
    private final TaskRepository mTaskRepository;

    public DeleteTaskHead(@NonNull TaskHeadRepository taskHeadRepository, @NonNull TaskRepository taskRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository);
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final String taskHeadId = requestValues.getTaskHeadId();
        mTaskRepository.deleteTasks(taskHeadId);
        mTaskHeadRepository.deleteTaskHead(taskHeadId);

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;

        public RequestValues(@NonNull String taskHeadId) {
            mTaskHeadId = checkNotNull(taskHeadId, "taskHeadId cannot be null");
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
