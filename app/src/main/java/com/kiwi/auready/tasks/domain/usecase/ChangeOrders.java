package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit order and description of tasks
 */
public class ChangeOrders extends UseCase<ChangeOrders.RequestValues, ChangeOrders.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public ChangeOrders(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.changeOrders(values.getMemberId(), values.getEditingTasks(),
                new TaskDataSource.ChangeOrdersCallback() {
                    @Override
                    public void onChangeOrdersSuccess(List<Task> tasksOfMember) {
                        getUseCaseCallback().onSuccess(new ResponseValue(tasksOfMember));
                    }

                    @Override
                    public void onChangeOrdersFail() {
                        getUseCaseCallback().onError();
                    }
                });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mMemberId;
        private final List<Task> mEditingTasks;

        public RequestValues(@NonNull String memberId, List<Task> editingTasks) {
            mMemberId = memberId;
            mEditingTasks = editingTasks;
        }

        public String getMemberId() {
            return mMemberId;
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
