package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskHeadDetailDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.MemberEntry;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;

/**
 * Created by kiwi on 1/2/17.
 */

public class TaskHeadDetailLocalDataSource implements TaskHeadDetailDataSource {

    private static TaskHeadDetailLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;

    private TaskHeadDetailLocalDataSource(Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static TaskHeadDetailLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadDetailLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void deleteTaskHeadDetail(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        String whereClause = TaskHeadEntry.COLUMN_ID + " LIKE?";
        String[] whereArgs = {taskHeadId};
        mDbHelper.delete(TaskHeadEntry.TABLE_NAME, whereClause, whereArgs);
    }

    @Override
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback) {

        // Get 2 objects - get a TaskHead and get Members
        // Get a taskHead
        String sql = String.format(
                "SELECT * FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = %s",
                TaskHeadEntry.TABLE_NAME, MemberEntry.TABLE_NAME,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID,
                MemberEntry.TABLE_NAME, MemberEntry.COLUMN_HEAD_ID_FK,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID, taskHeadId);

        Cursor cursor = mDbHelper.rawQuery(sql, null);

        TaskHead taskHead = null;
        List<Member> members = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Set TaskHead
                taskHeadId = cursor.getString(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                int order = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));
                if (taskHead == null) {
                    taskHead = new TaskHead(taskHeadId, title, order);
                }
                // Set members
                String memberId = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_ID));
                String taskHeadId_fk = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_HEAD_ID_FK));
                String friendId = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_FRIEND_ID_FK));
                String name = cursor.getColumnName(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_NAME));
                Member member = new Member(memberId, taskHeadId_fk, friendId, name);
                members.add(member);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        if (taskHead != null) {
            TaskHeadDetail taskHeadDetail =
                    new TaskHeadDetail(taskHead, members);
            callback.onTaskHeadDetailLoaded(taskHeadDetail);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead, @NonNull SaveCallback callback) {
        checkNotNull(taskHead);

        ContentValues values = new ContentValues();
        values.put(TaskHeadEntry.COLUMN_ID, taskHead.getId());
        values.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        values.put(TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());
        long isSuccess = mDbHelper.insert(TaskHeadEntry.TABLE_NAME, null, values);
        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void saveMembers(@NonNull List<Member> members, @NonNull SaveCallback callback) {
        checkNotNull(members);

        long isSuccess = DBExceptionTag.INSERT_ERROR;
        for (Member member : members) {
            ContentValues values = new ContentValues();
            values.put(MemberEntry.COLUMN_ID, member.getId());
            values.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            values.put(MemberEntry.COLUMN_FRIEND_ID_FK, member.getFriendId());
            values.put(MemberEntry.COLUMN_NAME, member.getName());
            isSuccess = mDbHelper.insert(MemberEntry.TABLE_NAME, null, values);
        }

        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }
}
