package com.kiwi.auready.tasks.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.TaskRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 1/13/17.
 */

public class GetMembers extends UseCase<GetMembers.RequestValues, GetMembers.ResponseValue> {

    private final TaskRepository mTaskRepository;

    public GetMembers(@NonNull TaskRepository taskRepository) {
        mTaskRepository = taskRepository;
    }

    @Override
    protected void executeUseCase(final RequestValues values) {

        mTaskRepository.getMembers(values.getTaskHeadId(), new TaskDataSource.LoadMembersCallback() {

            @Override
            public void onMembersLoaded(List<Member> members) {
                getUseCaseCallback().onSuccess(new ResponseValue(members));
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {
        private final String mTaskHeadId;

        public RequestValues(@NonNull String taskHeadId) {
            mTaskHeadId = checkNotNull(taskHeadId);
        }

        public String getTaskHeadId() {
            return mTaskHeadId;
        }
    }

    public class ResponseValue implements UseCase.ResponseValue {

        private final List<Member> mMembers;

        public ResponseValue(@NonNull List<Member> members) {
            mMembers = members;
        }

        public List<Member> getMembers() {
            return mMembers;
        }
    }
}
