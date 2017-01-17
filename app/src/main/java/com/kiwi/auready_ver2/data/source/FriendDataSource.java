package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;

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
    void findPeople(@NonNull String emailOrName, @NonNull LoadSearchedPeopleCallback callback);

    interface AddFriendCallback {
        void onSuccessAddFriend();
        void onFailedAddFriend();
    }
    void addFriend(@NonNull SearchedUser user, @NonNull AddFriendCallback callback);
}
