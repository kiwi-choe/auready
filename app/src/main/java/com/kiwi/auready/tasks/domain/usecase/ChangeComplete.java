package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Set completed of a task and Edit description of tasks
 */
public class ChangeComplete extends UseCase<ChangeComplete.RequestValues, ChangeComplete.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public ChangeComplete(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.changeComplete(values.getMemberId(), values.getTaskId(), values.getEditingTasks(),
                new TaskDataSource.ChangeCompleteTaskCallback() {

                    @Override
                    public void onChangeCompleteSuccess(List<Task> tasksOfMember) {
                        getUseCaseCallback().onSuccess(new ResponseValue(tasksOfMember));
                    }

                    @Override
                    public void onChangeCompleteFail() {
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
