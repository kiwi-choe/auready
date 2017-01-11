package com.kiwi.auready_ver2.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/12/16.
 */
public class SaveFriend extends UseCase<SaveFriend.RequestValues, SaveFriend.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public SaveFriend(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        final Friend friend = requestValues.getFriend();
        mFriendRepository.saveFriend(friend, new FriendDataSource.SaveCallback() {
            @Override
            public void onSaveSuccess() {

                getUseCaseCallback().onSuccess(new ResponseValue(friend));
            }

            @Override
            public void onSaveFailed() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Friend mFriend;

        public RequestValues(@NonNull Friend friend) {
            mFriend = checkNotNull(friend, "friend cannot be null");
        }

        public Friend getFriend() {
            return mFriend;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final Friend mFriend;

        public ResponseValue(@NonNull Friend friend) {
            mFriend = checkNotNull(friend, "friend cannot be null");
        }

        public Friend getFriend() {
            return mFriend;
        }
    }
}
