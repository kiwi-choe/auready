package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delete tasks
 */
public class DeleteTasks extends UseCase<DeleteTasks.RequestValues, DeleteTasks.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public DeleteTasks(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    public DeleteTasks(@NonNull DeleteTasks deleteTasks) {
        mTaskRepository = deleteTasks.mTaskRepository;
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.deleteTasks(values.getTaskIds());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final List<String> mTaskIds;

        public RequestValues(@NonNull List<String> taskIds) {
            mTaskIds = checkNotNull(taskIds, "taskIds cannot be null");
        }

        public List<String> getTaskIds() {
            return mTaskIds;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
