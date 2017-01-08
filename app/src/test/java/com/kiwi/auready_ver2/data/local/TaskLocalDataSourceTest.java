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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * Created by kiwi on 1/8/17.
 */

@RunWith(RobolectricTestRunner.class)
public class TaskLocalDataSourceTest {
    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mTaskLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);
    @Captor
    private ArgumentCaptor<TaskDataSource.LoadTaskHeadsCallback> mLoadTaskHeadsCallbackCaptor;

    @Before
    public void setup(){
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    private void saveStubbedTaskHead() {

        // Save the stubbed taskheads
        TaskHead taskHead = new TaskHead("stubbedTaskHeadId", "title", 0);
        ContentValues values = new ContentValues();
        values.put(PersistenceContract.TaskHeadEntry.COLUMN_ID, taskHead.getId());
        values.put(PersistenceContract.TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        values.put(PersistenceContract.TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

        mDbHelper.insert(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, values);
    }

    @Test
    public void testGetTaskHeads(){

        saveStubbedTaskHead();

        final TaskHead expectedTaskHead = new TaskHead("stubbedTaskHeadId", "title", 0);
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = new TaskDataSource.LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                assertEquals(taskHeads.get(0).getId(), expectedTaskHead.getId());
                assertEquals(taskHeads.get(0).getTitle(), expectedTaskHead.getTitle());
                assertEquals(taskHeads.get(0).getOrder(), expectedTaskHead.getOrder());
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };

        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        deleteStubbedTaskHead();
    }

    private void deleteStubbedTaskHead() {
        mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
    }

    @Test
    public void getTaskHeads_failed_whenSaveFailed(){
        // save failed
        TaskDataSource.LoadTaskHeadsCallback loadTaskHeadsCallback = Mockito.mock(TaskDataSource.LoadTaskHeadsCallback.class);
        mTaskLocalDataSource.getTaskHeads(loadTaskHeadsCallback);

        verify(loadTaskHeadsCallback).onDataNotAvailable();
    }

    @After
    public void tearDown(){
        mDbHelper.close();
    }


}
