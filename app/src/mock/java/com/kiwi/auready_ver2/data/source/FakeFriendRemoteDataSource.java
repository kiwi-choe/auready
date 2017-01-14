package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

/**
 * Fake RemoteDataSource of Friend
 */
public class FakeFriendRemoteDataSource implements FriendDataSource {

    private static FakeFriendRemoteDataSource INSTANCE;
    public static FakeFriendRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FakeFriendRemoteDataSource();
        }
        return INSTANCE;
    }

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
    public void saveFriend(@NonNull Friend friend, @NonNull SaveCallback callback) {

    }
}
