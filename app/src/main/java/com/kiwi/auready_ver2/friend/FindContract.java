package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.SearchedUser;

import java.util.List;

/**
 * Created by kiwi on 8/12/16.
 */
public interface FindContract {

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void setAddFriendSucceedUI(@NonNull String name);

        void showSearchedPeople(List<SearchedUser> searchedPeople);

        void showNoSearchedPeople();

        void setAddFriendFailMessage(int stringResource);
    }

    interface Presenter {

        // testing
        void saveFriend(@NonNull Friend friend);

        void findPeople(@NonNull String emailOrName);

        void addFriend(@NonNull String name);

        void onAddFriendSucceed(@NonNull String name);

        void onAddFriendFail(int stringResource);

        void onFindPeopleSucceed(@NonNull List<SearchedUser> searchedUsers);

        void onFindPeopleFail();
    }

}
