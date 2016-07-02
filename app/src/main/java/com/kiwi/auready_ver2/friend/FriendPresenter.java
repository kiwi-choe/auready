package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 6/28/16.
 */
public class FriendPresenter implements FriendContract.Presenter {

    // We start the friends to 3.
    private static ArrayList<Friend> FRIENDS = Lists.newArrayList(new Friend("aa"), new Friend("bb"), new Friend("cc"));

    private final FriendContract.View mFriendView;

    public FriendPresenter(@NonNull FriendContract.View friendView) {

        mFriendView = friendView;

        mFriendView.setPresenter(this);
    }

    @Override
    public void loadFriends() {

        // Stub friends
//        List<Friend> friendList = new ArrayList<>();
//        Friend friend1 = new Friend("aa");
//        Friend friend2 = new Friend("bb");
//        friendList.add(friend1);
//        friendList.add(friend2);


        processFriends(FRIENDS);
//        mFriendView.showFriends();
    }

    @Override
    public void start() {
        loadFriends();
    }

    @Override
    public void deleteAFriend(Friend clickedFriend) {

    }

    private void processFriends(List<Friend> friends) {

        mFriendView.showFriends(friends);
    }
}
