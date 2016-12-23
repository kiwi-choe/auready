package com.kiwi.auready_ver2.login.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Updates or creates a new {@link Friend} in the {@link FriendRepository}.
 */
public class InitFriend extends UseCase<InitFriend.RequestValues, InitFriend.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public InitFriend(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues requestValues) {

        // Save friends
        // * save friendList(save several friends on background,
        // and when completed to save callback(overhead? don't need to check if saving is success??)
        List<Friend> friendList = requestValues.getFriends();
        mFriendRepository.initFriend(friendList);

        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final List<Friend> mFriends;

        public RequestValues(@NonNull List<Friend> friends) {
            mFriends = checkNotNull(friends, "friends cannot be null!");
        }

        public List<Friend> getFriends() {
            return mFriends;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        // Response of save(success or not)
        // ex. boolean value || saved values

    }
}
