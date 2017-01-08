package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.MemberEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

import java.util.List;

import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag.INSERT_ERROR;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag.TAG_SQLITE;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.SQL_CREATE_TABLE.*;

/*
* Local Database helper
* */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    // Object mapper for serializing and deserializing JSON strings
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static SQLiteDBHelper sDbHelper = null;
    private static SQLiteDatabase sDb = null;

    /*
    * Constructor
    * */
    private SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    * Create new SQLiteDBHelper instance
    * */
    public static SQLiteDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // dont accidentally leak an Activity's context
        if (sDbHelper == null) {
            sDbHelper = new SQLiteDBHelper(context.getApplicationContext());

            try {
//                sDb = sDbHelper.getWritableDatabase();
//                Log.d(TAG, "db path is " + sDb.getPath());
            } catch (SQLException e) {
                Log.e(TAG_SQLITE, "Could not create and/or open the database ( " + DATABASE_NAME + " ) that will be used for reading and writing.", e);
            }
        }
        return sDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FRIEND_TABLE);
        db.execSQL(SQL_CREATE_TASKHEAD_TABLE);
        db.execSQL(SQL_CREATE_MEMBER_TABLE);
        db.execSQL(SQL_CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FriendEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TaskHeadEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + MemberEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);

            onCreate(db);
        }
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        sDb = sDbHelper.getWritableDatabase();

        long isSuccess = INSERT_ERROR;
        sDb.beginTransaction();
        try {
            isSuccess = sDb.insertOrThrow(table, nullColumnHack, values);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG_SQLITE, "Error insert new one to ( " + TaskHeadEntry.TABLE_NAME + " ). ", e);
        } finally {
            sDb.endTransaction();
        }
        return isSuccess;
    }

    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        sDb = sDbHelper.getReadableDatabase();

        return sDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void delete(String table, String whereClause, String[] whereArgs) {
        sDb = sDbHelper.getWritableDatabase();

        sDb.beginTransaction();
        try {
            sDb.delete(table, whereClause, whereArgs);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG_SQLITE, "Could not delete the column in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    Cursor rawQuery(String sql, String[] selectionArgs) {
        sDb = sDbHelper.getReadableDatabase();
        return sDb.rawQuery(sql, selectionArgs);
    }

    void execSQL(String sql) {
        sDb = sDbHelper.getWritableDatabase();
        sDb.beginTransaction();
        try {
            sDb.execSQL(sql);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG_SQLITE, "Could not delete taskheads in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    public long insertTaskHeadAndMembers(ContentValues taskHeadValues, List<ContentValues> memberValuesList) {
        sDb = sDbHelper.getWritableDatabase();

        long isSuccessAll = INSERT_ERROR;
        sDb.beginTransaction();
        try {
            // Save TaskHead
            long isSuccessOfTaskHead = sDb.insertOrThrow(TaskHeadEntry.TABLE_NAME, null, taskHeadValues);

            // Save members
            long isSuccessOfMember = INSERT_ERROR;
            for (ContentValues values : memberValuesList) {
                isSuccessOfMember = sDb.insertOrThrow(MemberEntry.TABLE_NAME, null, values);
            }
            if (isSuccessOfTaskHead != INSERT_ERROR && isSuccessOfMember != INSERT_ERROR) {
                isSuccessAll = isSuccessOfTaskHead; // any value is ok, if not INSERT_ERROR
                sDb.setTransactionSuccessful();
            }
        } catch (SQLException e) {
            Log.e(TAG_SQLITE, "Error insert new one to ( " + TaskHeadEntry.TABLE_NAME + " ). ", e);
        } finally {
            sDb.endTransaction();
        }
        return isSuccessAll;
    }
}

