package com.kiwi.auready_ver2;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import com.kiwi.auready_ver2.login.LoginActivity;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    public static Matcher<View> hasErrorText(final String expectedError) {
        return new BoundedMatcher<View, View>(View.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedError);
            }

            @Override
            protected boolean matchesSafely(View view) {

                if (!(view instanceof EditText)) {
                    return false;
                }

                EditText editText = (EditText) view;

                return expectedError.equals(editText.getError());
            }
        };
    }

    /*
    * Signup and Login Button
    * */
    @Test
    public void showEmailFormatError_whenClickSignupOrLoginButton() {

        // Try to signup with wrong email
        String editWrongEmail = "wrong email";
        onView(withId(R.id.ed_email))
                .perform(replaceText(editWrongEmail), closeSoftKeyboard());
        onView(withId(R.id.ed_password))
                .perform(replaceText("123"), closeSoftKeyboard());
        //onView(withId(R.id.bt_signup_complete)).perform(click());
        onView(withId(R.id.bt_login_complete)).perform(click());

        // Verify show email error of stringResourceName
        Assert.assertEquals(mLoginActivityTestRule.getActivity().getString(R.string.email_format_err), "email format is invalid");
//        onView(withId(R.id.ed_email)).check(matches(hasErrorText(
//                mLoginActivityTestRule.getActivity().getString(R.string.email_format_err))));
    }

    @Test
    public void showEmailEmptyError_whenClickSignupOrLoginButton() {

        String editWrongEmail = "";
        onView(withId(R.id.ed_email))
                .perform(replaceText(editWrongEmail), closeSoftKeyboard());
        onView(withId(R.id.ed_password))
                .perform(replaceText("123"), closeSoftKeyboard());
//        onView(withId(R.id.bt_signup_complete)).perform(click());
        onView(withId(R.id.bt_login_complete)).perform(click());

        // Verify show email error of stringResourceName
        onView(withId(R.id.ed_email)).check(matches(hasErrorText(
                mLoginActivityTestRule.getActivity().getString(R.string.email_empty_err))));
    }

    @Test
    public void showPasswordError_whenClickSignupOrLoginButton() {

        // Try to signup with empty password
        String editEmail = "aaa@aaa.aaa";
        String editEmptyPassword = "";
        onView(withId(R.id.ed_email))
                .perform(replaceText(editEmail), closeSoftKeyboard());
        onView(withId(R.id.ed_password))
                .perform(replaceText(editEmptyPassword), closeSoftKeyboard());
        onView(withId(R.id.bt_signup_complete)).perform(click());
//        onView(withId(R.id.bt_login_complete)).perform(click());

//        Assert.assertEquals(mLoginActivityTestRule.getActivity().getString(R.string.password_empty_err), "password is empty");
        // Verify show email error of stringResourceName
        onView(withId(R.id.ed_password)).check(matches(hasErrorText(
                mLoginActivityTestRule.getActivity().getString(R.string.password_empty_err))));
    }


    @Test
    public void sendResultToTasks_whenLoginSucceed() {


        //q 메인(TasksActivity)의 텍스트뷰에 대한 테스트는 어떻게 하지?
        // q How to check that LoginActivity is finished?
        // Finish LoginActivity

        // Check that TasksActivity was opened and NavigationDrawer was opened.
        onView(withId(R.id.test_fragment_tasks)).check(matches(isDisplayed()));

//        // set the logged in email to nav_header_email
//        onView(withId(R.id.nav_header_email))
//                .check(matches(withText("aaa@aaa.aaa")));
    }
}