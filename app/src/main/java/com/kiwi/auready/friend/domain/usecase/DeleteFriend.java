package com.kiwi.auready.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Friend;
import com.kiwi.auready.data.source.FriendRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes a {@link Friend} from the {@link FriendRepository}.
 */
public class DeleteFriend extends UseCase<DeleteFriend.RequestValues, DeleteFriend.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public DeleteFriend(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues values) {

        mFriendRepository.deleteFriend(values.getId());
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mId;

        public RequestValues(@NonNull String id) {
            mId = checkNotNull(id, "id cannot be null!");
        }
        public String getId() {
            return mId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue { }
}
