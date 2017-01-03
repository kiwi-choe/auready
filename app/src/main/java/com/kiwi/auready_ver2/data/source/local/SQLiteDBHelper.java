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

import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag.TAG_SQLITE;

/*
* Local Database helper
* */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    // Object mapper for serializing and deserializing JSON strings
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Database info
    // If you change the db scheme, you must increment the database version
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Auready.db";

    private static final String PRIMARY_KEY = " PRIMARY KEY, ";
    private static final String FOREIGN_KEY = "FOREIGN KEY(";
    private static final String REFERENCES = ") REFERENCES ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";

    public static final long INSERT_ERROR = -1;

    private static SQLiteDBHelper sDbHelper = null;
    private static SQLiteDatabase sDb = null;

    // insert, update, delete, execSQL, ...

    private static final String SQL_CREATE_FRIEND_TABLE =
            "CREATE TABLE IF NOT EXISTS " + FriendEntry.TABLE_NAME + " (" +
                    FriendEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                    FriendEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_NAME + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_TASKHEAD_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TaskHeadEntry.TABLE_NAME + " (" +
                    TaskHeadEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                    TaskHeadEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    TaskHeadEntry.COLUMN_ORDER + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_MEMBER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + MemberEntry.TABLE_NAME + " (" +
                    MemberEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                    MemberEntry.COLUMN_HEAD_ID_FK + TEXT_TYPE + COMMA_SEP +
                    MemberEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    MemberEntry.COLUMN_FRIEND_ID_FK + TEXT_TYPE + COMMA_SEP +
                    FOREIGN_KEY + MemberEntry.COLUMN_HEAD_ID_FK +
                    REFERENCES + TaskHeadEntry.TABLE_NAME + "(" + TaskHeadEntry.COLUMN_ID + ")" + COMMA_SEP +
                    FOREIGN_KEY + MemberEntry.COLUMN_FRIEND_ID_FK +
                    REFERENCES + FriendEntry.TABLE_NAME + "(" + FriendEntry.COLUMN_ID + ")" +
                    ON_DELETE_CASCADE +
                    " )";

    private static final String SQL_CREATE_TASK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry.COLUMN_ID + TEXT_TYPE + PRIMARY_KEY +
                    TaskEntry.COLUMN_MEMBER_ID_FK + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_COMPLETED + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN_KEY + TaskEntry.COLUMN_MEMBER_ID_FK +
                    REFERENCES + MemberEntry.TABLE_NAME + "(" + MemberEntry.COLUMN_ID + ")" +
                    ON_DELETE_CASCADE +
                    " )";

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

    long insert(String table, String nullColumnHack, ContentValues values) {
        sDb = sDbHelper.getWritableDatabase();

        long isSuccess = INSERT_ERROR;
        sDb.beginTransaction();
        try {
            isSuccess = sDb.insert(table, nullColumnHack, values);
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
}

