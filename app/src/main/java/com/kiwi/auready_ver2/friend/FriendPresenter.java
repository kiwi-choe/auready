package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.login.domain.usecase.GetFriend;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/28/16.
 */
public class FriendPresenter implements FriendContract.Presenter {

    // We start the friends to 3.
    private static ArrayList<Friend> FRIENDS = Lists.newArrayList(new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));

    private final FriendContract.View mFriendView;
    private final UseCaseHandler mUseCaseHandler;

    private FriendRepository mFriendRepository;

    public FriendPresenter(@NonNull UseCaseHandler useCaseHandler,
                           @NonNull FriendContract.View friendView,
                           @NonNull GetFriend getFriend) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null");
        mFriendView = checkNotNull(friendView, "friendView cannot be null");

        mFriendView.setPresenter(this);
    }

    @Override
    public void loadFriends() {
    }

    @Override
    public void start() {
        loadFriends();
    }

    @Override
    public void deleteFriend(String requestedFriendId) {
     }

    private void processFriends(List<Friend> friends) {

        mFriendView.showFriends(friends);
    }
}
