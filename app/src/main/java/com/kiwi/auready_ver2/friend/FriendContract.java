package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiwi on 6/28/16.
 */
public interface FriendContract {

    interface View {

        void showFriends(List<Friend> friendList);

        void showSearchedEmailList(ArrayList<String> searchedEmailList);
        void showNoResultByEmail();

        void setPresenter(@NonNull FriendContract.Presenter presenter);
    }

    interface Presenter {

        void loadFriends();

        void start();

        void deleteAFriend(Friend clickedFriend);
    }
}
