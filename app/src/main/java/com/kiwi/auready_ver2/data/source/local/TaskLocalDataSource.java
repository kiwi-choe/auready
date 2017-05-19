package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.*;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag.TAG_SQLITE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.SQL_CREATE_TABLE.DATABASE_NAME;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskLocalDataSource implements TaskDataSource {

    private static final String TAG = "TaskLocalDataSource";

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
    public void deleteAllTaskHeads(@NonNull DeleteAllCallback callback) {
        boolean isSuccess = mDbHelper.delete(TaskHeadEntry.TABLE_NAME, null, null);

        if(isSuccess) {
            callback.onDeleteAllSuccess();
        } else {
            callback.onDeleteAllFail();
        }
    }

    @Override
    public void initializeLocalData(@NonNull InitLocalDataCallback callback) {

        boolean isDeletedTaskHead = mDbHelper.delete(PersistenceContract.TaskHeadEntry.TABLE_NAME, null, null);
        // cascade
//        mDbHelper.delete(PersistenceContract.MemberEntry.TABLE_NAME, null, null);
//        mDbHelper.delete(PersistenceContract.TaskEntry.TABLE_NAME, null, null);
        boolean isDeletedFriend = mDbHelper.delete(PersistenceContract.FriendEntry.TABLE_NAME, null, null);
        boolean isDeletedNotification = mDbHelper.delete(PersistenceContract.NotificationEntry.TABLE_NAME, null, null);

        if(isDeletedTaskHead && isDeletedFriend && isDeletedNotification) {
            callback.onInitSuccess();
        } else {
            callback.onInitFail();
        }
    }

    @Override
    public void getTaskHeadDetails(@NonNull LoadTaskHeadDetailsCallback callback) {

        String sql = String.format(
                "SELECT * FROM %s, %s WHERE %s.%s = %s.%s ORDER BY %s.%s ASC",
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.MemberEntry.TABLE_NAME,
                PersistenceContract.TaskHeadEntry.TABLE_NAME, PersistenceContract.TaskHeadEntry.COLUMN_ID,
                PersistenceContract.MemberEntry.TABLE_NAME, PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ORDER);

        Cursor cursor = mDbHelper.rawQuery(sql, null);

        List<TaskHeadDetail> taskHeadDetails = new ArrayList<>(0);
        String taskHeadIdOfPreRow = "";
        int i = -1;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Set members
                String memberId = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_ID));
                String taskHeadId_fk = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_HEAD_ID_FK));
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.MemberEntry.COLUMN_EMAIL));
                Member member = new Member(memberId, taskHeadId_fk, userId, name, email);

                if (!taskHeadId_fk.equals(taskHeadIdOfPreRow)) {
                    i++;
                    // Set TaskHead
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_TITLE));
                    int order = cursor.getInt(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_ORDER));
                    int color = cursor.getInt(cursor.getColumnIndexOrThrow(PersistenceContract.TaskHeadEntry.COLUMN_COLOR));

                    Log.d("Tag_updateOrders", String.valueOf(order));

                    TaskHead taskHead = new TaskHead(taskHeadId_fk, title, order, color);
                    List<Member> members = Lists.newArrayList(member);
                    TaskHeadDetail taskHeadDetail = new TaskHeadDetail(taskHead, members);
                    taskHeadDetails.add(taskHeadDetail);
                } else {
                    taskHeadDetails.get(i).getMembers().add(member);
                }

                taskHeadIdOfPreRow = taskHeadId_fk;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        if (taskHeadDetails.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTaskHeadDetailsLoaded(taskHeadDetails);
        }
    }

    @Override
    public void deleteTaskHeads(List<String> taskHeadIds, @NonNull DeleteTaskHeadsCallback callback) {

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

        boolean isSuccess = mDbHelper.execSQL(sql);
        if(isSuccess) {
            callback.onDeleteSuccess();
        } else {
            callback.onDeleteFail();
        }
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
        taskHeadValues.put(TaskHeadEntry.COLUMN_COLOR, taskHead.getColor());

        // Save members
        List<Member> tmpMembers = taskHeadDetail.getMembers();
        checkNotNull(tmpMembers);

        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> members = new ArrayList<>();
        for (Member member : tmpMembers) {
            members.add(new Member(member.getId(), taskHeadId, member.getUserId(), member.getName(), member.getEmail()));
            Log.d(TAG, "member id - " + member.getId());
        }

        List<ContentValues> memberValuesList = new ArrayList<>();
        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValues.put(MemberEntry.COLUMN_EMAIL, member.getEmail());
            memberValuesList.add(memberValues);
        }

        // insert two tables
        long isSuccess = mDbHelper.insertTaskHeadAndMembers(taskHeadValues, memberValuesList);
        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            Log.d(TAG, "entered into saveTaskHeadDetail local saveSuccess");
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
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

            if (isSuccessOfTaskHead != DBExceptionTag.UPDATE_ERROR &&
                    isSuccessOfAddMember != DBExceptionTag.INSERT_ERROR) {
                isSuccessAll = true;
                db.setTransactionSuccessful();
            }

        } catch (SQLException e) {
            Log.e(TAG_SQLITE, "Could not edit the rows in ( " + DATABASE_NAME + "). ", e);
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

    private long saveMembers(String taskHeadId, List<Member> addingMembers) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Save adding members
        // Coz member of the new taskHeadDetail didnt set taskHeadId
        List<Member> tmpMembers = new ArrayList<>(0);
        for (Member member : addingMembers) {
            tmpMembers.add(new Member(member.getId(), taskHeadId, member.getUserId(), member.getName(), member.getEmail()));
        }
        long isSuccessOfAddMember = DBExceptionTag.INSERT_ERROR;
        for (Member member : tmpMembers) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValues.put(MemberEntry.COLUMN_EMAIL, member.getEmail());
            isSuccessOfAddMember = db.insertOrThrow(MemberEntry.TABLE_NAME, null, memberValues);
        }
        return isSuccessOfAddMember;
    }

    private long updateTaskHead(TaskHead editTaskHead) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // update taskHead with the existing id
        ContentValues taskHeadValues = new ContentValues();
        taskHeadValues.put(TaskHeadEntry.COLUMN_TITLE, editTaskHead.getTitle());
        taskHeadValues.put(TaskHeadEntry.COLUMN_COLOR, editTaskHead.getColor());
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
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_USER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_EMAIL));
                Member member = new Member(memberId, taskHeadId_fk, userId, name, email);
                members.add(member);

                // Set TaskHead
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_TITLE));
                int order = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_ORDER));
                int color = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHeadEntry.COLUMN_COLOR));
                if (taskHead == null) {
                    taskHead = new TaskHead(taskHeadId_fk, title, order, color);
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

        List<Member> members = new ArrayList<>();

        String selection = MemberEntry.COLUMN_HEAD_ID_FK + " LIKE?";
        String[] selectionArgs = {taskHeadId};

        Cursor c = mDbHelper.query(MemberEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_ID));
                String taskheadId_fk = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_HEAD_ID_FK));
                String userId = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_USER_ID));
                String name = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_NAME));
                String email = c.getString(c.getColumnIndexOrThrow(MemberEntry.COLUMN_EMAIL));

                Member member = new Member(id, taskheadId_fk, userId, name, email);
                members.add(member);
            }
        }
        if (c != null) {
            c.close();
        }
        if (members.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onMembersLoaded(members);
        }
    }

    /*
    * Tasks
    * */
    @Override
    public void getTasksOfMember(@NonNull String memberId, @NonNull LoadTasksCallback callback) {

        List<Task> tasks = new ArrayList<>();

        String selection = TaskEntry.COLUMN_MEMBER_ID_FK + " LIKE?";
        String[] selectionArgs = {memberId};
        String orderBy = TaskEntry.COLUMN_ORDER + " asc";

        Cursor c = mDbHelper.query(TaskEntry.TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String memberId_fk = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_MEMBER_ID_FK));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPLETED)) > 0);
                int order = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ORDER));

                Task task = new Task(id, memberId_fk, description, completed, order);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        if (tasks.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();

        String query = String.format(
                "SELECT * FROM %s " +
                        "INNER JOIN %s ON %s.%s = %s.%s " +
                        "INNER JOIN %s ON %s.%s = %s.%s " +
                        "WHERE %s.%s = \'%s\'",
                TaskEntry.TABLE_NAME,
                MemberEntry.TABLE_NAME, MemberEntry.TABLE_NAME, MemberEntry.COLUMN_ID, TaskEntry.TABLE_NAME, TaskEntry.COLUMN_ID,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID, MemberEntry.TABLE_NAME, MemberEntry.COLUMN_HEAD_ID_FK,
                TaskHeadEntry.TABLE_NAME, TaskHeadEntry.COLUMN_ID, taskheadId
        );
        Cursor c = mDbHelper.rawQuery(query, null);
        if(c != null && c.getCount() > 0) {
            while(c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ID));
                String memberId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_MEMBER_ID_FK));
                String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_COMPLETED)) > 0);
                int order = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_ORDER));

                Task task = new Task(id, memberId, description, completed, order);
                Log.d("getTasksOfTaskHead", task.toString() + "\n");
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        if (tasks.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_ID, task.getId());
        values.put(TaskEntry.COLUMN_MEMBER_ID_FK, task.getMemberId());
        values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_COMPLETED, task.getCompletedInteger());
        values.put(TaskEntry.COLUMN_ORDER, task.getOrder());

        mDbHelper.replace(TaskEntry.TABLE_NAME, null, values);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {

        String whereClause = TaskEntry.COLUMN_ID + " LIKE?";
        String[] whereArgs = {taskId};
        mDbHelper.delete(TaskEntry.TABLE_NAME, whereClause, whereArgs);
    }

    @Override
    public void editTasks(@NonNull String taskHeadId, @NonNull Map<String, List<Task>> cachedTasks) {

        // Make the collection for all the tasks of members
        List<Task> updatingTasks = new ArrayList<>();
        for(String key:cachedTasks.keySet()) {
            List<Task> tasks = cachedTasks.get(key);
            updatingTasks.addAll(tasks);
        }

        editTasksInLocal(updatingTasks);
    }

    @Override
    public void saveMembers(List<Member> members) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for (Member member : members) {
            ContentValues memberValues = new ContentValues();
            memberValues.put(MemberEntry.COLUMN_ID, member.getId());
            memberValues.put(MemberEntry.COLUMN_HEAD_ID_FK, member.getTaskHeadId());
            memberValues.put(MemberEntry.COLUMN_USER_ID, member.getUserId());
            memberValues.put(MemberEntry.COLUMN_NAME, member.getName());
            memberValues.put(MemberEntry.COLUMN_EMAIL, member.getEmail());
            db.insertOrThrow(MemberEntry.TABLE_NAME, null, memberValues);
        }
    }

    // Delete members by taskHeadId
    @Override
    public void deleteMembers(String taskHeadId, DeleteMembersCallback callback) {

        String whereClause = MemberEntry.COLUMN_HEAD_ID_FK + " LIKE?";
        String[] whereArgs = {taskHeadId};
        boolean isSuccess = mDbHelper.delete(PersistenceContract.MemberEntry.TABLE_NAME, whereClause, whereArgs);
        if(isSuccess) {
            callback.onDeleteSuccess();
        } else {
            callback.onDeleteFail();
        }
    }

    @Override
    public void editTasksOfMember(String memberId, List<Task> tasks, @NonNull EditTasksOfMemberCallback callback) {

        if(editTasksInLocal(tasks)) {
            callback.onEditSuccess();
        } else {
            callback.onEditFail();
        }
    }

    @Override
    public void refreshLocalTaskHead() {
        // Not required because the {@link TaskRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    private boolean editTasksInLocal(List<Task> tasks) {
        boolean success = true;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int numOfUpdatedRows = 0;

            String whereClause = TaskEntry.COLUMN_ID + " LIKE?";
            for (Task task : tasks) {
                ContentValues values = new ContentValues();
                values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
                values.put(TaskEntry.COLUMN_COMPLETED, task.getCompletedInteger());
                values.put(TaskEntry.COLUMN_ORDER, task.getOrder());

                String[] whereArgs = {task.getId()};
                numOfUpdatedRows += db.update(TaskEntry.TABLE_NAME, values, whereClause, whereArgs);
            }

            if (numOfUpdatedRows == tasks.size()) {
                db.setTransactionSuccessful();
            }
        } catch (SQLException e) {
            success = false;
            Log.e(TAG_SQLITE, "Could not delete taskheads in ( " + DATABASE_NAME + "). ", e);
        } finally {
            db.endTransaction();
        }
        return success;
    }

}
