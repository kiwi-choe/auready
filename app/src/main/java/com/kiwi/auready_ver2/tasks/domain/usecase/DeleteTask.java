package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
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
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.deleteTask(values.getId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mId;

        public RequestValues(@NonNull String id) {
            mId = checkNotNull(id);
        }

        public String getId() {
            return mId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
