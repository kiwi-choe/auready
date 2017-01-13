package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;

/**
 * Created by kiwi on 8/12/16.
 */
public interface FindContract {

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void showSuccessMsgOfAddFriend(Friend newFriend);

        void showSearchedEmailList(ArrayList<String> searchedEmailList);

        void showNoResultByEmail();
    }

    interface Presenter {

        // testing
        void saveFriend(@NonNull Friend friend);

        void findPeople(@NonNull String emailOrName);

        void addFriend(@NonNull Friend friend);
    }

}
