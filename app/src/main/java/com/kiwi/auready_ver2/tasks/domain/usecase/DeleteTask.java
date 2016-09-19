package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 9/19/16.
 */
public class DeleteTask extends UseCase<DeleteTask.RequestValues, DeleteTask.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public DeleteTask(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskRepository.deleteTask(requestValues.getTask());
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
