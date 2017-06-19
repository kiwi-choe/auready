package com.kiwi.auready_ver2.notification;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Notification;

import java.util.List;

/**
 * Contract of Notification
 */

public interface NotificationContract {

    interface View extends BaseView<Presenter>{
        void showNotifications(List<Notification> notifications);

        void showNoNotification();

        void showAcceptFriendRequestSuccessUI(String fromUserId);

        void showDeleteFriendRequestSuccessUI(String fromUserId);
    }

    interface Presenter extends BasePresenter{
        void loadNotifications();

        void readNotification(int id);  // Update 'isNew' field

        // Send the response of friend request to Server
        void acceptFriendRequest(String fromUserId, String fromUserName, int notificationId);

        void deleteFriendRequest(String fromUserId);

        void onAcceptFriendRequestSucceed(String fromUserName, int notificationId);
        void onAcceptFriendRequestFail();

        void onDeleteFriendRequestSucceed(String fromUserId);
        void onDeleteFriendRequestFail();
    }

    // Menu View of TaskHeadsFragment
    interface MenuView {
        void setMenuPresenter(MenuPresenter presenter);
        void showNotificationSign(int numOfNewNotifications);

        void showNoNotificationSign();
    }

    interface MenuPresenter {

        void getNotificationsCount();
    }
}
