package com.kiwi.auready_ver2.taskheaddetail;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.Injection;
import com.kiwi.auready_ver2.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by kiwi on 12/9/16.
 */
@RunWith(AndroidJUnit4.class)
public class TaskHeadDetailViewTest {

    @Rule
    public ActivityTestRule<TaskHeadDetailActivity> mActivityTestRule =
            new ActivityTestRule<>(TaskHeadDetailActivity.class);


    @Test
    public void errorShownOnEmptyTask() {
        // Add task title and description
        onView(withId(R.id.taskheaddetail_title)).perform(typeText(""), closeSoftKeyboard());

        // Save the task
        onView(withId(R.id.create_taskhead)).perform(click());

        // Verify empty tasks snackbar is shown
        String emptyTaskMessageText = getTargetContext().getString(R.string.empty_taskhead_message);
        onView(withText(emptyTaskMessageText)).check(matches(isDisplayed()));
    }

    @Test
    public void clickCreateButton_showTasksView() {
        // Add task title and description
        onView(withId(R.id.taskheaddetail_title)).perform(typeText("titleOfTaskhead!!!"), closeSoftKeyboard());

        onView(withId(R.id.create_taskhead)).perform(click());

        // Show one of the view on TasksView
        onView(withId(R.id.tasks_toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void clickCancelButton_showTaskHeadsView() {
        onView(withId(R.id.cancel_taskhead)).perform(click());

        // Show back view (TaskHeadsView)
        onView(withId(R.id.taskheads)).check(matches(isDisplayed()));
    }
}