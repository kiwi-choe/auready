package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

import java.util.List;

/**
 * Created by kiwi on 7/1/16.
 */
public interface FriendDataSource {

    interface GetFriendCallback {

        void onFriendLoaded(Friend friend);
        void onDataNotAvailable();
    }

    void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback);

    interface SaveFriendsCallback {

        void onFriendsSaved(List<Friend> friends);
        void onDataNotAvailable();
    }

    void saveFriends(@NonNull SaveFriendsCallback callback);

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
