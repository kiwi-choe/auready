package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskHeadLocalDataSource extends BaseDBAdapter
        implements TaskHeadDataSource {

    private static TaskHeadLocalDataSource INSTANCE;

    private TaskHeadLocalDataSource(Context context) {
        open(context);
    }

    public static TaskHeadLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public int getTaskHeadsCount() {
        int countOfTaskHeads = 0;

        String query = "SELECT * FROM " + TaskHeadEntry.TABLE_NAME;
        Cursor c = sDb.rawQuery(query, null);
        if (c != null) {
            countOfTaskHeads = c.getCount();
            c.close();
        }
        return countOfTaskHeads;
    }

    @Override
    public void deleteTaskHeads(List<String> taskHeadIds) {

        String args = "";
        String TOKEN = ", ";
        int size = taskHeadIds.size();
        for(int i = 0; i<size; i++) {
            args = args +"\"" + taskHeadIds.get(i);
            args = args + "\"";
            if(i == size-1) {
                break;
            }
            args = args + TOKEN;
        }

        String sql = String.format("DELETE FROM %s WHERE %s IN (%s);",
                TaskHeadEntry.TABLE_NAME,
                TaskHeadEntry.COLUMN_ID,
                args);
        sDb.beginTransaction();
        try {
            sDb.execSQL(sql);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(BaseDBAdapter.TAG, "Could not delete taskheads by taskHeadIds in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {
        List<TaskHead> taskHeads = new ArrayList<>();

        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE,
                TaskHeadEntry.COLUMN_MEMBERS,
                TaskHeadEntry.COLUMN_ORDER
        };

        Cursor c = sDb.query(
                TaskHeadEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                String members = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_MEMBERS));
                int order = c.getInt(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));

                TaskHead taskHead = new TaskHead(id, title, members, order);
                taskHeads.add(taskHead);
            }
        }
        if (c != null) {
            c.close();
        }

        if (taskHeads.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTaskHeadsLoaded(taskHeads);
        }
    }

    @Override
    public void getTaskHead(@NonNull String taskHeadId, @NonNull GetTaskHeadCallback callback) {

        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE,
                TaskHeadEntry.COLUMN_MEMBERS,
                TaskHeadEntry.COLUMN_ORDER
        };

        String selection = TaskHeadEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {taskHeadId};

        Cursor c = sDb.query(
                TaskHeadEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        TaskHead taskHead = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
            String members = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_MEMBERS));
            int order = c.getInt(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));

            taskHead = new TaskHead(itemId, title, members, order);
        }
        if (c != null) {
            c.close();
        }

        if (taskHead != null) {
            callback.onTaskHeadLoaded(taskHead);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void deleteAllTaskHeads() {
        sDb.beginTransaction();
        try {
            sDb.delete(TaskHeadEntry.TABLE_NAME, null, null);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(BaseDBAdapter.TAG, "Could not delete the column in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {
        checkNotNull(taskHead);

        sDb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TaskHeadEntry.COLUMN_ID, taskHead.getId());
            values.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
            values.put(TaskHeadEntry.COLUMN_MEMBERS, taskHead.getMembersString());
            values.put(TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

            sDb.insert(TaskHeadEntry.TABLE_NAME, null, values);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(BaseDBAdapter.TAG, "Error insert new one to ( " + TaskHeadEntry.TABLE_NAME + " ). ", e);
        } finally {
            sDb.endTransaction();
        }
    }
}
