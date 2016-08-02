package com.kiwi.auready_ver2.data.source;

import com.kiwi.auready_ver2.data.Friend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class FriendRepositoryTest {

    private static final String FRIEND_COL_ID = "friendColumnId";
    private FriendRepository mFriendRepository;

    @Mock
    private FriendDataSource mFriendRemoteDataSource;
    @Mock
    private FriendDataSource mFriendLocalDataSource;

    @Mock
    private FriendDataSource.GetFriendCallback mGetFriendCallback;

    @Before
    public void setupFriendRepository() {
        MockitoAnnotations.initMocks(this);
        // Get a reference to the class under test
        mFriendRepository = FriendRepository.getInstance(
                mFriendRemoteDataSource, mFriendLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        FriendRepository.destroyInstance();
    }

    @Test
    public void getFriend_requestsAnFriendFromLocalDataSource() {
        // When a friend is requested from the friend repository
        mFriendRepository.getFriend(FRIEND_COL_ID, mGetFriendCallback);

        // Then the friend is loaded from the database
        verify(mFriendLocalDataSource).getFriend(eq(FRIEND_COL_ID), any(
                FriendDataSource.GetFriendCallback.class));
    }

    @Test
    public void saveFriend_savesFriendToServiceAPI() {
        // Given a stub friend with email and name
        String stubEmail = "aa@aa.com";
        String stubName = "nameOfaa";
        Friend newFriend = new Friend(stubEmail, stubName);

        mFriendRepository.saveFriend(newFriend);

        // Then the service API and persistent repository are called
        verify(mFriendRemoteDataSource).saveFriend(newFriend);
        verify(mFriendLocalDataSource).saveFriend(newFriend);
    }
}