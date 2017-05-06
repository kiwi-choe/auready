package com.kiwi.auready_ver2.notification;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.local.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.local.NotificationLocalDataSource;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNewNotificationsCount;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotifications;
import com.kiwi.auready_ver2.notification.domain.usecase.ReadNotification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Notification Presenter test;
 * 1. for NotificationFragment view
 * 2. for TaskHeadsFragment menu view
 */
public class NotificationPresenterTest {

    private static final List<Notification> NOTIFICATIONS = Lists.newArrayList(
            new Notification(Notification.TYPES.friend_request.name(), "stubbed_fromUserId", "stubbed_fromUserName", "친구요청"));

    private NotificationPresenter mPresenter;

    private NotificationPresenter mMenuPresenter;

    @Mock
    private NotificationContract.View mView;
    @Mock
    private NotificationContract.MenuView mMenuView;

    @Mock
    private NotificationLocalDataSource mLocalRepository;
    @Captor
    private ArgumentCaptor<NotificationDataSource.LoadNotificationsCallback> mLoadNotificationsCallbackCaptor;
    @Captor
    private ArgumentCaptor<NotificationDataSource.GetNewCountCallback> mGetNewCountCallbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mPresenter = givenNotificationPresenter();
        mMenuPresenter = givenMenuPresenter();
    }

    private NotificationPresenter givenMenuPresenter() {
        String accessToken = "stubbedAccessToken";
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetNewNotificationsCount getNewNotificationsCount = new GetNewNotificationsCount(mLocalRepository);

        return new NotificationPresenter(accessToken, useCaseHandler, mMenuView, getNewNotificationsCount);
    }

    private NotificationPresenter givenNotificationPresenter() {
        String accessToken = "stubbedAccessToken";
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetNotifications getNotifications = new GetNotifications(mLocalRepository);
        ReadNotification readNotification = new ReadNotification(mLocalRepository);

        return new NotificationPresenter(accessToken, useCaseHandler, mView, getNotifications, readNotification);
    }

    @Test
    public void loadNotificationsFromRepository_updateView() {
        mPresenter = givenNotificationPresenter();
        mPresenter.loadNotifications();

        verify(mLocalRepository).loadNotifications(mLoadNotificationsCallbackCaptor.capture());
        mLoadNotificationsCallbackCaptor.getValue().onLoaded(NOTIFICATIONS);

        ArgumentCaptor<List> showNotisArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mView).showNotifications(showNotisArgumentCaptor.capture());
        assertTrue(showNotisArgumentCaptor.getValue().size() == NOTIFICATIONS.size());
    }

    @Test
    public void getNewNotificationsCount_updateView() {
        mMenuPresenter = givenMenuPresenter();
        mMenuPresenter.getNewNotificationsCount();

        int COUNT_OF_NEW = 0;
        verify(mLocalRepository).getNewNotificationsCount(mGetNewCountCallbackCaptor.capture());
        mGetNewCountCallbackCaptor.getValue().onLoaded(COUNT_OF_NEW);

        ArgumentCaptor<Integer> showArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mMenuView).showNewSign(showArgumentCaptor.capture());
        assertTrue(showArgumentCaptor.getValue() == COUNT_OF_NEW);
    }

    @Test
    public void acceptFriendRequest_whenSucceed_updateUIAndDeleteTheNotification() {
        mPresenter = givenNotificationPresenter();
        String fromUserId = "stubbed_fromUserId";
        int notificationId = 1;
        mPresenter.onAcceptFriendRequestSucceed(fromUserId, notificationId);

        verify(mView).showAcceptFriendRequestSuccessUI(fromUserId);
        verify(mLocalRepository).deleteNotification(notificationId);
    }
}