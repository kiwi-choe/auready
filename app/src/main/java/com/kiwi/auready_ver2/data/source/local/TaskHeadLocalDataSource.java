package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskHeadLocalDataSource implements TaskHeadDataSource {

    private static TaskHeadLocalDataSource INSTANCE;

    private SQLiteDbHelper mDbHelper;

    private TaskHeadLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SQLiteDbHelper(context);
    }

    public static TaskHeadLocalDataSource getInstance(@NonNull Context context) {
        if(INSTANCE == null) {
            INSTANCE = new TaskHeadLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public int getTaskHeadsCount() {
        int countOfTaskHeads = 0;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TaskHeadEntry.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        if(c != null) {
            countOfTaskHeads = c.getCount();
            c.close();
        }
        db.close();
        return countOfTaskHeads;
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {
        List<TaskHead> taskHeads = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE,
                TaskHeadEntry.COLUMN_MEMBERS,
                TaskHeadEntry.COLUMN_ORDER
        };

        Cursor c = db.query(
                TaskHeadEntry.TABLE_NAME, projection, null, null, null, null, null);

        if(c!=null && c.getCount()>0) {
            while(c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                String members = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_MEMBERS));
                int order = c.getInt(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));

                TaskHead taskHead = new TaskHead(id, title, members, order);
                taskHeads.add(taskHead);
            }
        }
        if(c!=null) {
            c.close();
        }
        db.close();

        if(taskHeads.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTaskHeadsLoaded(taskHeads);
        }
    }

    @Override
    public void getTaskHead(@NonNull String taskHeadId, @NonNull GetTaskHeadCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE,
                TaskHeadEntry.COLUMN_MEMBERS
        };

        String selection = TaskHeadEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { taskHeadId };

        Cursor c = db.query(
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

        db.close();

        if (taskHead != null) {
            callback.onTaskHeadLoaded(taskHead);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void deleteTaskHead(@NonNull String taskHeadId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskHeadEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { taskHeadId };

        db.delete(TaskHeadEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void deleteAllTaskHeads() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskHeadEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {
        checkNotNull(taskHead);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskHeadEntry.COLUMN_ID, taskHead.getId());
        values.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        values.put(TaskHeadEntry.COLUMN_MEMBERS, taskHead.getMembersString());

        db.insert(TaskHeadEntry.TABLE_NAME, null, values);

        db.close();
    }
}
