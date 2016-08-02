package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

/**
 * Created by kiwi on 7/1/16.
 */
public interface FriendDataSource {

    void saveFriend(@NonNull Friend friend);

    interface GetFriendCallback {

        void onFriendLoaded(Friend friend);
        void onDataNotAvailable();
    }

    void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback);

//    void getFriends(@NonNull LoadFriendsCallback callback);
//
//    void saveFriend(Friend friend);
//
//    void getFriend(String _id, GetFriendCallback getFriendCallback);
//
//    void deleteFriend(@NonNull String email);
//
//    interface LoadFriendsCallback {
//
//        void onFriendsLoaded(List<Friend> friends);
//        void onDataNotAvailable();
//    }
//
//    interface GetFriendCallback {
//
//        void onFriendLoaded(Friend friend);
//        void onDataNotAvailable();
//    }
}
