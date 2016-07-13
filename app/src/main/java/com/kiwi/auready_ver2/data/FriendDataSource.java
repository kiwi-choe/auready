package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by kiwi on 7/1/16.
 */
public interface FriendDataSource {

    void getFriends(@NonNull LoadFriendsCallback callback);

    void saveFriend(Friend friend);

    void getFriend(String friendId, GetFriendCallback getFriendCallback);

    void deleteFriend(@NonNull String id);

    interface LoadFriendsCallback {

        void onFriendsLoaded(List<Friend> friends);
        void onDataNotAvailable();
    }

    interface GetFriendCallback {

        void onFriendLoaded(Friend friend);
        void onDataNotAvailable();
    }
}
