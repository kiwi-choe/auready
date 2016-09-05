package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.remote.FakeTaskRemoteDataSource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by kiwi on 9/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class TasksViewTest {

    private static final String TASKHEAD_ID = "stubTaskHeadId";
    private static final String TASK_DESCRIPTION1 = "someday";
    private static final String TASK_DESCRIPTION2 = "we will know";
    private static final String TASK_DESCRIPTION3 = "OK?";

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    // 3 tasks, one active and two completed
    private static List<Task> TASKS = Lists.newArrayList(new Task(TASKHEAD_ID, TASK_DESCRIPTION1),
            new Task(TASKHEAD_ID, TASK_DESCRIPTION2, true), new Task(TASKHEAD_ID, TASK_DESCRIPTION3, true));

    @Rule
    public ActivityTestRule<TasksActivity> mTasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class, true /* Initial touch mode */,
                    false /* Lazily launch activity */);


    @Test
    public void showTasks() {
        loadTasks();

        onView(withId(R.id.task_list)).check(matches(isDisplayed()));
        onView(withItemText(TASK_DESCRIPTION1)).check(matches(isDisplayed()));
        onView(withItemText(TASK_DESCRIPTION2)).check(matches(isDisplayed()));
        onView(withItemText(TASK_DESCRIPTION3)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.complete), hasSibling(withText(TASK_DESCRIPTION1))))
                .check(matches(not(isChecked())));
        onView(allOf(withId(R.id.complete), hasSibling(withText(TASK_DESCRIPTION2))))
                .check(matches(not(isChecked())));
        onView(allOf(withId(R.id.complete), hasSibling(withText(TASK_DESCRIPTION3))))
                .check(matches(not(isChecked())));
    }

    //q Check the position of AddTaskViewButton?
    @Test
    public void showAddTaskButton() {
        loadTasks();

        onView(withId(R.id.task_list)).check(matches(isDisplayed()));

        onView(withId(R.id.add_taskview_bt)).check(matches(isDisplayed()));
    }

    private void loadTasks() {
        startActivityWithStubbedTasks(TASKS, TASKHEAD_ID);
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

    /*
    * Setup the test fixture with a fake taskHead id. The {@link TasksActivity} is started with
    * a particular taskHead id, which is then loaded from the service API.
    *
    * Note that this test runs hermetically and is fully isolated using a fake implementation of
    * the service API. This is a great way to make the tests more reliable and faster at the same
    * time, since they are isolated from any outside dependencies.
    * */
    private void startActivityWithStubbedTasks(List<Task> tasks, String taskHeadId) {
        // Add tasks stub to the fake service api layer.
        TaskRepository.destroyInstance();
        FakeTaskRemoteDataSource.getInstance().addTasks(taskHeadId, tasks);

        // Lazily start Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(TasksActivity.EXTRA_TASKHEAD_ID, taskHeadId);
        mTasksActivityTestRule.launchActivity(startIntent);
    }
}