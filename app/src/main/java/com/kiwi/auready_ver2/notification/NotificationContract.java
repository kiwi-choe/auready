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
    }

    interface Presenter extends BasePresenter{
        void loadNotifications();

        void readNotification(int id);  // Update 'isNew' field
    }

    // Menu View of TaskHeadsFragment
    interface MenuView {
        void setMenuPresenter(MenuPresenter presenter);
        void showNewSign(int numOfNewNotifications);
    }

    interface MenuPresenter {
        void getNewNotificationsCount();
    }
}
