package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TaskEntry.TABLE_NAME, null, null);
        db.close();
    }

    @Override
    public void editDescription(@NonNull Task task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());

            String selection = TaskEntry.COLUMN_ID + " LIKE ?";
            String[] selectionArgs = {task.getId()};

            db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error when update description of task");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    @Override
    public void getTasksByTaskHeadId(String taskHeadId, @NonNull GetTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String QUERY = String.format("SELECT * FROM %s WHERE %s = '%s' ORDER BY %s asc;",
                TaskEntry.TABLE_NAME, TaskEntry.COLUMN_HEAD_ID, taskHeadId, TaskEntry.COLUMN_ORDER);

        Cursor c = db.rawQuery(QUERY, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String headId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_HEAD_ID));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPLETED)) == 1;
                int order = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ORDER));

                Task task = new Task(headId, id, description, completed, order);
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
    public void getAllTasks(@NonNull GetTasksCallback callback) {

    }

    @Override
    public void deleteTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {task.getId()};

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    public void saveTask(@NonNull Task task, @NonNull SaveTaskCallback callback) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(TaskEntry.COLUMN_ID, task.getId());
            values.put(TaskEntry.COLUMN_HEAD_ID, task.getTaskHeadId());
            values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
            values.put(TaskEntry.COLUMN_COMPLETED, task.getCompleted());
            values.put(TaskEntry.COLUMN_ORDER, task.getOrder());

            db.insert(TaskEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error insert new task");

            callback.onTaskNotSaved();
        } finally {
            db.endTransaction();
        }
        db.close();

        callback.onTaskSaved();
    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void sortTasks(LinkedList<Task> taskList) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            int size = taskList.size();
            for (int i = 0; i < size; i++) {
                Task task = taskList.get(i);
                ContentValues values = new ContentValues();
                values.put(TaskEntry.COLUMN_ORDER, i);

                String selection = TaskEntry.COLUMN_ID + " LIKE ?";
                String[] selectionArgs = {task.getId()};

                db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error update to " + TaskEntry.TABLE_NAME + ". ", e);
        } finally {
            db.endTransaction();
        }

        db.close();
    }
//
//    @Override
//    public void sortTask(Task task) {
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        db.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//
//            values.put(TaskEntry.COLUMN_ID, task.getId());
//            values.put(TaskEntry.COLUMN_ORDER, task.getOrder());
//
//            db.update(TaskEntry.TABLE_NAME, values,
//                    TaskEntry.COLUMN_ID + "=" + task.getId(), null);
//            db.setTransactionSuccessful();
//        } catch (SQLException e) {
//            Log.e(DBExceptionTag.TAG_SQLITE, "Error update to " + TaskEntry.TABLE_NAME + ". ", e);
//        } finally {
//            db.endTransaction();
//        }
//
//        db.close();
//    }
}
