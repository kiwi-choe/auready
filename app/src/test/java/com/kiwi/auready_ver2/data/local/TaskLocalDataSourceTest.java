package com.kiwi.auready_ver2.data.local;

import android.database.Cursor;

import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;
import com.kiwi.auready_ver2.data.source.local.TaskLocalDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.kiwi.auready_ver2.StubbedData.TaskStub.TASK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by kiwi on 1/8/17.
 */

@RunWith(RobolectricTestRunner.class)
public class TaskLocalDataSourceTest {

    private static SQLiteDBHelper mDbHelper;
    private TaskLocalDataSource mLocalDataSource = TaskLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void saveTask() {

        mLocalDataSource.saveTask(TASK);

        // Retrieve the saved task using query directly
        String[] projection = {
                TaskEntry.COLUMN_ID,
                TaskEntry.COLUMN_MEMBER_ID_FK,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry.COLUMN_COMPLETED,
                TaskEntry.COLUMN_ORDER
        };
        String selection = TaskEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {TASK.getId()};
        Cursor c = mDbHelper.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if(c!=null && c.getCount()>0) {
            while(c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                assertThat(id, is(TASK.getId()));
                assertThat(description, is(TASK.getDescription()));
            }
        }

        deleteAll();
    }

    private void deleteAll() {
        mDbHelper.delete(TaskEntry.TABLE_NAME, null, null);
    }

    @After
    public void tearDown() {
        mDbHelper.close();
    }


}
