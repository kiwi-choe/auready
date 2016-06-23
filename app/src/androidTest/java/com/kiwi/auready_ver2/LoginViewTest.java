package com.kiwi.auready_ver2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginViewTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void TestShowEmailError() {

        // Edit wrong email
        String editWrongEmail = "wrongEmail";
        onView(withId(R.id.ed_email))
                .perform(replaceText(editWrongEmail), closeSoftKeyboard());
        // Verify show email error of stringResourceName
        onView(withId(R.id.ed_email)).check(matches(hasErrorText("dd")));
    }
}