package com.kiwi.auready_ver2.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Retrieves a {@link Friend} from the {@link FriendRepository}.
 */
public class GetFriend extends UseCase<GetFriend.RequestValues, GetFriend.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public GetFriend(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues values) {
        mFriendRepository.getFriend(values.getFriendColumnId(), new FriendDataSource.GetFriendCallback() {

            @Override
            public void onFriendLoaded(Friend friend) {
                ResponseValue responseValue = new ResponseValue(friend);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String mFriendColumnId;

        public RequestValues(@NonNull String friendColumnId) {
            mFriendColumnId = checkNotNull(friendColumnId, "friendColumnId cannot be null!");
        }
        public String getFriendColumnId() {
            return mFriendColumnId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Friend mFriend;

        public ResponseValue(@NonNull Friend friend) {
            mFriend = checkNotNull(friend, "friend cannot be null!");
        }

        public Friend getFriend() {
            return mFriend;
        }
    }
}
