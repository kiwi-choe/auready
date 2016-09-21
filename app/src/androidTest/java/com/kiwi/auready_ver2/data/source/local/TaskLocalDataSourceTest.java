package com.kiwi.auready_ver2.data.source.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration test for the {@link TaskDataSource}, which uses the {@link SQLiteDbHelper}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TaskLocalDataSourceTest {

    private static final String TASKHEAD_ID = "stub_taskHeadId";

    private TaskLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        mLocalDataSource = TaskLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
        mLocalDataSource.deleteAllTasks();
    }

    @Test
    public void saveTask_retrievesTask() {
        final Task newTask = new Task(TASKHEAD_ID);

        // When saved into the persistent repository
        mLocalDataSource.saveTask(newTask, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {
                fail();
            }
        });

        // Then the task can be retrieved from the persistent repository
        mLocalDataSource.getTasks(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertThat(tasks.get(0).getId(), is(newTask.getId()));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void deleteAllTasks_emptyListOfRetrievedTask() {
        Task newTask = new Task(TASKHEAD_ID);
        mLocalDataSource.saveTask(newTask, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {
                fail();
            }
        });
        TaskDataSource.GetTasksCallback callback = mock(TaskDataSource.GetTasksCallback.class);

        // When all tasks are deleted
        mLocalDataSource.deleteAllTasks();
        // Then the retrieved tasks is an empty list
        mLocalDataSource.getTasks(TASKHEAD_ID, callback);

        verify(callback).onDataNotAvailable();
        verify(callback, never()).onTasksLoaded(anyList());
    }
}
