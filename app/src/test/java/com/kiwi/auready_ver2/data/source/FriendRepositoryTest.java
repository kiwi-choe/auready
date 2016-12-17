package com.kiwi.auready_ver2.data.source;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    @Mock
    private FriendDataSource.LoadFriendsCallback mGetFriendsCallback;

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
    public void getFriends_requestsFriendsFromLocalDataSource() {

        mFriendRepository.getFriends(mGetFriendsCallback);
        verify(mFriendLocalDataSource).getFriends(any(
                FriendDataSource.LoadFriendsCallback.class));
    }

    @Test
    public void saveFriends() {
        // Given a stub friend list
        List<Friend> friends = Lists.newArrayList(
                new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));

        // When friends are saved to the friend repository
        mFriendRepository.saveFriends(friends);

        // Then the persistent repository are called
        verify(mFriendLocalDataSource).saveFriends(friends);
    }

    @Test
    public void saveFriend() {
        // Given a stub friend with title and description
        Friend newFriend = new Friend("aa@aa.com", "nameOfaa");

        // When a friend is saved to the friend repository
        mFriendRepository.saveFriend(newFriend);

        // Then the persistent repository are called
        verify(mFriendLocalDataSource).saveFriend(newFriend);
    }

    @Test
    public void deleteFriend() {
        Friend friend = new Friend("aa@aa.com", "nameOfaa");
        mFriendRepository.deleteFriend(friend.getId());

        verify(mFriendRemoteDataSource).deleteFriend(friend.getId());
        verify(mFriendLocalDataSource).deleteFriend(friend.getId());


    }
}