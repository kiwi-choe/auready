package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;

import java.util.List;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeFriendRemoteDataSource implements FriendDataSource {

    private static FakeFriendRemoteDataSource INSTANCE;

    // Prevent direct instantiation
    private FakeFriendRemoteDataSource() {}

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
    public void getFriends(@NonNull LoadFriendsCallback callback) {

    }

    @Override
    public void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback) {

    }

    @Override
    public void saveFriends(List<Friend> friends) {

    }

    @Override
    public void saveFriend(@NonNull Friend friend) {

    }
}
