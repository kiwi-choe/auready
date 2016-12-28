package com.kiwi.auready_ver2.taskheaddetail.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Edit a taskHead - title or members
 */
public class EditTaskHead extends UseCase<EditTaskHead.RequestValues, EditTaskHead.ResponseValue> {

    private final TaskHeadRepository mTaskHeadRepository;

    public EditTaskHead(@NonNull TaskHeadRepository taskHeadRepository) {
        mTaskHeadRepository = checkNotNull(taskHeadRepository, "taskHeadRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mTaskHeadRepository.editTaskHead(requestValues.getId(), requestValues.getTitle(), requestValues.getMembers());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final String mId;
        private final String mTitle;
        private final List<Friend> mMembers;

        public RequestValues(@NonNull String id, @NonNull String title, List<Friend> members) {
            mId = checkNotNull(id);
            mTitle = checkNotNull(title, "title cannot be null");
            mMembers = checkNotNull(members);
        }

        public String getTitle() {
            return mTitle;
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
