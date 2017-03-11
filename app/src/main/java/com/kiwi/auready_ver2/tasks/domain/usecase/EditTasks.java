package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit tasks
 */
public class EditTasks extends UseCase<EditTasks.RequestValues, EditTasks.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public EditTasks(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    public EditTasks(@NonNull EditTasks editTasks) {
        mTaskRepository = editTasks.mTaskRepository;
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.editTasks(values.getTasks());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final List<Task> mTasks;

        public RequestValues(@NonNull List<Task> tasks) {
            mTasks = checkNotNull(tasks, "tasks cannot be null");
        }

        public List<Task> getTasks() {
            return mTasks;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
