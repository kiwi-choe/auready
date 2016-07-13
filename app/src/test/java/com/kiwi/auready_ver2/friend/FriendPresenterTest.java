package com.kiwi.auready_ver2.friend;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.FriendDataSource.LoadFriendsCallback;
import com.kiwi.auready_ver2.data.FriendRepository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/30/16.
 */
public class FriendPresenterTest {

    private static ArrayList<Friend> FRIENDS;

    private FriendPresenter mFriendPresenter;

    @Mock
    private FriendContract.View mFriendView;

    @Mock
    private FriendRepository mFriendRepository;

    @Captor
    private ArgumentCaptor<LoadFriendsCallback> mLoadFriendCallbackCaptor;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);


        mFriendPresenter = new FriendPresenter(mFriendView, mFriendRepository);

        // We start the friends to 3.
        FRIENDS = Lists.newArrayList(new Friend("aa"), new Friend("bb"), new Friend("cc"));
    }

    @Test
    public void loadFriendsFromRepository() {
        // Given an initialized FriendsPresenter with initialized friends
        // When loading of Friends is requested

        mFriendPresenter.loadFriends();

        // Callback is captured and invoked with stubbed friends
        verify(mFriendRepository).getFriends(mLoadFriendCallbackCaptor.capture());
        mLoadFriendCallbackCaptor.getValue().onFriendsLoaded(FRIENDS);

        ArgumentCaptor<List> showFriendsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mFriendView).showFriends(showFriendsArgumentCaptor.capture());
        assertTrue(showFriendsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void deleteFriendFromRepository() {

        // Given a friend in the repository
        Friend friend = new Friend("email1");
        mFriendRepository.saveFriend(friend);
        assertNotSame("", friend.getId());

        mFriendPresenter.deleteFriend(friend.getId());

        verify(mFriendRepository).deleteFriend(friend.getId());
        verify(mFriendView).showFriendDeleted();
    }

}