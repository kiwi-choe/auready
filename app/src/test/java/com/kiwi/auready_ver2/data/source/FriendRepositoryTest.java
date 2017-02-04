package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Friend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.FriendStub.FRIENDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class FriendRepositoryTest {

    private static final String FRIEND_COL_ID = "friendColumnId";

    private FriendRepository mRepository;

    @Mock
    private FriendDataSource mLocalDataSource;
    @Mock
    private FriendDataSource mRemoteDataSource;

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
        mRepository = FriendRepository.getInstance(mLocalDataSource, mRemoteDataSource);
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
    public void getFriends_fromRemote_andRefreshLocalDataSource() {
        mRepository.getFriends(mLoadFriendsCallback);
        verify(mRemoteDataSource).getFriends(mLoadFriendsCallbackCaptor.capture());
        mLoadFriendsCallbackCaptor.getValue().onFriendsLoaded(FRIENDS);

        verify(mLocalDataSource, times(FRIENDS.size())).saveFriend(any(Friend.class), mSaveCallbackCaptor.capture());
    }

    @Test
    public void getFriendsWithRemoteUnAvailable_friendsAreRetrievedFromLocal() {
        mRepository.getFriends(mLoadFriendsCallback);

        verify(mRemoteDataSource).getFriends(mLoadFriendsCallbackCaptor.capture());
        mLoadFriendsCallbackCaptor.getValue().onDataNotAvailable();

        verify(mLocalDataSource).getFriends(any(FriendDataSource.LoadFriendsCallback.class));
    }

    @Test
    public void getFriendsWithBothDataSourceUnavailable_firesOnDataUnavailable() {
        mRepository.getFriends(mLoadFriendsCallback);

        // Both data source has no data available
        setFriendsNotAvailable(mRemoteDataSource);
        setFriendsNotAvailable(mLocalDataSource);

        verify(mLoadFriendsCallback).onDataNotAvailable();
    }

    @Test
    public void deleteAll_fromLocal() {
        mRepository.saveFriend(FRIENDS.get(0), mSaveCallback);

        mRepository.deleteAllFriends();

        verify(mLocalDataSource).deleteAllFriends();
    }

    @Test
    public void deleteFriend_fromLocal() {
        // Save the stubbed friends
        saveStubbedFriends(FRIENDS);

        Friend deletingFriend = FRIENDS.get(1);
        mRepository.deleteFriend(deletingFriend.getId());

        verify(mLocalDataSource).deleteFriend(eq(deletingFriend.getId()));

        assertThat(mRepository.mCacheFriends.containsKey(deletingFriend.getId()), is(false));
        assertThat(mRepository.mCacheFriends.size(), is(2));
    }

    private void setFriendsNotAvailable(FriendDataSource dataSource) {
        verify(dataSource).getFriends(mLoadFriendsCallbackCaptor.capture());
        mLoadFriendsCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void saveStubbedFriends(List<Friend> friends) {
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