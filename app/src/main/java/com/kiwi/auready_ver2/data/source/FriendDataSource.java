package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

import java.util.List;

/**
 * Created by kiwi on 7/1/16.
 */
public interface FriendDataSource {

    void deleteAllFriends();

    interface LoadFriendsCallback {

        void onFriendsLoaded(List<Friend> friends);
        void onDataNotAvailable();
    }

    interface GetFriendCallback {

        void onFriendLoaded(Friend friend);
        void onDataNotAvailable();
    }

    interface SaveFriendsCallback {

        void onFriendsSaved(List<Friend> friends);
        void onDataNotAvailable();
    }

    void getFriends(@NonNull LoadFriendsCallback callback);

    void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback getFriendCallback);

    void saveFriends(@NonNull List<Friend> friends);

    void saveFriend(@NonNull Friend friend);
}
