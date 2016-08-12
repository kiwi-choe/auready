package com.kiwi.auready_ver2.friend;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by kiwi on 8/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class FindViewTest {

    @Rule
    public ActivityTestRule<FindActivity> mActivityTestRule =
            new ActivityTestRule<>(FindActivity.class);

    @Test
    public void clickOnSaveFriendButton_ShowSuccessMsg() {

        onView(withId(R.id.bt_test_save_friend))
                .perform(click());
    }
}