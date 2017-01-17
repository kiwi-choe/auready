package com.kiwi.auready_ver2.friend;

import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.SearchedUser;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.domain.usecase.AddFriend;
import com.kiwi.auready_ver2.friend.domain.usecase.FindPeople;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 8/12/16.
 */
public class FindPresenterTest {

    private static final String EMAIL = "aa@a.com";
    private static final String NAME = "nameOfaa";

    @Mock
    private FriendRepository mFriendRepository;
    @Mock
    private FindContract.View mFindView;

    private FindPresenter mFindPresenter;
    @Mock
    private FriendDataSource.LoadSearchedPeopleCallback loadSearchedPeopleCallback;
    @Captor
    private ArgumentCaptor<FriendDataSource.LoadSearchedPeopleCallback> mLoadCallbackCaptor;
    @Captor
    private ArgumentCaptor<FriendDataSource.AddFriendCallback> mAddFriendCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mFindPresenter = givenFindPresenter();
    }

    private FindPresenter givenFindPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveFriend saveFriend = new SaveFriend(mFriendRepository);
        FindPeople findPeople = new FindPeople(mFriendRepository);
        AddFriend addFriend = new AddFriend(mFriendRepository);

        return new FindPresenter(useCaseHandler, mFindView, saveFriend, findPeople, addFriend);
    }

    // for Testing
    @Test
    public void saveFriendAndShowSuccessMsg() {

        // Given a new Friend
        final Friend newFriend = new Friend(EMAIL, NAME);
        mFindPresenter.saveFriend(newFriend);
        FriendDataSource.SaveCallback saveCallback = Mockito.mock(FriendDataSource.SaveCallback.class);
        verify(mFriendRepository).saveFriend(newFriend, saveCallback);
    }

    @Test
    public void findPeople() {
        String emailOrName = "emailOrName";
        mFindPresenter.findPeople(emailOrName);

        verify(mFriendRepository).findPeople(eq(emailOrName), mLoadCallbackCaptor.capture());
    }

    @Test
    public void addFriend() {
        SearchedUser user = new SearchedUser("email", 0);
        mFindPresenter.addFriend(user);
        verify(mFriendRepository).addFriend(eq(user), mAddFriendCallbackCaptor.capture());
        mAddFriendCallbackCaptor.getValue().onSuccessAddFriend();
        // Update view
        verify(mFindView).setViewWhenAddFriendSucceed(user);
    }
}