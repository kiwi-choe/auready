package com.kiwi.auready_ver2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import com.kiwi.auready_ver2.taskheads.TaskHeadActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static com.kiwi.auready_ver2.custom.action.NavigationViewActions.navigateTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class TaskHeadsViewTest {

    private static final String TITLE1 = "title1";

    @Rule
    public ActivityTestRule<TaskHeadActivity> mActivityTestRule =
            new ActivityTestRule<>(TaskHeadActivity.class);

    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(ListView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("isDescendantOfA ListView with Text " + itemText);
            }
        };
    }

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


//    @Test
//    public void setSuccessUi_whenLoginSuccess() {
//
//        // Stub of logged in name and email
//        String loggedInName = "nameOfaa";
//        String loggedInEmail = "aaa@aaa.aaa";
//
//        // temp event for test
//        onView(withId(R.id.test_fragment_tasks))
//                .perform(click());
//
//        // Set Member's view
//        // 1. Set loggedInEmail to nav_name and nav_email
//        onView(withId(R.id.nav_name))
//                .check(matches(withText(loggedInName)));
//        onView(withId(R.id.nav_email))
//                .check(matches(withText(loggedInEmail)));
//        // 2. Open NavigationDrawer
//        onView(withId(R.id.drawer_layout))
//                .check(matches(isOpen(Gravity.START)));
//    }

    @Test
    public void openAddTaskView() {

        onView(withId(R.id.fab_add_task)).perform(click());
        // open TaskView
        onView(withId(R.id.add_taskview_bt)).check(matches(isDisplayed()));
    }

//  @Test  openAddTaskViewWithTitleOfTaskHead_OnToolbar

    @Test
    public void showTaskHeads() {
        // Check that set visible to taskHeadView_layout
        onView(withId(R.id.taskheads_view))
                .check(matches(isDisplayed()));
        // and set gone to no_taskheadview_layout
        onView(withId(R.id.no_taskhead_view))
                .check(matches(not(isDisplayed())));

        // Verify that all taskHeads are shown.
        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
    }

    @Test
    public void deleteOnLongClickedTaskHeadItem() {

        onView(withText(TITLE1)).perform(click());
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())));
    }
}