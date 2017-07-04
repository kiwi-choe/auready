package com.kiwi.auready.friend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready.UseCase;
import com.kiwi.auready.UseCaseHandler;
import com.kiwi.auready.data.Friend;
import com.kiwi.auready.friend.domain.usecase.DeleteFriend;
import com.kiwi.auready.friend.domain.usecase.GetFriends;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready.friend.FriendsFragment.TAG_FRIENDFRAG;

/**
 * Created by kiwi on 6/28/16.
 */
public class FriendsPresenter implements FriendsContract.Presenter {

    private final FriendsContract.View mFriendView;
    private final UseCaseHandler mUseCaseHandler;
    private final GetFriends mGetFriends;
    private final DeleteFriend mDeleteFriend;

    public FriendsPresenter(@NonNull UseCaseHandler useCaseHandler,
                            @NonNull FriendsContract.View friendView,
                            @NonNull GetFriends getFriends,
                            @NonNull DeleteFriend deleteFriend) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mFriendView = checkNotNull(friendView, "friendView cannot be null");

        mGetFriends = checkNotNull(getFriends, "getFriends cannot be null");
        mDeleteFriend = checkNotNull(deleteFriend);

        mFriendView.setPresenter(this);
    }

    @Override
    public void loadFriends() {

        mFriendView.setLoadingIndicator(true);

        mUseCaseHandler.execute(mGetFriends, new GetFriends.RequestValues(),
                new UseCase.UseCaseCallback<GetFriends.ResponseValue>() {
                    @Override
                    public void onSuccess(GetFriends.ResponseValue response) {
                        List<Friend> friends = response.getFriends();

                        mFriendView.setLoadingIndicator(false);
                        processFriends(friends);
                    }

                    @Override
                    public void onError() {
                        mFriendView.setLoadingIndicator(false);
                        mFriendView.showNoFriends();
                    }
                });
    }

    @Override
    public void start() {
        loadFriends();
    }

    @Override
    public void deleteFriend(@NonNull String id) {
        mUseCaseHandler.execute(mDeleteFriend, new DeleteFriend.RequestValues(id),
                new UseCase.UseCaseCallback<DeleteFriend.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteFriend.ResponseValue response) {
                        loadFriends();
                    }

                    @Override
                    public void onError() {
                        Log.d(TAG_FRIENDFRAG, "deleteFriend onError()");
                    }
                });
     }

    private void processFriends(List<Friend> friends) {

        if(friends.isEmpty()) {
            mFriendView.showNoFriends();
        } else {
            mFriendView.showFriends(friends);
        }
    }
}
