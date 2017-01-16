package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.kiwi.auready_ver2.StubbedData_forView.*;

/**
 * Tasks view test
 */
@RunWith(AndroidJUnit4.class)
public class TasksViewTest {

    @Rule
    public ActivityTestRule<TasksActivity> mTasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class, true /* Initial touch mode */,
                    false /* Lazily launch activity */);



    @Test
    public void showMembers() {
        startActivityWithStubbedTasks(MEMBERS.get(0).getId(), TASKS);

        onView(withId(R.id.expand_listview)).check(matches(isDisplayed()));

        onView(withText(MEMBERS.get(0).getName())).check(matches(isDisplayed()));
    }
    /*
    * Setup the test fixture with a fake taskHead id. The {@link TasksActivity} is started with
    * a particular taskHead id, which is then loaded from the service API.
    *
    * Note that this test runs hermetically and is fully isolated using a fake implementation of
    * the service API. This is a great way to make the tests more reliable and faster at the same
    * time, since they are isolated from any outside dependencies.
    * */

    private void startActivityWithStubbedTasks(String memberId, List<Task> tasks) {
        // Add taskheads stub to the fake service api layer.
        TaskHeadRepository.destroyInstance();
        List<TaskHead> taskHeads = Lists.newArrayList(TASKHEAD);
        FakeTaskHeadRemoteDataSource.getInstance().addTaskHeads(taskHeads);

        // Add tasks stub to the fake service api layer.
        TaskRepository.destroyInstance();
//        FakeTaskRemoteDataSource.getInstance().addTasks(TASKHEAD.getId(), memberId, tasks);

        // Lazily start Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(TasksActivity.ARG_TASKHEAD_ID, TASKHEAD.getId());
        mTasksActivityTestRule.launchActivity(startIntent);
    }
}