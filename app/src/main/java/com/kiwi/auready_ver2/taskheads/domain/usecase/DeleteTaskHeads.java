package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes taskHeads from the TaskHeadRepository.
 */
public class DeleteTaskHeads extends UseCase<DeleteTaskHeads.RequestValues, DeleteTaskHeads.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;

    public DeleteTaskHeads(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository);
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadRepository.deleteTaskHeads(requestValues.getTaskHeadIds());

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final List<String> mTaskHeadIds;

        public RequestValues(@NonNull List<String> taskHeadIds) {
            mTaskHeadIds = checkNotNull(taskHeadIds, "taskHeadIds cannot be null");
        }

        public List<String> getTaskHeadIds() {
            return mTaskHeadIds;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
