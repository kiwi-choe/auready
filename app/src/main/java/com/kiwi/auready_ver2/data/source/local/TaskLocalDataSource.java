package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.*;

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
    public int getTaskHeadsCount() {

        int countOfTaskHeads = 0;
        String query = "SELECT * FROM " + TaskHeadEntry.TABLE_NAME;
        Cursor c = mDbHelper.rawQuery(query, null);
        if (c != null) {
            countOfTaskHeads = c.getCount();
            c.close();
        }
        return countOfTaskHeads;
    }

    @Override
    public void updateTaskHeadOrders(@NonNull List<TaskHead> taskHeads) {

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
                TaskHeadEntry.TABLE_NAME,
                TaskHeadEntry.COLUMN_ORDER, TaskHeadEntry.COLUMN_ID,
                whenThenArgs,
                TaskHeadEntry.COLUMN_ID, whereArgs);


        mDbHelper.execSQL(sql);
    }

    @Override
    public void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull SaveCallback callback) {
        checkNotNull(taskHeadDetail);

        // Save TaskHead
        TaskHead taskHead = taskHeadDetail.getTaskHead();
        checkNotNull(taskHead);
        String taskHeadId = taskHead.getId();
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(TaskHeadEntry.COLUMN_ID, taskHeadId);
        taskHeadValues.put(TaskHeadEntry.COLUMN_TITLE, taskHead.getTitle());
        taskHeadValues.put(TaskHeadEntry.COLUMN_ORDER, taskHead.getOrder());

        // Save members
        List<Member> tmpMembers = taskHeadDetail.getMembers();
        checkNotNull(tmpMembers);

        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> members = new ArrayList<>();
        for (Member member : tmpMembers) {
            members.add(new Member(member.getId(), taskHeadId, member.getFriendId(), member.getName()));
        }

        List<ContentValues> memberValuesList = new ArrayList<>();
        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_FRIEND_ID_FK, member.getFriendId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValuesList.add(memberValues);
        }

        // insert two tables
        long isSuccess = mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
                                   @NonNull List<String> deletingMembers,
                                   @NonNull EditTaskHeadDetailCallback callback) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        boolean isSuccessAll = false;
        db.beginTransaction();
        try {
            long isSuccessOfTaskHead = updateTaskHead(editTaskHead);

            long isSuccessOfAddMember = DBExceptionTag.INSERT_NOTHING;
            if (addingMembers.size() != 0) {
                isSuccessOfAddMember = saveMembers(editTaskHead.getId(), addingMembers);
            }

            if (deletingMembers.size() != 0) {
                deleteMembers(deletingMembers);
            }

            if (isSuccessOfTaskHead != DBExceptionTag.INSERT_ERROR &&
                    isSuccessOfAddMember != DBExceptionTag.INSERT_ERROR) {
                isSuccessAll = true;
                db.setTransactionSuccessful();
            }

        } catch (SQLException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Could not edit the rows in ( " + SQL_CREATE_TABLE.DATABASE_NAME + "). ", e);
        } finally {
            db.endTransaction();
        }

        if (isSuccessAll) {
            Log.d("TEST_NOW", "Success to edit taskHeadDetail");
            callback.onEditSuccess();
        } else {
            Log.d("TEST_NOW", "fail to edit taskHeadDetail");
            callback.onEditFailed();
        }
    }

    private void deleteMembers(List<String> deletingMembers) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Delete members
        String whereClause = null;
        for (String id : deletingMembers) {
            whereClause = MemberEntry.COLUMN_ID + " LIKE?";
            String[] whereArgs = {id};
            db.delete(MemberEntry.TABLE_NAME, whereClause, whereArgs);
        }
    }

    private long saveMembers(String taskHeadId, List<Member> addingMembers) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Save adding members
        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> tmpMembers = new ArrayList<>(0);
        for (Member member : addingMembers) {
            tmpMembers.add(new Member(member.getId(), taskHeadId, member.getFriendId(), member.getName()));
        }
        long isSuccessOfAddMember = DBExceptionTag.INSERT_ERROR;
        for (Member member : tmpMembers) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_FRIEND_ID_FK, member.getFriendId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            isSuccessOfAddMember = db.insertOrThrow(MemberEntry.TABLE_NAME, null, memberValues);
        }
        return isSuccessOfAddMember;
    }

    private long updateTaskHead(TaskHead editTaskHead) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // update taskHead with the existing id
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(TaskHeadEntry.COLUMN_TITLE, editTaskHead.getTitle());
        String selection = TaskHeadEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {editTaskHead.getId()};
        return db.update(TaskHeadEntry.TABLE_NAME, taskHeadValues, selection, selectionArgs);
    }

    @Override
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback) {

        // Get 2 objects - get a TaskHead and get Members
        // Get a taskHead
        String sql = String.format(
                "SELECT * FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = \'%s\'",
                TaskHeadEntry.TABLE_NAME, MemberEntry.TABLE_NAME,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID,
                MemberEntry.TABLE_NAME, MemberEntry.COLUMN_HEAD_ID_FK,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID, taskHeadId);

        Cursor cursor = mDbHelper.rawQuery(sql, null);

        TaskHead taskHead = null;
        List<Member> members = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Set members
                String memberId = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_ID));
                String taskHeadId_fk = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_HEAD_ID_FK));
                String friendId = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_FRIEND_ID_FK));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_NAME));
                Member member = new Member(memberId, taskHeadId_fk, friendId, name);
                members.add(member);

                // Set TaskHead
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                int order = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));
                if (taskHead == null) {
                    taskHead = new TaskHead(taskHeadId_fk, title, order);
                }
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
    public void getMembers(@NonNull String taskHeadId, @NonNull LoadMembersCallback callback) {

    }

    @Override
    public void getTasks(@NonNull String memberId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @Override
    public void deleteTask(@NonNull String id) {

    }
}
