package com.kiwi.auready.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready.BasePresenter;
import com.kiwi.auready.BaseView;
import com.kiwi.auready.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 6/28/16.
 */
public interface FriendsContract {

    interface View extends BaseView<Presenter> {

        void showNoFriends();
        void showFriends(List<Friend> friendList);

        void setLoadingIndicator(boolean active);

        void setResultToTaskHeadDetailView(ArrayList<Friend> selectedFriends);
    }

    interface Presenter extends BasePresenter {

        void loadFriends();

        void deleteFriend(@NonNull String id);
    }
}
