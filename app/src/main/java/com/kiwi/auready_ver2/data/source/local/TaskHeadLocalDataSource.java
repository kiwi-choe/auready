package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry.*;

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

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = sDb.rawQuery(query, null);
        if (c != null) {
            countOfTaskHeads = c.getCount();
            c.close();
        }
        return countOfTaskHeads;
    }

    @Override
    public void updateTaskHeadsOrder(List<TaskHead> taskHeads) {

        String whenThenArgs = "";
        String whereArgs = "";

        for (TaskHead taskHead : taskHeads) {
            whenThenArgs = whenThenArgs + " WHEN \"" + taskHead.getId() + "\" THEN " + taskHead.getOrder();
        }

        String TOKEN = ", ";
        int size = taskHeads.size();
        for (int i = 0; i < size; i++) {
            whereArgs = whereArgs + "\"" + taskHeads.get(i).getId() + "\"";
            if (i == size - 1) {
                break;
            }
            whereArgs = whereArgs + TOKEN;
        }

        String sql = String.format(
                "UPDATE %s" +
                        " SET %s = CASE %s" +
                        "%s END" +
                        " WHERE %s IN (%s)",
                TABLE_NAME,
                COLUMN_ORDER, COLUMN_ID,
                whenThenArgs,
                COLUMN_ID, whereArgs);


        sDb.beginTransaction();
        try {
            sDb.execSQL(sql);
            sDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(BaseDBAdapter.TAG, "Error updateTaskHeadsOrder to ( " + TABLE_NAME + " ).", e);
        } finally {
            sDb.endTransaction();
        }
    }

    @Override
    public void editTaskHead(@NonNull String id, String title, List<Friend> members) {
        checkNotNull(id);
        // Converting members to Local DB
        String strMembers = null;
        try {
            strMembers = BaseDBAdapter.OBJECT_MAPPER.writeValueAsString(members);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_MEMBERS, strMembers);

        String whereClause = COLUMN_ID + " LIKE ?";
        String[] whereArgs = { id };

        sDb.beginTransaction();
        try {
            sDb.update(TABLE_NAME, values, whereClause, whereArgs);
            sDb.setTransactionSuccessful();
        } catch (SQLException e){
            Log.e(TAG, "Could not update in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    @Override
    public void deleteTaskHeads(List<String> taskHeadIds) {

        String args = "";
        String TOKEN = ", ";
        int size = taskHeadIds.size();
        for (int i = 0; i < size; i++) {
            args = args + "\"" + taskHeadIds.get(i);
            args = args + "\"";
            if (i == size - 1) {
                break;
            }
            args = args + TOKEN;
        }

        String sql = String.format("DELETE FROM %s WHERE %s IN (%s);",
                TABLE_NAME,
                COLUMN_ID,
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
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_MEMBERS,
                COLUMN_ORDER
        };
        String orderBy = COLUMN_ORDER + " asc";

        Cursor c = sDb.query(
                TABLE_NAME, projection, null, null, null, null, orderBy);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE));
                String members = c.getString(c.getColumnIndexOrThrow(COLUMN_MEMBERS));
                int order = c.getInt(c.getColumnIndexOrThrow(COLUMN_ORDER));

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
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_MEMBERS,
                COLUMN_ORDER
        };

        String selection = COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {taskHeadId};

        Cursor c = sDb.query(
                TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        TaskHead taskHead = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
            String title = c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE));
            String members = c.getString(c.getColumnIndexOrThrow(COLUMN_MEMBERS));
            int order = c.getInt(c.getColumnIndexOrThrow(COLUMN_ORDER));

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
            sDb.delete(TABLE_NAME, null, null);
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
            values.put(COLUMN_ID, taskHead.getId());
            values.put(COLUMN_TITLE, taskHead.getTitle());
            values.put(COLUMN_MEMBERS, taskHead.getMembersString());
            values.put(COLUMN_ORDER, taskHead.getOrder());

            sDb.insert(TABLE_NAME, null, values);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(BaseDBAdapter.TAG, "Error insert new one to ( " + TABLE_NAME + " ). ", e);
        } finally {
            sDb.endTransaction();
        }
    }
}
