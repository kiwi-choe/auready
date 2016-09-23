package com.kiwi.auready_ver2.taskheads.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 9/23/16.
 */
public class EditTitle extends UseCase<EditTitle.RequestValues, EditTitle.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;

    public EditTitle(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        TaskHead taskHead = requestValues.getTaskHead();
        mTaskHeadRepository.editTitle(taskHead);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final TaskHead mTaskHead;

        public RequestValues(@NonNull TaskHead taskHead) {
            mTaskHead = checkNotNull(taskHead, "taskHead cannot be null");
        }

        public TaskHead getTaskHead() {
            return mTaskHead;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }
}
