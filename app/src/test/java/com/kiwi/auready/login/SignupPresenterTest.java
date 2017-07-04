package com.kiwi.auready.login;

import com.kiwi.auready.R;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/12/16.
 */
public class SignupPresenterTest {


    private SignupPresenter mSignupPresenter;

    @Mock
    private SignupContract.View mSignupView;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mSignupPresenter = new SignupPresenter(mSignupView);
    }

    @Test
    public void email_isEmptyValue() {
        mSignupPresenter.validateEmail("");
        verify(mSignupView).showEmailError(anyInt());
    }

    @Test
    public void email_isNull() {
        mSignupPresenter.validateEmail(null);
        verify(mSignupView).showEmailError(anyInt());
    }

    @Test
    public void email_isCorrectFormat() {

        boolean isCorrect = mSignupPresenter.validateEmail("aaa@aaa.a");
        Assert.assertFalse(isCorrect);

        isCorrect = mSignupPresenter.validateEmail("aaa@aaa.aaa");
        Assert.assertTrue(isCorrect);
    }

    @Test
    public void password_isEmptyValue() {
        mSignupPresenter.validatePassword("");
        verify(mSignupView).showPasswordError(anyInt());
    }

    @Test
    public void password_isNull() {
        mSignupPresenter.validatePassword(null);
        verify(mSignupView).showPasswordError(anyInt());
    }

    @Test
    public void setSignupSuccessUi_whenEmailAndPasswordIsValid() {

        // Create the signupInfo stub
        String email = "dd@gmail.com";
        String name = "nameOfdd";

        // Request signup to Server
        mSignupPresenter.onSignupSuccess(email, name);
        verify(mSignupView).setSignupSuccessUI(email, name);
    }

    @Test
    public void showSignupFailMessage_whenEmailAndPasswordIsInvalid() throws IOException {

        // When requests signup to server with invalid credentials
        // Failed to request signup
        // with error code 404, reason: email is already registered.
        mSignupPresenter.onSignupFail(R.string.signup_fail_message_404);
        verify(mSignupView).showSignupFailMessage(R.string.signup_fail_message_404);
    }
}