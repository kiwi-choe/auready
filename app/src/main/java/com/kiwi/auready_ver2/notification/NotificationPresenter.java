package com.kiwi.auready_ver2.notification;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.notification.domain.usecase.DeleteNotification;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotificationsCount;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotifications;
import com.kiwi.auready_ver2.notification.domain.usecase.ReadNotification;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.friend.IFriendService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Connect TaskHeadsFragment(Views about notification) to model
 * has two types;
 * 1. notification fragment view
 * 2. menu view of taskHeads fragment
 */
public class NotificationPresenter implements
        NotificationContract.Presenter,
        NotificationContract.MenuPresenter {

    @NonNull
    private final UseCaseHandler mUseCaseHandler;
    private NotificationContract.View mView;
    private NotificationContract.MenuView mMenuView;

    @NonNull
    private GetNotifications mGetNotifications;
    private DeleteNotification mDeleteNotification;

    @NonNull
    private GetNotificationsCount mGetNotificationsCount;

    private String mAccessToken;

    // for NotificationFragment view
    public NotificationPresenter(@NonNull String accessToken,
                                 @NonNull UseCaseHandler useCaseHandler,
                                 @NonNull NotificationContract.View view,
                                 @NonNull GetNotifications getNotifications,
                                 @NonNull ReadNotification readNotification,
                                 @NonNull DeleteNotification deleteNotification) {
        mUseCaseHandler = useCaseHandler;
        mView = checkNotNull(view, "view cannot be null");

        mGetNotifications = checkNotNull(getNotifications, "getNotifications cannot be null");
        mDeleteNotification = checkNotNull(deleteNotification, "deleteNotification cannot be null");

        mView.setPresenter(this);

        mAccessToken = accessToken;
    }

    // for TaskHeadsFragment menu view
    public NotificationPresenter(String accessToken, @NonNull UseCaseHandler useCaseHandler,
                                 @NonNull NotificationContract.MenuView menuView,
                                 @NonNull GetNotificationsCount getNotificationsCount) {
        mUseCaseHandler = useCaseHandler;
        mMenuView = checkNotNull(menuView, "menuView cannot be null");

        mGetNotificationsCount = checkNotNull(getNotificationsCount, "getNotificationsCount cannot be null");

        mMenuView.setMenuPresenter(this);
    }

    @Override
    public void loadNotifications() {
        mUseCaseHandler.execute(mGetNotifications, new GetNotifications.RequestValues(),
                new UseCase.UseCaseCallback<GetNotifications.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNotifications.ResponseValue response) {
                        processNotifications(response.getNotifications());
                    }

                    @Override
                    public void onError() {
                        mView.showNoNotification();
                    }
                });
    }

    private void processNotifications(List<Notification> notifications) {
        if (notifications.size() == 0) {
            mView.showNoNotification();
        } else {
            mView.showNotifications(notifications);
        }
    }

    @Override
    public void readNotification(int id) {

    }

    @Override
    public void acceptFriendRequest(final String fromUserId, final int notificationId) {
        IFriendService friendService =
                ServiceGenerator.createService(IFriendService.class, mAccessToken);

        Call<Void> call = friendService.acceptFriendRequest(fromUserId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    onAcceptFriendRequestSucceed(fromUserId, notificationId);
                } else {
                    onAcceptFriendRequestFail();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onAcceptFriendRequestFail();
            }
        });
    }

    @Override
    public void onAcceptFriendRequestSucceed(String fromUserId, int notificationId) {
        // 1. Delete this notification
        mUseCaseHandler.execute(mDeleteNotification, new DeleteNotification.RequestValues(notificationId),
                new UseCase.UseCaseCallback<DeleteNotification.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteNotification.ResponseValue response) {
                        loadNotifications();
                    }

                    @Override
                    public void onError() {

                    }
                });

        // 2. Show the result msg - 'fromUserId가 친구로 추가되었습니다.'
        mView.showAcceptFriendRequestSuccessUI(fromUserId);
    }

    @Override
    public void onAcceptFriendRequestFail() {

    }

    @Override
    public void deleteFriendRequest(final String fromUserId) {
        IFriendService friendService =
                ServiceGenerator.createService(IFriendService.class, mAccessToken);

        Call<Void> call = friendService.deleteFriendRequest(fromUserId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    onDeleteFriendRequestSucceed(fromUserId);
                } else {
                    onDeleteFriendRequestFail();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onDeleteFriendRequestFail();
            }
        });

    }

    @Override
    public void onDeleteFriendRequestSucceed(String fromUserId) {
        mView.showDeleteFriendRequestSuccessUI(fromUserId);
    }

    @Override
    public void onDeleteFriendRequestFail() {

    }

    @Override
    public void start() {
        loadNotifications();
    }

    /*
    * for Notification menu item
    * */
    @Override
    public void getNotificationsCount() {
        mUseCaseHandler.execute(mGetNotificationsCount, new GetNotificationsCount.RequestValues(),
                new UseCase.UseCaseCallback<GetNotificationsCount.ResponseValue>() {
                    @Override
                    public void onSuccess(GetNotificationsCount.ResponseValue response) {
                        mMenuView.showNotificationSign(response.getNotificationsCount());
                    }

                    @Override
                    public void onError() {
                        mMenuView.showNoNotificationSign();
                    }
                });
    }
}
