package com.kiwi.auready.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready.data.SearchedUser;

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

        void onEmailOrNameTextError();
    }

    interface Presenter {

        void findPeople(@NonNull String emailOrName);

        void addFriend(@NonNull SearchedUser searchedUser);

        void onAddFriendSucceed(@NonNull String name);

        void onAddFriendFail(int stringResource);

        void onFindPeopleSucceed(@NonNull List<SearchedUser> searchedUsers);

        void onFindPeopleFail();
    }

}
