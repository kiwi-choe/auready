package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a task as completed.
 */
public class CompleteTask extends UseCase<CompleteTask.RequestValues, CompleteTask.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public CompleteTask(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Task activeTask = requestValues.getTask();
        mTaskRepository.completeTask(activeTask);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Task mTask;

        public RequestValues(@NonNull Task task) {
            mTask = checkNotNull(task, "task cannot be null");
        }

        public Task getTask() {
            return mTask;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
