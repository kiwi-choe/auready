package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;

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
    public void editTitle(@NonNull TaskHead taskHead) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());

            String selection = TaskHeadEntry.COLUMN_ID + " LIKE ?";
            String[] selectionArgs = {taskHead.getId()};

            db.update(TaskHeadEntry.TABLE_NAME, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error when update title of taskHead");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {
        List<TaskHead> taskHeads = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE
        };

        Cursor c = db.query(
                TaskHeadEntry.TABLE_NAME, projection, null, null, null, null, null);

        if(c!=null && c.getCount()>0) {
            while(c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));

                TaskHead taskHead = new TaskHead(id, title);
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
    public void deleteTaskHead(@NonNull String taskHeadId) {

    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {

    }
}
