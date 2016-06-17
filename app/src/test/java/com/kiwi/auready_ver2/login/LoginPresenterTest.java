package com.kiwi.auready_ver2.login;

import com.kiwi.auready_ver2.rest_service.SignupInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/12/16.
 */
public class LoginPresenterTest {


    private LoginPresenter mLoginPresenter;

    private MockWebServer server;
    @Mock
    private LoginContract.View mLoginView;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mLoginPresenter = new LoginPresenter(mLoginView);

        server = new MockWebServer();
        server.start();
    }

    @Test
    public void email_isEmptyValue() {
        mLoginPresenter.validateEmail("");
        verify(mLoginView).showEmailError();
    }

    @Test
    public void password_isEmptyOrNullValue() {
        mLoginPresenter.validatePassword("");
        verify(mLoginView).showPasswordError();
    }

//    @Test
//    public void requestSignup_whenAccountCredentialsIsCorrect() {
//        String email = "dd@gmail.com";
//        String password = "123";
//        if(mLoginPresenter.validateAccountCredentials(email, password)) {
//            verify => mLoginPresenter.onRequestSignup(email, password);
//        }
//
//    }

    @Test
    public void showSignupFailMessage_whenSignupFail() {

        // Request signup to server with validated credentials
        String email = "dd@gmail.com";
        String password = "123";

        // When signup is requested
        mLoginPresenter.onRequestSignup(email, password);

        // Callback invoked with stubbed signupInfo
        SignupInfo signupInfo = new SignupInfo(email, password);

        // if received response 400 code,
        server.enqueue(new MockResponse()
                .setResponseCode(400));

        // Call onSignupFail()
        mLoginPresenter.onSignupFail();
        // and show the signup fail message
        verify(mLoginView).showSignupFailMessage();
    }

    @Test
    public void setSignupSuccessUi_whenSignupSucceed() {
        String email = "aa@gmail.com";
        mLoginPresenter.onSignupSuccess(email);
        verify(mLoginView).setSignupSuccessUI(email);
    }

}