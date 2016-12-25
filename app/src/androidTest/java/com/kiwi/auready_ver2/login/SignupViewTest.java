package com.kiwi.auready_ver2.login;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.R;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by kiwi on 7/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class SignupViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);


    /* Q
    * I want to test this fragment in isolation.I do not want to use the actual parent activity.
    * Problem on this,
    * If using LoginActivity, be called only LoginFragment didn't called SignupFragment.
    * */
    @Test
    public void clickSignupWithInvalidFormatEmail_showEmailFormatError() {

        // Replace SignupView
        onView(withId(R.id.bt_signup_open)).perform(click());

        // Try to signup with wrong email
        String editWrongEmail = "wrong email";
        onView(withId(R.id.ed_signup_email))
                .perform(replaceText(editWrongEmail), closeSoftKeyboard());
        onView(withId(R.id.ed_signup_password))
                .perform(replaceText("123"), closeSoftKeyboard());
        onView(withId(R.id.bt_signup_complete)).perform(click());

        // Verify show email error of stringResourceName
        Assert.assertEquals(mLoginActivityTestRule.getActivity().getString(R.string.email_format_err), "email format is invalid");
    }

}