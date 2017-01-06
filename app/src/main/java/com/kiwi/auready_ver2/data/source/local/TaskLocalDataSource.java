package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskLocalDataSource implements TaskDataSource {

    private static TaskLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;

    private TaskLocalDataSource(Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static TaskLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {

        List<TaskHead> taskHeads = new ArrayList<>(0);

        String[] projection = {
                TaskHeadEntry.COLUMN_ID,
                TaskHeadEntry.COLUMN_TITLE,
                TaskHeadEntry.COLUMN_ORDER
        };
        String orderBy = TaskHeadEntry.COLUMN_ORDER + " asc";

        Cursor c = mDbHelper.query(
                TaskHeadEntry.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                int order = c.getInt(c.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));

                TaskHead taskHead = new TaskHead(id, title, order);
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
                TaskHeadEntry.TABLE_NAME,
                TaskHeadEntry.COLUMN_ID,
                args);

        mDbHelper.execSQL(sql);
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead, @NonNull SaveCallback callback) {
        checkNotNull(taskHead);

        ContentValues values = new ContentValues();
        values.put(TaskHeadEntry.COLUMN_ID, taskHead.getId());
        values.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        values.put(TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());
        long isSuccess = mDbHelper.insert(TaskHeadEntry.TABLE_NAME, null, values);
        if (isSuccess != PersistenceContract.DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void saveMembers(@NonNull List<Member> members, @NonNull SaveCallback callback) {
        checkNotNull(members);

        long isSuccess = PersistenceContract.DBExceptionTag.INSERT_ERROR;
        for (Member member : members) {
            ContentValues values = new ContentValues();
            values.put(PersistenceContract.MemberEntry.COLUMN_ID, member.getId());
            values.put(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            values.put(PersistenceContract.MemberEntry.COLUMN_FRIEND_ID_FK, member.getFriendId());
            values.put(PersistenceContract.MemberEntry.COLUMN_NAME, member.getName());
            isSuccess = mDbHelper.insert(PersistenceContract.MemberEntry.TABLE_NAME, null, values);
        }

        if (isSuccess != PersistenceContract.DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }


}
