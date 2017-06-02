package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delete a task
 */
public class DeleteTask extends UseCase<DeleteTask.RequestValues, DeleteTask.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public DeleteTask(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    public DeleteTask(@NonNull DeleteTask deleteTask) {
        mTaskRepository = deleteTask.mTaskRepository;
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.deleteTask(values.getMemberId(), values.getTaskId(), values.getEditingTasks(),
                new TaskDataSource.DeleteTaskCallback() {

                    @Override
                    public void onDeleteSuccess(List<Task> tasksOfMember) {
                        getUseCaseCallback().onSuccess(new ResponseValue(tasksOfMember));
                    }

                    @Override
                    public void onDeleteFailed() {
                        getUseCaseCallback().onError();
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mMemberId;
        private final String mTaskId;
        private final List<Task> mEditingTasks;

        public RequestValues(@NonNull String memberId, @NonNull String taskId, List<Task> editingTasks) {
            mMemberId = memberId;
            mTaskId = checkNotNull(taskId, "taskId cannot be null");
            mEditingTasks = editingTasks;
        }

        public String getMemberId() {
            return mMemberId;
        }

        public String getTaskId() {
            return mTaskId;
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
