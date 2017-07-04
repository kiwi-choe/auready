package com.kiwi.auready.friend.domain.usecase;

import android.support.annotation.NonNull;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.data.Friend;
import com.kiwi.auready.data.source.FriendDataSource;
import com.kiwi.auready.data.source.FriendRepository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of friends.
 */
public class GetFriends extends UseCase<GetFriends.RequestValues, GetFriends.ResponseValue> {

    private final FriendRepository mFriendRepository;

    public GetFriends(@NonNull FriendRepository friendRepository) {
        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

//        List<Friend> friends = new ArrayList<>();
//        for(int i=0; i<100; i++){
//            friends.add(new Friend("abc"+i+"@naver.com", "MY_Friend " + i));
//        }
//
//        ResponseValue responseValue = new ResponseValue(friends);
//        getUseCaseCallback().onSuccess(responseValue);

        mFriendRepository.getFriends(new FriendDataSource.LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                ResponseValue responseValue = new ResponseValue(friends);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }


    public static class RequestValues implements UseCase.RequestValues {
    }

    public class ResponseValue implements UseCase.ResponseValue {

        private final List<Friend> mFriends;

        public ResponseValue(@NonNull List<Friend> friends) {
            mFriends = checkNotNull(friends, "friends cannot be null");
        }

        public List<Friend> getFriends() {
            return mFriends;
        }
    }
}
