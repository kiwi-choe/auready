package com.kiwi.auready_ver2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.login.LoginActivity;
import com.kiwi.auready_ver2.login.LoginContract;
import com.kiwi.auready_ver2.login.LoginPresenter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    private LoginContract.UserActionsListener mLoginPresenter;

    @Before
    public void setUp() {
    }

    @Test
    public void clickSignupButton_validateSignupCredentials() {

        String email = "aa@gmail.com";
        String password = "123";

        // Click on the signup button
        onView(withId(R.id.bt_signup_complete)).perform(click());

        mLoginPresenter = Mockito.mock(LoginPresenter.class);
        // Call onRequestSignup in LoginPresenter
        verify(mLoginPresenter).validateAccountCredentials(email, password);
    }

    @Test
    public void clickLoginButton_validateAccountCredentials() {

        String email = "aa@gmail.com";
        String password = "123";

        onView(withId(R.id.bt_login_complete)).check(matches(isClickable()));

        onView(withId(R.id.bt_login_complete)).perform(click());

        mLoginPresenter = Mockito.mock(LoginPresenter.class);
        // Call onRequestSignup in LoginPresenter
        verify(mLoginPresenter).validateAccountCredentials(email, password);
    }
}