package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.api_model.LoginResponse;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.FriendsContract;
import com.kiwi.auready_ver2.login.domain.usecase.InitFriend;
import com.kiwi.auready_ver2.rest_service.MockSuccessLoginService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit test LoginPresenter
 */
public class LoginPresenterTest {

    private static final String MY_ID_OF_FRIEND = "stubbedId";
    private static final String EMAIL = "dd@gmail.com";
    private static final String PASSWORD = "123";
    private LoginPresenter mLoginPresenter;

    @Mock
    private LoginContract.View mLoginView;

    @Mock
    private FriendRepository mFriendRepository;

    @Mock
    private FriendsContract.View mFriendView;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mLoginPresenter = givenLoginPresenter();
    }

    @Test
    public void setLoginSuccessUi_whenLoginSucceed() {

        // Succeed to request login
        LoginResponse loginResponse = MockSuccessLoginService.RESPONSE;
        mLoginPresenter.onLoginSuccess(loginResponse, EMAIL);

        verify(mLoginView).setLoginSuccessUI(eq(EMAIL), eq(loginResponse.getName()));
    }

    @Test
    public void initFriends_whenLoginSuccess() {

        mLoginPresenter.onLoginSuccess(MockSuccessLoginService.RESPONSE, EMAIL);

        // Create ME friend object and Add to friendList
        List<Friend> friendList = MockSuccessLoginService.RESPONSE.getFriends();
        Friend friend = new Friend(EMAIL, MockSuccessLoginService.RESPONSE.getName());
        friendList.add(friend);
        // ME and FRIENDS is saved in the repository
        verify(mFriendRepository).initFriend(friendList);
    }

    @Test
    public void setLoggedInUserInfo_whenLoginSuccess() {

        LoginResponse successResponse = MockSuccessLoginService.RESPONSE;
        mLoginPresenter.onLoginSuccess(successResponse, EMAIL);

        verify(mLoginView).setLoggedInUserInfo(
                eq(successResponse.getTokenInfo()), eq(EMAIL),
                eq(successResponse.getName()), any(String.class));
    }

    @Test
    public void showLoginFailMessage_whenLoginFailed_byInvalidUserInfo() throws IOException {

        mLoginPresenter.onLoginFail(R.string.login_fail_message_400);
        verify(mLoginView).showLoginFailMessage(R.string.login_fail_message_400);
    }

    @Test
    public void setLogoutUi_whenLogoutSuccess() {
        String accessToken = "accesstoken stub";
        mLoginPresenter.requestLogout(accessToken);
        // succeed to request requestLogout
        mLoginPresenter.onLogoutSuccess();
        verify(mLoginView).setLogoutSuccessResult();
    }

    private LoginPresenter givenLoginPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        InitFriend InitFriend = new InitFriend(mFriendRepository);

        return new LoginPresenter(useCaseHandler, mLoginView, InitFriend);
    }
}