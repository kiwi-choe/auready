package com.kiwi.auready.friend;

import com.kiwi.auready.TestUseCaseScheduler;
import com.kiwi.auready.UseCaseHandler;
import com.kiwi.auready.data.Friend;
import com.kiwi.auready.data.source.FriendDataSource;
import com.kiwi.auready.data.source.FriendRepository;
import com.kiwi.auready.friend.domain.usecase.DeleteFriend;
import com.kiwi.auready.friend.domain.usecase.GetFriends;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.kiwi.auready.StubbedData.FriendStub.FRIENDS;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/30/16.
 */
public class FriendsPresenterTest {

    private FriendsPresenter mFriendsPresenter;

    @Mock
    private FriendsContract.View mFriendView;

    @Mock
    private FriendRepository mFriendRepository;

    @Captor
    private ArgumentCaptor<FriendDataSource.LoadFriendsCallback> mLoadFriendsCallbackCaptor;



    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        mFriendsPresenter = givenFriendPresenter();
    }

    private FriendsPresenter givenFriendPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetFriends getFriends = new GetFriends(mFriendRepository);
        DeleteFriend deleteFriend = new DeleteFriend(mFriendRepository);

        return new FriendsPresenter(useCaseHandler, mFriendView, getFriends, deleteFriend);
    }

    @Test
    public void loadFriendsFromRepositoryAndLoadIntoView() {

        mFriendsPresenter.loadFriends();

        // Callback is captured and invoked with stubbed friends
        verify(mFriendRepository).getFriends(mLoadFriendsCallbackCaptor.capture());
        mLoadFriendsCallbackCaptor.getValue().onFriendsLoaded(FRIENDS);

        // Then progress indicator is shown
        verify(mFriendView).setLoadingIndicator(true);
        // is hidden and all friends are shown in UI
        verify(mFriendView).setLoadingIndicator(false);
        ArgumentCaptor<List> showFriendsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mFriendView).showFriends(showFriendsArgumentCaptor.capture());
        assertTrue(showFriendsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void deleteFriendFromRepo_updateView() {
        Friend friend = FRIENDS.get(0);
        mFriendsPresenter.deleteFriend(friend.getUserId());

        // Verify deleteFriend is called
        verify(mFriendRepository).deleteFriend((eq(friend.getUserId())));
        // Update view
    }
}