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

import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
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
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
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
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, callback);

        verify(callback).onDataNotAvailable();
        verify(callback, never()).onTasksLoaded(anyList());
    }

    @Test
    public void deleteTask_taskNotRetrievable() {
        // Save two tasks
        final Task newTask1 = new Task(TASKHEAD_ID);
        mLocalDataSource.saveTask(newTask1, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {
                fail();
            }
        });
        final Task newTask2 = new Task(TASKHEAD_ID);
        mLocalDataSource.saveTask(newTask2, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {
                fail();
            }
        });

        // When newTask1 is deleted
        mLocalDataSource.deleteTask(newTask1);

        // Then only newTask2 can be retrieved from the persistent repository
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertThat(tasks.size(), is(1));
                assertThat(tasks.get(0).getId(), is(newTask2.getId()));
            }

            @Override
            public void onDataNotAvailable() {
                fail("One task should be retrieved at least.");
            }
        });
    }

    @Test
    public void updateOrderOfTasks_retrievedTasks() {
        LinkedList<Task> taskList = new LinkedList<>();

        final Task task1 = new Task(TASKHEAD_ID, "active", 0);
        Task task2 = new Task(TASKHEAD_ID, "completed", true, 1);

        // Save two tasks
        mLocalDataSource.saveTask(task1, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {

            }
        });
        mLocalDataSource.saveTask(task2, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {

            }

            @Override
            public void onTaskNotSaved() {

            }
        });

        // before sorting
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertThat(tasks.get(0).getOrder(), is(0));
                assertThat(tasks.get(1).getOrder(), is(1));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

        taskList.add(task1);
        // Add new task (event)
        Task newTask = new Task(TASKHEAD_ID, "new task", 0);
        mLocalDataSource.saveTask(newTask, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {

            }

            @Override
            public void onTaskNotSaved() {
                fail();
            }
        });
        taskList.add(newTask);

        taskList.add(task2);

        mLocalDataSource.sortTasks(taskList);

        // after sorting
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertThat(tasks.get(0).getDescription(), is("active"));
                assertThat(tasks.get(0).getOrder(), is(0));
                assertThat(tasks.get(1).getDescription(), is("new task"));

                assertThat(tasks.get(1).getOrder(), is(1));
                assertThat(tasks.get(2).getDescription(), is("completed"));
                assertThat(tasks.get(2).getOrder(), is(2));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }

    @Test
    public void editDescriptionOfTask_retrievedTasks() {
        final Task task1 = new Task(TASKHEAD_ID, "original", 0);

        // Save new task
        mLocalDataSource.saveTask(task1, new TaskDataSource.SaveTaskCallback() {
            @Override
            public void onTaskSaved() {
            }

            @Override
            public void onTaskNotSaved() {

            }
        });

        final Task editTask = new Task(TASKHEAD_ID, task1.getId(), "edited!!", task1.getOrder());
        // Update editedTask description
        mLocalDataSource.editDescription(editTask);

        // Get
        mLocalDataSource.getTasksByTaskHeadId(TASKHEAD_ID, new TaskDataSource.GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                assertThat(tasks.get(0).getDescription(), is(editTask.getDescription()));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}
