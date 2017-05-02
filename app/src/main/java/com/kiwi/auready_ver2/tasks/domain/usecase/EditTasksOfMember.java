package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit tasks of a member
 */
public class EditTasksOfMember extends UseCase<EditTasksOfMember.RequestValues, EditTasksOfMember.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public EditTasksOfMember(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.editTasksOfMember(values.getMemberId(), values.getTasks());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mMemberId;
        private final List<Task> mTasks;

        public RequestValues(@NonNull String memberId, @NonNull List<Task> tasks) {
            mMemberId = checkNotNull(memberId);
            mTasks = checkNotNull(tasks);
        }

        public String getMemberId() {
            return mMemberId;
        }

        public List<Task> getTasks() {
            return mTasks;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
