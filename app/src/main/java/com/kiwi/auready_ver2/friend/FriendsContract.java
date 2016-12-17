package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 6/28/16.
 */
public interface FriendsContract {

    interface View extends BaseView<Presenter> {

        void showNoFriends();
        void showFriends(List<Friend> friendList);

        void showSearchedEmailList(ArrayList<String> searchedEmailList);
        void showNoResultByEmail();

        void setLoadingIndicator(boolean active);

    }

    interface Presenter extends BasePresenter {

        void loadFriends();

        void deleteFriend(@NonNull String id);
    }
}
