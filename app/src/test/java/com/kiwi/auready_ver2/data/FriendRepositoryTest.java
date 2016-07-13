package com.kiwi.auready_ver2.data;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 7/5/16.
 */
public class FriendRepositoryTest {

    private FriendRepository mFriendRepository;

    @Mock
    private FriendDataSource mFriendLocalDataSource;
    @Mock
    private FriendDataSource.LoadFriendsCallback mLoadFriendsCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mFriendRepository = FriendRepository.getInstance(mFriendLocalDataSource);
    }
    @After
    public void destroyRepositoryInstance() {
        FriendRepository.destroyInstance();
    }

    @Test
    public void getFriends_requestsFriendsFromLocalDataSource() {
        mFriendRepository.getFriends(mLoadFriendsCallback);
        verify(mFriendLocalDataSource).getFriends(any(FriendDataSource.LoadFriendsCallback.class));
    }

    @Test
    public void saveFriend() {
        Friend newFriend = new Friend("email1");
        mFriendRepository.saveFriend(newFriend);

        verify(mFriendLocalDataSource).saveFriend(newFriend);
    }

    @Test
    public void deleteFriend_removedFromCache() {
        // Given a friend in the repository
        Friend friend = new Friend("email1");
        mFriendRepository.saveFriend(friend);
        assertThat(mFriendRepository.mCachedFriend.containsKey(friend.getId()), is(true));

        // When deleted
        mFriendRepository.deleteFriend(friend.getId());

        // Verify the data sources were called
        verify(mFriendLocalDataSource).deleteFriend(friend.getId());

        // Verify it's removed from repository
        assertThat(mFriendRepository.mCachedFriend.containsKey(friend.getId()), is(false));
    }
}