package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;

/**
 * Created by kiwi on 1/18/17.
 */

public class FriendRemoteDataSource implements FriendDataSource {

    private static FriendRemoteDataSource INSTANCE;

    private FriendRemoteDataSource() {}
    public static FriendRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FriendRemoteDataSource();
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

        // Request to Server
        IFriendService friendService = ServiceGenerator.createService(
                IFriendService.class,
                AccessTokenStore.getInstance().getStringValue(AccessTokenStore.ACCESS_TOKEN, ""));
        // and return callback
    }

    @Override
    public void saveFriend(@NonNull Friend friend, @NonNull SaveCallback callback) {

    }
}

