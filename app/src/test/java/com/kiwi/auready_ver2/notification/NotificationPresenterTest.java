package com.kiwi.auready_ver2.notification;

import android.content.Context;
import android.test.mock.MockContext;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Notification;
import com.kiwi.auready_ver2.data.source.NotificationDataSource;
import com.kiwi.auready_ver2.data.source.NotificationRepository;
import com.kiwi.auready_ver2.notification.domain.usecase.DeleteNotification;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotifications;
import com.kiwi.auready_ver2.notification.domain.usecase.GetNotificationsCount;
import com.kiwi.auready_ver2.notification.domain.usecase.ReadNotification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private NotificationRepository mRepository;
    @Captor
    private ArgumentCaptor<NotificationDataSource.LoadNotificationsCallback> mLoadNotificationsCallbackCaptor;
    @Captor
    private ArgumentCaptor<NotificationDataSource.GetCountCallback> mGetCountCallbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mPresenter = givenNotificationPresenter();
        mMenuPresenter = givenMenuPresenter();
    }

    private NotificationPresenter givenMenuPresenter() {
        String accessToken = "stubbedAccessToken";
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetNotificationsCount getNotificationsCount = new GetNotificationsCount(mRepository);

        return new NotificationPresenter(accessToken, useCaseHandler, mMenuView, getNotificationsCount);
    }

    private NotificationPresenter givenNotificationPresenter() {
        String accessToken = "stubbedAccessToken";
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetNotifications getNotifications = new GetNotifications(mRepository);
        ReadNotification readNotification = new ReadNotification(mRepository);
        DeleteNotification deleteNotification = new DeleteNotification(mRepository);
//        Context context = Mockito.mock(Context.)
        Context context = Mockito.mock(MockContext.class);
        return new NotificationPresenter(accessToken, useCaseHandler, mView,
                getNotifications, readNotification, deleteNotification, context);
    }

    @Test
    public void loadNotificationsFromRepository_updateView() {
        mPresenter = givenNotificationPresenter();
        mPresenter.loadNotifications();

        verify(mRepository).loadNotifications(mLoadNotificationsCallbackCaptor.capture());
        mLoadNotificationsCallbackCaptor.getValue().onLoaded(NOTIFICATIONS);

        ArgumentCaptor<List> showNotisArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mView).showNotifications(showNotisArgumentCaptor.capture());
        assertTrue(showNotisArgumentCaptor.getValue().size() == NOTIFICATIONS.size());
    }

    @Test
    public void getNotificationsCount_updateView() {
        mMenuPresenter = givenMenuPresenter();
        mMenuPresenter.getNotificationsCount();

        int COUNT_OF_NOTIS = 1;
        verify(mRepository).getNotificationsCount(mGetCountCallbackCaptor.capture());
        mGetCountCallbackCaptor.getValue().onSuccessGetCount(COUNT_OF_NOTIS);

        ArgumentCaptor<Integer> showArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mMenuView).showNotificationSign(showArgumentCaptor.capture());
        assertTrue(showArgumentCaptor.getValue() == COUNT_OF_NOTIS);
    }

    @Test
    public void acceptFriendRequest_whenSucceed_updateUIAndDeleteTheNotification() {
        mPresenter = givenNotificationPresenter();
        String fromUserId = "stubbed_fromUserId";
        int notificationId = 1;
        mPresenter.onAcceptFriendRequestSucceed(fromUserId, fromUserName, notificationId);

        verify(mView).showAcceptFriendRequestSuccessUI(fromUserId);
        verify(mRepository).deleteNotification(notificationId);
    }
}