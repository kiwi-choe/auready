package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit tasks of a taskHead
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
        mTaskRepository.editTasks(values.getTaskHeadId(), values.getMemberTasks());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;
        private final Map<String, List<Task>> mMemberTasks;

        public RequestValues(@NonNull String taskHeadId, @NonNull Map<String, List<Task>> memberTasks) {
            mTaskHeadId = checkNotNull(taskHeadId);
            mMemberTasks = checkNotNull(memberTasks, "memberTasks cannot be null");
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }

        public Map<String, List<Task>> getMemberTasks() {
            return mMemberTasks;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
