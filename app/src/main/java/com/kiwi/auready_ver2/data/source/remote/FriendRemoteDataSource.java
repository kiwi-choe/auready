package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;

import java.util.List;

/**
 * Created by kiwi on 7/19/16.
 */
public class FriendRemoteDataSource implements FriendDataSource {

    private static FriendRemoteDataSource INSTANCE;
    public static FriendRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FriendRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation
    private FriendRemoteDataSource() {}

    /*
    * Not save, update Friend to remote database
    * */
//    @Override
//    public void saveFriend(@NonNull Friend friend) {
//
//    }

    @Override
    public void deleteAllFriends() {

    }

    @Override
    public void deleteFriend(@NonNull String id) {

    }

    @Override
    public void getFriends(@NonNull LoadFriendsCallback callback) {

    }

    @Override
    public void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback) {

    }

    @Override
    public void initFriend(@NonNull List<Friend> friends) {
        // implement in Local
    }

    @Override
    public void saveFriend(@NonNull Friend friend) {

    }
}
