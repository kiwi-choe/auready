package com.kiwi.auready_ver2.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 9/19/16.
 */
public class DeleteTasks extends UseCase<DeleteTasks.RequestValues, DeleteTasks.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public DeleteTasks(@NonNull TaskRepository taskRepository) {
        mTaskRepository = checkNotNull(taskRepository);
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mTaskRepository.deleteTasks(values.getTaskHeadId(), values.getMemberId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;
        private final String mMemberId;

        public RequestValues(@NonNull String taskHeadId, @NonNull String memberId) {
            mTaskHeadId = taskHeadId;
            mMemberId = memberId;
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }

        public String getMemberId() {
            return mMemberId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
