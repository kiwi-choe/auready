package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.source.FriendDataSource;

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
    public void saveFriend(@NonNull Friend friend) {

    }

    @Override
    public void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback) {

    }
}
