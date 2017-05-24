package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Set completed of a task
 */
public class ChangeComplete extends UseCase<ChangeComplete.RequestValues, ChangeComplete.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public ChangeComplete(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.changeComplete(values.getEditedTask());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Task mEditedTask;

        public RequestValues(@NonNull Task editedTask) {
            mEditedTask = checkNotNull(editedTask);
        }

        public Task getEditedTask() {
            return mEditedTask;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
