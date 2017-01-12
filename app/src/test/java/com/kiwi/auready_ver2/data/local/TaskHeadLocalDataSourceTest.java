package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASKHEADS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 1/8/17.
 */

@RunWith(RobolectricTestRunner.class)
public class TaskHeadLocalDataSourceTest {

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mTaskLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void getTaskHeads() {

        saveStubbedTaskHeads(TASKHEADS);

        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertEquals(TASKHEADS.get(0).getId(), taskHeads.get(0).getId());
                assertEquals(TASKHEADS.get(0).getTitle(), taskHeads.get(0).getTitle());

                assertEquals(TASKHEADS.get(1).getId(), taskHeads.get(1).getId());
                assertEquals(TASKHEADS.get(1).getTitle(), taskHeads.get(1).getTitle());
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };

        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    @Test
    public void getTaskHeads_failed_whenSaveFailed() {
        // save failed
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = Mockito.mock(TaskDataSource.LoadTaskHeadsCallback.class);
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        verify(loadTaskHeadsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskHeadsCount() {
        // Save 3 taskHeads
        saveStubbedTaskHeads(TASKHEADS);

        // Verify that returned taskHeadsCount is 2
        int actualTaskHeadsCount = mTaskLocalDataSource.getTaskHeadsCount();
        assertThat(actualTaskHeadsCount, is(3));

        deleteStubbedTaskHead();
    }

    @Test
    public void deleteTaskHeads_retrieveExistingTaskHeads() {
        saveStubbedTaskHeads(TASKHEADS);

        // Delete taskHeads - index 0, 2
        List<String> deletingTaskHeadIds = new ArrayList<>();
        deletingTaskHeadIds.add(TASKHEADS.get(0).getId());
        deletingTaskHeadIds.add(TASKHEADS.get(2).getId());
        mTaskLocalDataSource.deleteTaskHeads(deletingTaskHeadIds);

        // Verify if taskHeads are deleted
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertEquals(TASKHEADS.get(1).getId(), taskHeads.get(0).getId());
                assertEquals(TASKHEADS.get(1).getTitle(), taskHeads.get(0).getTitle());

                assertThat(taskHeads.size(), is(1));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    @Test
    public void updateTaskHeadOrders_retrieveUpdatingTaskHeads() {
        saveStubbedTaskHeads(TASKHEADS);

        TaskHead taskHead0 = TASKHEADS.get(0);
        TaskHead taskHead1 = TASKHEADS.get(1);

        List<TaskHead> updatingTaskHeads = new ArrayList<>();
        final TaskHead updating0 = new TaskHead(taskHead0.getId(), taskHead0.getTitle(), 100);
        updatingTaskHeads.add(updating0);
        final TaskHead updating1 = new TaskHead(taskHead1.getId(), taskHead1.getTitle(), 200);
        updatingTaskHeads.add(updating1);
        mTaskLocalDataSource.updateTaskHeadOrders(updatingTaskHeads);

        // Verify if taskHeads are updating
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                for(TaskHead taskHead: taskHeads) {
                    if(taskHead.getId().equals(updating0.getId())) {

                        assertEquals(100, taskHead.getOrder());
                    }
                    if(taskHead.getId().equals(updating1.getId())) {
                        assertEquals(200, taskHead.getOrder());
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    /*
    * Convenience methods
    * */
    private void saveStubbedTaskHeads(List<TaskHead> taskHeads) {

        // Save the stubbed taskheads
        for (TaskHead taskHead : taskHeads) {

            ContentValues values = new ContentValues();
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_ID, taskHead.getId());
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
            values.put(PersistenceContract.TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

            mDbHelper.insert(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, values);
        }
    }

    private void deleteStubbedTaskHead() {
        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        mDbHelper.close();
    }


}
