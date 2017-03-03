package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.TestUseCaseScheduler;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.rest_service.login.LoginResponse;
import com.kiwi.auready_ver2.data.source.FriendRepository;
import com.kiwi.auready_ver2.friend.FriendsContract;
import com.kiwi.auready_ver2.rest_service.login.MockSuccessLoginService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

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

        verify(mLoginView).setLoginSuccessUI(eq(EMAIL), eq(loginResponse.getUserName()));
    }


    @Test
    public void setLoggedInUserInfo_whenLoginSuccess() {

        LoginResponse successResponse = MockSuccessLoginService.RESPONSE;
        mLoginPresenter.onLoginSuccess(successResponse, EMAIL);

        verify(mLoginView).setLoggedInUserInfo(
                eq(successResponse.getAccessToken()),
                eq(EMAIL),
                eq(successResponse.getUserName()));
    }

    @Test
    public void showLoginFailMessage_whenLoginFailed_byInvalidUserInfo() throws IOException {

        mLoginPresenter.onLoginFail(R.string.login_fail_message_400);
        verify(mLoginView).showLoginFailMessage(R.string.login_fail_message_400);
    }

    private LoginPresenter givenLoginPresenter() {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());

        return new LoginPresenter(useCaseHandler, mLoginView);
    }
}