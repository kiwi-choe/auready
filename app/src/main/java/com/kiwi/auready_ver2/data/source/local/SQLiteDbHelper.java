package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskHeadEntry;

/**
 * Created by kiwi on 7/4/16.
 */
public class SQLiteDbHelper extends SQLiteOpenHelper {

    // Object mapper for serializing and deserializing JSON strings
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Database info
    // If you change the db scheme, you must increment the database version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Auready.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

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
                    TaskHeadEntry.COLUMN_MEMBERS + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_TASK_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry.COLUMN_ID + TEXT_TYPE + " PRIMARY KEY," +
                    TaskEntry.COLUMN_HEAD_ID + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_COMPLETED + INTEGER_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_ORDER + INTEGER_TYPE +
                    " )";


    public SQLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
