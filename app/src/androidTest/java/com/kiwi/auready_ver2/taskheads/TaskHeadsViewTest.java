package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.api_model.TokenInfo;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.data.source.remote.FakeTaskHeadRemoteDataSource;
import com.kiwi.auready_ver2.login.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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
import static com.google.common.base.Preconditions.checkNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by kiwi on 6/15/16.
 */
@RunWith(AndroidJUnit4.class)
public class TaskHeadsViewTest {

    private static final String TITLE1 = "Don't be hurry";
    private static final String TITLE2 = "just";
    private static final String TITLE3 = "keep going";

    private static final String USER_NAME = "KIWIYA";
    private static final String USER_EMAIL = "KIWIYA@gmail.com";

    /*
    * {@link TaskHead} stub that is added to the fake service API layer.
    * */
    private static List<TaskHead> TASKHEADS = Lists.newArrayList(new TaskHead(TITLE1),
            new TaskHead(TITLE2), new TaskHead(TITLE3));


    private TaskHeadsActivity mActivity;

//    @Rule
//    public ActivityTestRule<TaskHeadsActivity> mActivityTestRule =
//            new ActivityTestRule<>(TaskHeadsActivity.class, true, false);

    @Rule
    public ActivityTestRule<TaskHeadsActivity> mActivityTestRule =
            new ActivityTestRule<>(TaskHeadsActivity.class);

    @Before
    public void setup() {
        mActivity = mActivityTestRule.getActivity();
    }

    @After
    public void cleanup() {
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(mActivity.getApplicationContext());
        accessTokenStore.logoutUser();
    }
    @Test
    public void clickOnAndroidHomeIcon_OpensNavigation() {

        // Check that left drawer is opened at startup
        onView(ViewMatchers.withId(R.id.drawer_layout))
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
        onView(withId(R.id.bt_account_settings))
                .perform(click());

        // Check that Login Activity was opened
        onView(withId(R.id.ed_email)).check(matches(isDisplayed()));
    }

    @Test
    public void setMemberNavView_whenLoggedIn() {
        // Set logged in status
        setLoggedIn(USER_NAME, USER_EMAIL);

        // Open Drawer to click on navigation
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        onView(withId(R.id.nav_email)).check(matches(isDisplayed()));

        onView(withId(R.id.bt_manage_friend)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnBtManageFriend_showsFriendView() {

        setLoggedIn(USER_NAME, USER_EMAIL);

        // Open Drawer to click on navigation
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        onView(withId(R.id.bt_manage_friend)).perform(click());

        // Check that FriendActivity was opened.
        onView(withId(R.id.friend_list_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void setSuccessUi_whenLoginSuccess() {

        // Stub of logged in name and email
        String loggedInName = "nameOfaa";
        String loggedInEmail = "aaa@aaa.aaa";

        setLoggedIn(loggedInName, loggedInEmail);

        // Open Drawer to click on navigation
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open());

        // Set loggedInEmail to nav_name and nav_email
        onView(withId(R.id.nav_name))
                .check(matches(withText(loggedInName)));
        onView(withId(R.id.nav_email))
                .check(matches(withText(loggedInEmail)));
    }

    @Test
    public void openTasksView() {

        onView(withId(R.id.add_taskhead_bt)).perform(click());
        // open TaskView
        onView(withId(R.id.add_taskview_bt)).check(matches(isDisplayed()));
    }

    @Test
    public void showNoTaskHeadView_whenNoTaskHead() {
        // Create new taskHead
        onView(withId(R.id.add_taskhead_bt)).perform(click());

        // Show taskHead empty error message to snackbar
        String msg = mActivity.getString(R.string.taskhead_empty_err);
        onView(withText(msg)).check(matches(isDisplayed()));

        // Show no taskHeadView
        onView(withId(R.id.no_taskhead_txt)).check(matches(isDisplayed()));

        onView(withId(R.id.taskhead_list))
                .check(matches(not(isDisplayed())));
    }
    @Test
    public void showTaskHeads() {
        loadTaskHeads();

        onView(withId(R.id.taskhead_list)).check(matches(isDisplayed()));

        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
        onView(withItemText(TITLE2)).check(matches(isDisplayed()));
        onView(withItemText(TITLE3)).check(matches(isDisplayed()));
    }

    @Test
    public void onLongClickedTaskHeadItem_deleteTaskHead() {

        // Given taskHead stubs
        loadTaskHeads();

        // On longClick the item with TITLE2
        onView(withText(TITLE2)).perform(longClick());

        // Verify one taskHead was deleted
        onView(withItemText(TITLE2)).check(doesNotExist());
    }

    @Test
    public void onClickTaskHeadItem_openTaskHead() {
        loadTaskHeads();

        onView(withText(TITLE1)).perform(click());

        onView(withText(TITLE1)).check(matches(isDisplayed()));
//        onView(withId(R.id.task_list)).check(matches(isDisplayed()));
    }

    @Test
    public void addNewTaskHeadToTaskHeadList() {
        // Create a taskHead
        onView(withId(R.id.add_taskhead_bt)).perform(click());

        // Verify taskHead is displayed on screen
        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
    }

    /*
    * Useful methods for test
    * */
    private void setLoggedIn(String userName, String userEmail) {
        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(mActivity.getApplicationContext());
        TokenInfo tokenInfo = new TokenInfo("", "");
        accessTokenStore.save(tokenInfo, userName, userEmail);

        assertEquals(userName, accessTokenStore.getStringValue(AccessTokenStore.USER_NAME, ""));
        assertEquals(userEmail, accessTokenStore.getStringValue(AccessTokenStore.USER_EMAIL, ""));

        accessTokenStore.setLoggedInStatus();
        assertTrue(accessTokenStore.isLoggedIn());
    }

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

    private void loadTaskHeads() {
        startActivityWithStubbedTasks(TASKHEADS);
    }
    private void startActivityWithStubbedTasks(List<TaskHead> taskHeads) {
        // Add tasks stub to the fake service api layer.
        TaskHeadRepository.destroyInstance();
        FakeTaskHeadRemoteDataSource.getInstance().addTaskHeads(taskHeads);

        // Lazily start Activity from the ActivityTestRule this time to inject the start Intent.
        Intent startIntent = new Intent();
        mActivityTestRule.launchActivity(startIntent);

    }
}