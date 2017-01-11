package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class FriendRepositoryTest {

    private static final String FRIEND_COL_ID = "friendColumnId";
    // We start the friends to 3.
    private static ArrayList<Friend> FRIENDS = Lists.newArrayList(
            new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));

    private FriendRepository mRepository;

    @Mock
    private FriendDataSource mLocalDataSource;

    @Mock
    private FriendDataSource.SaveCallback mSaveCallback;
    @Captor
    private ArgumentCaptor<FriendDataSource.SaveCallback> mSaveCallbackCaptor;
    @Mock
    private FriendDataSource.LoadFriendsCallback mLoadFriendsCallback;
    @Captor
    private ArgumentCaptor<FriendDataSource.LoadFriendsCallback> mLoadFriendsCallbackCaptor;

    @Before
    public void setupFriendRepository() {
        MockitoAnnotations.initMocks(this);
        // Get a reference to the class under test
        mRepository = FriendRepository.getInstance(mLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        FriendRepository.destroyInstance();
    }

    @Test
    public void saveFriend() {
        // Given a stub friend with title and description
        Friend newFriend = new Friend("aa@aa.com", "nameOfaa");

        // When a friend is saved to the friend repository
        mRepository.saveFriend(newFriend , mSaveCallback);

        // Then the persistent repository are called
        verify(mLocalDataSource).saveFriend(eq(newFriend), mSaveCallbackCaptor.capture());
    }
    @Test
    public void saveFriend_addToCache_whenSaveToLocalIsSucceed() {
        // Given a stub friend with title and description
        Friend newFriend = new Friend("aa@aa.com", "nameOfaa");

        // Save to Local data source is succeed
        mRepository.saveFriend(newFriend , mSaveCallback);
        setFriendsSavedSuccess(mLocalDataSource, newFriend);

        assertThat(mRepository.mCacheFriends.containsKey(newFriend.getId()), is(true));
    }

    @Test
    public void getFriends_requestsFriendsFromLocalDataSource() {

        mRepository.getFriends(mLoadFriendsCallback);
        verify(mLocalDataSource).getFriends(any(FriendDataSource.LoadFriendsCallback.class));
    }

    @Test
    public void getFriendsWithLocalDataSourceUnavailable_firesOnDataUnavailable() {
        mRepository.getFriends(mLoadFriendsCallback);
        setFriendsNotAvailable(mLocalDataSource);

        verify(mLoadFriendsCallback).onDataNotAvailable();
    }

    @Test
    public void deleteAll_fromLocal() {
        mRepository.deleteAllFriends();

        verify(mLocalDataSource).deleteAllFriends();
    }

    private void setFriendsNotAvailable(FriendDataSource dataSource) {
        verify(dataSource).getFriends(mLoadFriendsCallbackCaptor.capture());
        mLoadFriendsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void saveStubbedFriends(ArrayList<Friend> friends) {
        for(Friend friend:friends) {
            mRepository.saveFriend(friend, mSaveCallback);
            setFriendsSavedSuccess(mLocalDataSource, friend);
        }
    }

    private void setFriendsSavedSuccess(FriendDataSource dataSource, Friend newFriend) {
        verify(dataSource).saveFriend(eq(newFriend), mSaveCallbackCaptor.capture());
        mSaveCallbackCaptor.getValue().onSaveSuccess();
    }
}