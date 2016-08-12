package com.kiwi.auready_ver2.friend;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

/**
 * Created by kiwi on 8/12/16.
 */
public interface FindContract {

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void showSuccessMsgOfAddFriend(Friend newFriend);
    }

    interface Presenter {

        void saveFriend(@NonNull Friend friend);

    }

}
