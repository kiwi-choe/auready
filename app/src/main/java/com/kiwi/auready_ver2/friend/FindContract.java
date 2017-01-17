package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;

import java.util.List;

/**
 * Created by kiwi on 8/12/16.
 */
public interface FindContract {

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void setViewWhenAddFriendSucceed(@NonNull SearchedUser user);

        void showSearchedPeople(List<SearchedUser> searchedPeople);

        void showNoSearchedPeople();
    }

    interface Presenter {

        // testing
        void saveFriend(@NonNull Friend friend);

        void findPeople(@NonNull String emailOrName);

        void addFriend(@NonNull SearchedUser user);
    }

}
