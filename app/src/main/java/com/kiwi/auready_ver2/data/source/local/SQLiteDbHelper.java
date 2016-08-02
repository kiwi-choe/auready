package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kiwi on 7/4/16.
 */
public class SQLiteDbHelper extends SQLiteOpenHelper {

    // Database info
    // If you change the db scheme, you must increment the database version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Auready.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_FRIEND_TABLE =
            "CREATE TABLE IF NOT EXISTS " + PersistenceContract.FriendEntry.TABLE_NAME + " (" +
                    PersistenceContract.FriendEntry.COLUMN_ID + TEXT_TYPE + " PRIMARY KEY," +
                    PersistenceContract.FriendEntry.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    PersistenceContract.FriendEntry.COLUMN_NAME + TEXT_TYPE +
                    " )";

    public SQLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FRIEND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PersistenceContract.FriendEntry.TABLE_NAME);

            onCreate(db);
        }
    }

}
