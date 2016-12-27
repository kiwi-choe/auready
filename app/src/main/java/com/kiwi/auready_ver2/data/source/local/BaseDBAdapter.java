package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

/**
 * Created by kiwi on 7/4/16.
 */
public class BaseDBAdapter {

    protected static final String TAG = "SQLiteDBHelper: ";

    // Object mapper for serializing and deserializing JSON strings
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Database info
    // If you change the db scheme, you must increment the database version
    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "Auready.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static SQLiteDBHelper sDbHelper = null;
    protected static SQLiteDatabase sDb;

    private static final String SQL_CREATE_FRIEND_TABLE =
            "CREATE TABLE IF NOT EXISTS " + FriendEntry.TABLE_NAME + " (" +
                    FriendEntry.COLUMN_ID + TEXT_TYPE + " PRIMARY KEY," +
                    FriendEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    FriendEntry.COLUMN_NAME + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_TASKHEAD_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TaskHeadEntry.TABLE_NAME + " (" +
                    TaskHeadEntry.COLUMN_ID + TEXT_TYPE + " PRIMARY KEY," +
                    TaskHeadEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    TaskHeadEntry.COLUMN_MEMBERS + TEXT_TYPE + COMMA_SEP +
                    TaskHeadEntry.COLUMN_ORDER + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_TASK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry.COLUMN_ID + TEXT_TYPE + " PRIMARY KEY," +
                    TaskEntry.COLUMN_HEAD_ID + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_COMPLETED + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_ORDER + INTEGER_TYPE +
                    " )";

    /*
    * Open or create
    *
    * @return this
    * @throws SQLException if the database could be neither opened or created
    * */
    public void open(Context context) throws SQLiteException {
        // FIXME: 2016-03-10 APP 시작시, call. in MainActivity or Application
        sDbHelper = SQLiteDBHelper.getInstance(context);
        if(!sDb.isOpen()) {
            sDbHelper.onOpen(sDb);
        }
    }

    public void close() {
        if(sDbHelper != null) {
            sDbHelper.close();
        }
        if(sDb != null) {
            sDb.close();
        }
    }

    /*
    * Database INNER class
    * */
    private static class SQLiteDBHelper extends SQLiteOpenHelper {

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
            if(sDbHelper == null) {
                sDbHelper = new SQLiteDBHelper(context.getApplicationContext());

                try {
                    sDb = sDbHelper.getWritableDatabase();
                    Log.d(TAG, "db path is " + sDb.getPath());
                } catch (SQLException e) {
                    Log.e(TAG, "Could not create and/or open the database ( " + DATABASE_NAME + " ) that will be used for reading and writing.", e);
                }
            }
            return sDbHelper;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_FRIEND_TABLE);
            db.execSQL(SQL_CREATE_TASKHEAD_TABLE);
            db.execSQL(SQL_CREATE_TASK_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + FriendEntry.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + TaskHeadEntry.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);

                onCreate(db);
            }
        }
    }
}
