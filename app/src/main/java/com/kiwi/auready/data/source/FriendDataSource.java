package com.kiwi.auready.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready.data.Friend;
import com.kiwi.auready.data.SearchedUser;

import java.util.List;

/**
 * Created by kiwi on 7/1/16.
 */
public interface FriendDataSource {

    void deleteAllFriends();

    void deleteFriend(@NonNull String id);

    interface LoadFriendsCallback {

        void onFriendsLoaded(List<Friend> friends);
        void onDataNotAvailable();
    }

    void getFriends(@NonNull LoadFriendsCallback callback);

    interface SaveCallback {
        void onSaveSuccess();
        void onSaveFailed();
    }

    void saveFriend(@NonNull Friend friend, @NonNull SaveCallback callback);

    /*
    * only for RemoteDataSource
    * */
    interface LoadSearchedPeopleCallback {
        void onSearchedPeopleLoaded(@NonNull List<SearchedUser> searchedPeople);
        void onDataNotAvailable();
    }
}
