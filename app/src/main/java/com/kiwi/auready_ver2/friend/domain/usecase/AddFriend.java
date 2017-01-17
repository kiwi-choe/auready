package com.kiwi.auready_ver2.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;

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
        mFriendRepository.addFriend(requestValues.getUser(), new FriendDataSource.AddFriendCallback() {
            @Override
            public void onSuccessAddFriend() {
                getUseCaseCallback().onSuccess(new ResponseValue());
            }

            @Override
            public void onFailedAddFriend() {
                getUseCaseCallback().onError();
            }
        });
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
