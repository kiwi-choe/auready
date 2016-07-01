package com.kiwi.auready_ver2.friend;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.FriendRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

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

        mFriendPresenter = new FriendPresenter(mFriendView);

        // We start the friends to 3.
        FRIENDS = Lists.newArrayList(new Friend("aa"), new Friend("bb"), new Friend("cc"));
    }
    @Test
    public void loadFriendsFromRepository() {
        // Given an initialized FriendsPresenter with initialized friends
        // When loading of Friends is requested

        mFriendPresenter.start();

    }
}