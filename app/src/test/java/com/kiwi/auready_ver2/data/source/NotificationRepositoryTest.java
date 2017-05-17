package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Notification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for Notification DataSources
 */
public class NotificationRepositoryTest {

    private static final List<Notification> NOTIFICATIONS = Lists.newArrayList(
            new Notification(Notification.TYPES.friend_request.name(), "A id", "A", "친구요청"),
            new Notification(Notification.TYPES.friend_request.name(), "B id", "B", "친구요청")
    );
    private NotificationRepository mRepository;

    @Mock
    private NotificationDataSource mLocalDataSource;
    @Mock
    private NotificationDataSource mRemoteDataSource;

    @Mock
    private NotificationDataSource.LoadNotificationsCallback mLoadCallback;
    @Captor
    private ArgumentCaptor<NotificationDataSource.LoadNotificationsCallback> mLoadCallbackCaptor;
    @Mock
    private NotificationDataSource.GetCountCallback mGetCountCallback;
    @Captor
    private ArgumentCaptor<NotificationDataSource.GetCountCallback> mGetCountCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mRepository = NotificationRepository.getInstance(mRemoteDataSource, mLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        NotificationRepository.destroyInstance();
    }

    @Test
    public void loadNotificationsFromLocal() {
        mRepository.loadNotifications(mLoadCallback);
        verify(mLocalDataSource).loadNotifications(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onLoaded(NOTIFICATIONS);
        verify(mLoadCallback).onLoaded(NOTIFICATIONS);
    }

    @Test
    public void loadNotificationsFromRemote() {
        mRepository.loadNotifications(mLoadCallback);

        // load failed from Local
        verify(mLocalDataSource).loadNotifications(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onDataNotAvailable();

        verify(mRemoteDataSource).loadNotifications(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onLoaded(NOTIFICATIONS);
        verify(mLoadCallback).onLoaded(NOTIFICATIONS);
    }

    @Test
    public void getNotificationsCount() {
        mRepository.getNotificationsCount(mGetCountCallback);
        verify(mLocalDataSource).getNotificationsCount(mGetCountCallbackCaptor.capture());
        int stubbedNotiCount = 1;
        mGetCountCallbackCaptor.getValue().onSuccessGetCount(stubbedNotiCount);
        verify(mGetCountCallback).onSuccessGetCount(eq(stubbedNotiCount));
    }

    @Test
    public void getNotificationCountFromRemote() {
        mRepository.getNotificationsCount(mGetCountCallback);

        verify(mLocalDataSource).getNotificationsCount(mGetCountCallbackCaptor.capture());
        mGetCountCallbackCaptor.getValue().onDataNotAvailable();

        verify(mRemoteDataSource).loadNotifications(mLoadCallbackCaptor.capture());
        mLoadCallbackCaptor.getValue().onLoaded(NOTIFICATIONS);
        verify(mGetCountCallback).onSuccessGetCount(anyInt());
    }

    @Test
    public void deleteNotification_local() {
        mRepository.deleteNotification(NOTIFICATIONS.get(0).getId());

        verify(mLocalDataSource).deleteNotification(NOTIFICATIONS.get(0).getId());
    }
}