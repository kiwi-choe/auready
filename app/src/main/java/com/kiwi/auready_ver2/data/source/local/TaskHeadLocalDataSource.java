package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry.COLUMN_ORDER;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry.COLUMN_TITLE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry.TABLE_NAME;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskHeadLocalDataSource implements TaskHeadDataSource {

    private static TaskHeadLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;

    private TaskHeadLocalDataSource(Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static TaskHeadLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {

    }

    //    @Override
//    public int getTaskHeadsCount() {
//        mDb = mDbHelper.getReadableDatabase();
//
//        int countOfTaskHeads = 0;
//
//        String query = "SELECT * FROM " + TABLE_NAME;
//        Cursor c = mDb.rawQuery(query, null);
//        if (c != null) {
//            countOfTaskHeads = c.getCount();
//            c.close();
//        }
//        return countOfTaskHeads;
//    }
//
//    @Override
//    public void updateTaskHeadsOrder(List<TaskHead> taskHeads) {
//        mDb = mDbHelper.getWritableDatabase();
//
//        String whenThenArgs = "";
//        String whereArgs = "";
//
//        for (TaskHead taskHead : taskHeads) {
//            whenThenArgs = whenThenArgs + " WHEN \"" + taskHead.getTaskHeadId() + "\" THEN " + taskHead.getOrder();
//        }
//
//        String TOKEN = ", ";
//        int size = taskHeads.size();
//        for (int i = 0; i < size; i++) {
//            whereArgs = whereArgs + "\"" + taskHeads.get(i).getTaskHeadId() + "\"";
//            if (i == size - 1) {
//                break;
//            }
//            whereArgs = whereArgs + TOKEN;
//        }
//
//        String sql = String.format(
//                "UPDATE %s" +
//                        " SET %s = CASE %s" +
//                        "%s END" +
//                        " WHERE %s IN (%s)",
//                TABLE_NAME,
//                COLUMN_ORDER, COLUMN_ID,
//                whenThenArgs,
//                COLUMN_ID, whereArgs);
//
//
//        mDb.beginTransaction();
//        try {
//            mDb.execSQL(sql);
//            mDb.setTransactionSuccessful();
//        } catch (SQLiteException e) {
//            Log.e(TAG_SQLITE, "Error updateTaskHeadsOrder to ( " + TABLE_NAME + " ).", e);
//        } finally {
//            mDb.endTransaction();
//        }
//    }
//
//    @Override
//    public void editTaskHead(@NonNull String id, String title, List<Friend> members) {
//        checkNotNull(id);
//        mDb = mDbHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_TITLE, title);
//
//        String whereClause = COLUMN_ID + " LIKE ?";
//        String[] whereArgs = { id };
//
//        mDb.beginTransaction();
//        try {
//            mDb.update(TABLE_NAME, values, whereClause, whereArgs);
//            mDb.setTransactionSuccessful();
//        } catch (SQLException e){
//            Log.e(TAG_SQLITE, "Could not update in ( " + DATABASE_NAME + "). ", e);
//        } finally {
//            mDb.endTransaction();
//        }
//    }
//
//    @Override
//    public void addMembers(@NonNull String id, List<Friend> members) {
//mDb = mDbHelper.getWritableDatabase();
//        // Get members by id
//        List<Friend> originalMembers = getMembersBy(id);
//
//        // and Add this new members to original members
//        originalMembers.addAll(members);
//
//        // Update added members
//        ContentValues values = new ContentValues();
//        String strMembers = getStrMembersByConverting(originalMembers);
//        values.put(COLUMN_MEMBERS, strMembers);
//
//        String whereClause = COLUMN_ID + " LIKE ?";
//        String[] whereArgs = {id};
//
//        mDb.beginTransaction();
//        try {
//            mDb.update(TABLE_NAME, values, whereClause, whereArgs);
//            mDb.setTransactionSuccessful();
//        } catch (SQLException e){
//            Log.e(TAG_SQLITE, "Could not update in ( " + DATABASE_NAME + "). ", e);
//        } finally {
//            mDb.endTransaction();
//        }
//    }
//
//    private List<Friend> getMembersBy(String id) {
//        mDb = mDbHelper.getReadableDatabase();
//
//        String[] projection = {COLUMN_MEMBERS};
//        String selection = COLUMN_ID + " LIKE ?";
//        String[] selectionArgs = {id};
//
//        Cursor c = mDb.query(
//                TABLE_NAME, projection, selection, selectionArgs, null, null, null);
//        String strMembers = "";
//        if (c != null && c.getCount() > 0) {
//            c.moveToFirst();
//            strMembers = c.getString(c.getColumnIndexOrThrow(COLUMN_MEMBERS));
//        }
//        if (c != null) {
//            c.close();
//        }
//        List<Friend> members = getMemberListByConverting(strMembers);
//        return members;
//    }
//
//    private List<Friend> getMemberListByConverting(String strMembers) {
//        mDb = mDbHelper.getReadableDatabase();
//
//        List<Friend> members = new ArrayList<>();
//        if (strMembers.length() != 0) {
//            try {
//                members =
//                        BaseDBAdapter.OBJECT_MAPPER.reader()
//                                .forType(new TypeReference<List<Friend>>() {})
//                                .readValue(strMembers);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return members;
//    }
//
//    private String getStrMembersByConverting(List<Friend> members) {
//        mDb = mDbHelper.getReadableDatabase();
//
//        // Converting members to Local DB
//        String strMembers = null;
//        try {
//            strMembers = BaseDBAdapter.OBJECT_MAPPER.writeValueAsString(members);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return strMembers;
//    }
//
//    /*
//    * Delete tasks of taskHead first and then delete taskHead
//    * */
//    @Override
//    public void deleteTaskHeads(List<String> taskHeadIds) {
//        mDb = mDbHelper.getWritableDatabase();
//
//        String args = "";
//        String TOKEN = ", ";
//        int size = taskHeadIds.size();
//        for (int i = 0; i < size; i++) {
//            args = args + "\"" + taskHeadIds.get(i);
//            args = args + "\"";
//            if (i == size - 1) {
//                break;
//            }
//            args = args + TOKEN;
//        }
//
//        String sql = String.format("DELETE FROM %s WHERE %s IN (%s);",
//                TABLE_NAME,
//                COLUMN_ID,
//                args);
//        mDb.beginTransaction();
//        try {
//            mDb.execSQL(sql);
//            mDb.setTransactionSuccessful();
//        } catch (SQLException e) {
//            Log.e(TAG_SQLITE, "Could not delete taskheads by taskHeadIds in ( " + DATABASE_NAME + "). ", e);
//        } finally {
//            mDb.endTransaction();
//        }
//    }
//
    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {

        List<TaskHead> taskHeads = new ArrayList<>();

        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_ORDER
        };
        String orderBy = COLUMN_ORDER + " asc";

        Cursor c = mDbHelper.query(
                TABLE_NAME, projection, null, null, null, null, orderBy);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                String title = c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE));
                int order = c.getInt(c.getColumnIndexOrThrow(COLUMN_ORDER));

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
    public void deleteAllTaskHeads() {
        mDbHelper.delete(TABLE_NAME, null, null);
    }

//    @Override
//    public void saveTaskHead(@NonNull TaskHead taskHead) {
//        checkNotNull(taskHead);
//        mDb = mDbHelper.getWritableDatabase();
//
//        mDb.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_ID, taskHead.getTaskHeadId());
//            values.put(COLUMN_TITLE, taskHead.getTitle());
//            values.put(COLUMN_MEMBERS, taskHead.getMembersString());
//            values.put(COLUMN_ORDER, taskHead.getOrder());
//
//            mDb.insert(TABLE_NAME, null, values);
//            mDb.setTransactionSuccessful();
//        } catch (SQLException e) {
//            Log.e(BaseDBAdapter.TAG, "Error insert new one to ( " + TABLE_NAME + " ). ", e);
//        } finally {
//            mDb.endTransaction();
//        }
//    }
}
