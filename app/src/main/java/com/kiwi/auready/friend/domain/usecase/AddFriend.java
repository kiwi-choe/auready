package com.kiwi.auready.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.SearchedUser;
import com.kiwi.auready.data.source.FriendRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Request adding to be Friend
 */
public class AddFriend extends UseCase<AddFriend.RequestValues, AddFriend.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public AddFriend(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final SearchedUser mUser;

        public RequestValues(@NonNull SearchedUser user) {
            mUser = checkNotNull(user, "user cannot be null");
        }

        public SearchedUser getUser() {
            return mUser;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue { }
}
