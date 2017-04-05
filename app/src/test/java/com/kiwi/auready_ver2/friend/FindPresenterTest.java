package com.kiwi.auready_ver2.friend;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.SearchedUser;
import com.kiwi.auready_ver2.data.User;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.domain.usecase.SaveFriend;
import com.kiwi.auready_ver2.rest_service.friend.MockSuccessFriendService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
    @Captor
    private ArgumentCaptor<FriendDataSource.SaveCallback> mSaveCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mFindPresenter = givenFindPresenter();
    }

    private FindPresenter givenFindPresenter() {

        String accessToken = "stubbedAccessToken";
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        SaveFriend saveFriend = new SaveFriend(mFriendRepository);

        return new FindPresenter(accessToken, useCaseHandler, mFindView, saveFriend);
    }

    // for Testing
    @Test
    public void saveFriendAndShowSuccessMsg() {

        // Given a new Friend
        final Friend newFriend = new Friend(EMAIL, NAME);
        mFindPresenter.saveFriend(newFriend);
        verify(mFriendRepository).saveFriend(eq(newFriend), mSaveCallbackCaptor.capture());
    }

    @Test
    public void findPeople_whenSucceed() {
        List<SearchedUser> searchedUsers = MockSuccessFriendService.SEARCHED_PEOPLE;
        mFindPresenter.onFindPeopleSucceed(searchedUsers);

        verify(mFindView).showSearchedPeople(searchedUsers);
    }

    @Test
    public void findPeople_whenFail() {
        mFindPresenter.onFindPeopleFail();

        verify(mFindView).showNoSearchedPeople();
    }

    @Test
    public void addFriend_whenSucceed() {
        User userInfo = new User("userid", "useremail", "username");
        SearchedUser user = new SearchedUser(userInfo, SearchedUser.NO_STATUS);
        mFindPresenter.onAddFriendSucceed(user.getUserInfo().getName());
        
        // Update view
        verify(mFindView).setAddFriendSucceedUI(user.getUserInfo().getName());
    }
    @Test
    public void addFriend_whenFail() {
        int stringResource = R.string.addfriend_fail_msg_onfailure;
        mFindPresenter.onAddFriendFail(stringResource);

        // Update view
        verify(mFindView).setAddFriendFailMessage(stringResource);
    }
}