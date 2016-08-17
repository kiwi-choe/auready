package com.kiwi.auready_ver2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.kiwi.auready_ver2.tasks.TasksActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.kiwi.auready_ver2.custom.action.NavigationViewActions.navigateTo;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class TasksViewTest {

    @Rule
    public ActivityTestRule<TasksActivity> mActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class);

    @Test
    public void clickOnAndroidHomeIcon_OpensNavigation() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)));

        // Open Drawer
        onView(withContentDescription("Navigate up")).perform(click());


        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)));
    }
    @Test
    public void clickOnLoginNavigationItem_showsLoginScreen() {
        // Open Drawer to click on navigation
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        // Start Login screen
        onView(withId(R.id.nav_header_account_layout))
                .perform(click());

        // Check that Login Activity was opened
        onView(withId(R.id.ed_email)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnFriendNavigationItem_showsFriendView() {
        // Open Drawer to click on navigation
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        // Start Friend view
        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.friend_navigation_menu_item));

        // Check that FriendActivity was opened.
        onView(withId(R.id.friend_list_layout)).check(matches(isDisplayed()));
    }


    @Test
    public void setSuccessUi_whenLoginSuccess() {

        // Stub of logged in name and email
        String loggedInName = "nameOfaa";
        String loggedInEmail = "aaa@aaa.aaa";

        // temp event for test
        onView(withId(R.id.test_fragment_tasks))
                .perform(click());

        // Set Member's view
        // 1. Set loggedInEmail to nav_name and nav_email
        onView(withId(R.id.nav_name))
                .check(matches(withText(loggedInName)));
        onView(withId(R.id.nav_email))
                .check(matches(withText(loggedInEmail)));
        // 2. Open NavigationDrawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START)));
    }

    @Test
    public void openAddTaskView() {

        onView(withId(R.id.fab_add_task)).perform(click());

        // open AddEditTaskView

    }
}