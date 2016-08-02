package com.kiwi.auready_ver2.friend;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.login.domain.usecase.GetFriend;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
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


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        mFriendPresenter = givenFriendPresenter();

        // We start the friends to 3.
        FRIENDS = Lists.newArrayList(new Friend("aa", "name1"), new Friend("bb", "name2"), new Friend("cc", "name3"));
    }

    private FriendPresenter givenFriendPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetFriend getFriend = new GetFriend(mFriendRepository);
        // SaveFriends

        return new FriendPresenter(useCaseHandler, mFriendView, getFriend);
    }

    @Test
    public void loadFriendsFromRepository() {
        // Given an initialized FriendsPresenter with initialized friends
        // When loading of Friends is requested

        mFriendPresenter.loadFriends();

        // Callback is captured and invoked with stubbed friends
//        verify(mFriendRepository).getFriends(mLoadFriendCallbackCaptor.capture());
//        mLoadFriendCallbackCaptor.getValue().onFriendsLoaded(FRIENDS);

        ArgumentCaptor<List> showFriendsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mFriendView).showFriends(showFriendsArgumentCaptor.capture());
        assertTrue(showFriendsArgumentCaptor.getValue().size() == 3);
    }

}