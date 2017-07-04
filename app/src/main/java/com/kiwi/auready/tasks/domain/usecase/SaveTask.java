package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Save a Task
 */
public class SaveTask extends UseCase<SaveTask.RequestValues, SaveTask.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public SaveTask(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository cannot be null");
    }

    public SaveTask(@NonNull SaveTask saveTask) {
        mTaskRepository = saveTask.mTaskRepository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValue) {
        mTaskRepository.saveTask(requestValue.getTask(), requestValue.getEditingTasks(), new TaskDataSource.SaveTaskCallback() {

            @Override
            public void onSaveSuccess(List<Task> tasksOfMember) {
                getUseCaseCallback().onSuccess(new ResponseValue(tasksOfMember));
            }

            @Override
            public void onSaveFailed() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Task mTask;
        private final List<Task> mEditingTasks;

        public RequestValues(@NonNull Task task, List<Task> editingTasks) {
            mTask = checkNotNull(task, "task cannot be null");
            mEditingTasks = editingTasks;
        }

        public Task getTask() {
            return mTask;
        }

        public List<Task> getEditingTasks() {
            return mEditingTasks;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final List<Task> mTasksOfMember;

        public ResponseValue(List<Task> tasksOfMember) {
            mTasksOfMember = tasksOfMember;
        }

        public List<Task> getTasksOfMember() {
            return mTasksOfMember;
        }
    }
}
