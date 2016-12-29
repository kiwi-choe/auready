package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Update a taskHead - add members
 */
public class AddMembers extends UseCase<AddMembers.RequestValues, AddMembers.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;

    public AddMembers(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadRepository.addMembers(requestValues.getId(), requestValues.getMembers());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final String mId;
        private final List<Friend> mMembers;

        public RequestValues(@NonNull String id, @NonNull List<Friend> members) {
            mId = checkNotNull(id);
            mMembers = checkNotNull(members);
        }

        public List<Friend> getMembers() {
            return mMembers;
        }

        public String getId() {
            return mId;
        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {    }

}
