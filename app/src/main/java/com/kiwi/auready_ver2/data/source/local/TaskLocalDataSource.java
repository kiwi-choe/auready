package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.*;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskLocalDataSource implements TaskDataSource {

    private static TaskLocalDataSource INSTANCE;

    private SQLiteDbHelper mDbHelper;

    private TaskLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SQLiteDbHelper(context);
    }

    public static TaskLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TaskLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull String memberId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String whereClause = COLUMN_HEAD_ID + " LIKE? AND " + COLUMN_MEMBER_ID + " LIKE?";
        String[] whereArgs = {taskHeadId, memberId};
        db.delete(TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull DeleteTasksCallback callback) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String whereClause = COLUMN_HEAD_ID + " LIKE?";
        String[] whereArgs = {taskHeadId};
        int deleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        // TODO: 12/15/16 Compare 'deleted' to 'tasks of taskHead'
        if(deleted != 0) {
            callback.onDeleteSuccess();
        } else {
            callback.onDeleteFail();
        }
    }

    @Override
    public void deleteTask(@NonNull String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String whereClause = COLUMN_ID + " LIKE?";
        String[] whereArgs = {id};
        db.delete(TABLE_NAME, whereClause, whereArgs);

        db.close();
    }

    @Override
    public void getTasks(@NonNull String taskHeadId, @NonNull String memberId, @NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>(0);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] columns = {
                COLUMN_ID,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_ORDER
        };

        String selection = COLUMN_HEAD_ID + " LIKE? AND " + COLUMN_MEMBER_ID + " LIKE?";
        String[] selectionArgs = {taskHeadId, memberId};
        Cursor c = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(COLUMN_ID));
                String description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndex(COLUMN_COMPLETED))) != 0;
                int order = c.getInt(c.getColumnIndex(COLUMN_ORDER));

                Task task = new Task(id, taskHeadId, memberId, description, completed, order);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        db.close();
        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, task.getId());
            values.put(COLUMN_HEAD_ID, task.getTaskHeadId());
            values.put(COLUMN_MEMBER_ID, task.getMemberId());
            values.put(COLUMN_DESCRIPTION, task.getDescription());
            values.put(COLUMN_COMPLETED, task.getCompleted());
            values.put(COLUMN_ORDER, task.getOrder());
        } catch (SQLiteException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error insert new one to (" + TABLE_NAME + "). ", e);
        } finally {
            db.endTransaction();
        }
        db.close();
    }


}
