package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskRepository;
import com.kiwi.auready_ver2.data.source.FakeTaskRemoteDataSource;
import com.kiwi.auready_ver2.taskheads.TaskHeadsActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by kiwi on 9/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class TasksViewTest {

    @Rule
    public ActivityTestRule<TasksActivity> mTasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class, true /* Initial touch mode */,
                    false /* Lazily launch activity */);



    /*
    * Setup the test fixture with a fake taskHead id. The {@link TasksActivity} is started with
    * a particular taskHead id, which is then loaded from the service API.
    *
    * Note that this test runs hermetically and is fully isolated using a fake implementation of
    * the service API. This is a great way to make the tests more reliable and faster at the same
    * time, since they are isolated from any outside dependencies.
    * */
    private void startActivityWithStubbedTasks(String taskHeadId, String memberId, List<Task> tasks) {
        // Add tasks stub to the fake service api layer.
        TaskRepository.destroyInstance();
        FakeTaskRemoteDataSource.getInstance().addTasks(taskHeadId, memberId, tasks);

        // Lazily start Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(TaskHeadsActivity.EXTRA_TASKHEAD_ID, taskHeadId);
        mTasksActivityTestRule.launchActivity(startIntent);
    }
}