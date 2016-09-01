package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.remote.FakeTaskRemoteDataSource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by kiwi on 9/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class TasksViewTest {

    private static final String TASKHEAD_ID = "stubTaskHeadId";
    private static final String TASK_DESCRIPTION1 = "someday";
    private static final String TASK_DESCRIPTION2 = "we will know";

    /*
    * {@link Task}s stub that is added to the fake service API layer.
    * */
    private static List<Task> ACTIVE_TASKS = Lists.newArrayList(new Task(TASKHEAD_ID, TASK_DESCRIPTION1, false),
            new Task(TASKHEAD_ID, TASK_DESCRIPTION2, false));

    private static List<Task> COMPLETED_TASKS = Lists.newArrayList(new Task(TASKHEAD_ID, TASK_DESCRIPTION1, true),
            new Task(TASKHEAD_ID, TASK_DESCRIPTION1, true));


    @Rule
    public ActivityTestRule<TasksActivity> mTasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class, true /* Initial touch mode */,
                    false /* Lazily launch activity */);


    @Test
    public void showActiveTasks() {
        loadActiveTasks();

        onView(withId(R.id.active_task_list)).check(matches(isDisplayed()));
        onView(withId(R.id.description)).check(matches(withText(TASK_DESCRIPTION1)));
        onView(withId(R.id.complete)).check(matches(not(isChecked())));

    }

    private void loadActiveTasks() {
        startActivityWithStubbedTasks(ACTIVE_TASKS, TASKHEAD_ID);
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