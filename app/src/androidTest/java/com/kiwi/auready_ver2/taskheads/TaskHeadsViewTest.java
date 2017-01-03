package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.FakeTaskHeadRemoteDataSource;
import com.kiwi.auready_ver2.data.source.TaskHeadRepository;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static com.kiwi.auready_ver2.StubbedData_forView.TASKHEADS;
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
        // Doing this in @Before avoid a race condition(duplicate data).
        Injection.provideTaskHeadRepository(InstrumentationRegistry.getTargetContext())
                .deleteAllTaskHeads();

        AccessTokenStore accessTokenStore = AccessTokenStore.getInstance(mActivity.getApplicationContext());
        accessTokenStore.logoutUser();
    }

    /*
    * Navigation View
    * */
    @Test
    public void clickOnAccountNavigationItem_showsLoginScreen() {
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
//
//    @Test
//    public void showNoTaskHeadView_whenNoTaskHead() {
//        // Create new taskHead
//        onView(withId(R.id.add_taskhead_bt)).perform(click());
//
//        // Show taskHead empty error message to snackbar
//        String msg = mActivity.getString(R.string.taskhead_empty_err);
//        onView(withText(msg)).check(matches(isDisplayed()));
//
//        // Show no taskHeadView
//        onView(withId(R.id.no_taskhead_txt)).check(matches(isDisplayed()));
//
//        onView(withId(R.id.taskhead_list))
//                .check(matches(not(isDisplayed())));
//    }
    @Test
    public void showTaskHeads() {
        loadTaskHeads();

        onView(withId(R.id.taskheads)).check(matches(isDisplayed()));

        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
        onView(withItemText(TITLE2)).check(matches(isDisplayed()));
        onView(withItemText(TITLE3)).check(matches(isDisplayed()));
    }

    @Test
    public void deleteTaskHead() {
        // Doing this in @Before avoid a race condition(duplicate data).
        Injection.provideTaskHeadRepository(InstrumentationRegistry.getTargetContext())
                .deleteAllTaskHeads();

        // Load 1 taskhead
        loadATaskHead();
        // there is a taskhead with TITLE1
        onView(withItemText(TITLE1)).check(matches(isDisplayed()));

        // click delete button
        onView(withId(R.id.reorder)).perform(click());
        // no taskheads in the listview
//        onView(withId(R.id.no_taskhead_txt)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddTaskHeadBt_opensAddTaskHeadUi() {
        onView(withId(R.id.fab_add_taskhead)).perform(click());
        // Check if the add taskHead screen is displayed
        onView(withId(R.id.taskheaddetail_title)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddTaskHeadBt_showCreateButton() {
        // Click addTaskHead button
        onView(withId(R.id.fab_add_taskhead)).perform(click());

        // Display Create button and gone Done button
        onView(withId(R.id.create_taskhead)).check(matches(isDisplayed()));
        onView(withId(R.id.done_taskhead)).check(matches(not(isDisplayed())));
    }
    /*
    * Useful methods for test
    * */
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
    private void loadATaskHead() {
        List<TaskHead> taskheads = Lists.newArrayList(new TaskHead(TITLE1, 0));
        startActivityWithStubbedTasks(taskheads);
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