package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.FriendDataSource;
import com.kiwi.auready_ver2.data.FriendRepository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 6/28/16.
 */
public class FriendPresenter implements FriendContract.Presenter {

    // We start the friends to 3.
    private static ArrayList<Friend> FRIENDS = Lists.newArrayList(new Friend("aa"), new Friend("bb"), new Friend("cc"));

    private final FriendContract.View mFriendView;

    private FriendRepository mFriendRepository;

    public FriendPresenter(@NonNull FriendContract.View friendView, @NonNull FriendRepository friendRepository) {

        mFriendView = checkNotNull(friendView, "friendView cannot be null");

        mFriendRepository = checkNotNull(friendRepository, "friendRepository cannot be null");

        mFriendView.setPresenter(this);
    }

    @Override
    public void loadFriends() {

        mFriendRepository.getFriends(new FriendDataSource.LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {

                processFriends(friends);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    public void start() {
        loadFriends();
    }

    @Override
    public void deleteFriend(String requestedFriendId) {
        mFriendRepository.deleteFriend(requestedFriendId);
    }

    private void processFriends(List<Friend> friends) {

        mFriendView.showFriends(friends);
    }
}
