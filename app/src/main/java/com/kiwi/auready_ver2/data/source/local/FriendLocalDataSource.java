package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 7/5/16.
 */
public class FriendLocalDataSource implements FriendDataSource {

    private static FriendLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;
    private SQLiteDatabase mDb;

    private FriendLocalDataSource(@NonNull Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static FriendLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FriendLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void deleteAllFriends() {
        mDb = mDbHelper.getWritableDatabase();

        mDb.delete(FriendEntry.TABLE_NAME, null, null);
    }

    @Override
    public void deleteFriend(@NonNull String id) {
        mDb = mDbHelper.getWritableDatabase();

        String selection = FriendEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {id};

        mDb.delete(FriendEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void getFriends(@NonNull LoadFriendsCallback callback) {
        mDb = mDbHelper.getReadableDatabase();

        List<Friend> friends = new ArrayList<>();

        String[] projection = {
                FriendEntry.COLUMN_ID,
                FriendEntry.COLUMN_EMAIL,
                FriendEntry.COLUMN_NAME
        };

        Cursor c = mDb.query(
                FriendEntry.TABLE_NAME, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while(c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_ID));
                String email = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_EMAIL));
                String name = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_NAME));

                Friend friend = new Friend(itemId, email, name);
                friends.add(friend);
            }
        }
        if(c!=null) {
            c.close();
        }

        if(friends.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onFriendsLoaded(friends);
        }
    }

    /*
        * Note: {@link GetFriendCallback#onDataNotAvailable()} is fired
        * if the {@link Friend} isn't found.
        * */
    @Override
    public void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback callback) {
        mDb = mDbHelper.getReadableDatabase();

        String[] projection = {
                FriendEntry.COLUMN_ID,
                FriendEntry.COLUMN_EMAIL,
                FriendEntry.COLUMN_NAME
        };

        String selection = FriendEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {friendColumnId};

        Cursor c = mDb.query(
                FriendEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Friend friend = null;
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_ID));
            String email = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_EMAIL));
            String name = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_NAME));

            friend = new Friend(itemId, email, name);
        }
        if (c != null) {
            c.close();
        }

        if (friend != null) {
            callback.onFriendLoaded(friend);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void initFriend(@NonNull List<Friend> friends) {
        checkNotNull(friends);
        mDb = mDbHelper.getWritableDatabase();

        mDb.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            for (Friend friend : friends) {
                values.put(FriendEntry.COLUMN_ID, friend.getId());
                values.put(FriendEntry.COLUMN_EMAIL, friend.getEmail());
                values.put(FriendEntry.COLUMN_NAME, friend.getName());

                mDb.insert(FriendEntry.TABLE_NAME, null, values);
            }
            mDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error insert new list to (" + FriendEntry.TABLE_NAME + " ). ", e);
        } finally {
            mDb.endTransaction();
        }
    }

    @Override
    public void saveFriend(@NonNull Friend friend) {
        checkNotNull(friend);
        mDb = mDbHelper.getWritableDatabase();

        mDb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(FriendEntry.COLUMN_ID, friend.getId());
            values.put(FriendEntry.COLUMN_EMAIL, friend.getEmail());
            values.put(FriendEntry.COLUMN_NAME, friend.getName());

            mDb.insert(FriendEntry.TABLE_NAME, null, values);
            mDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error insert new one to (" + FriendEntry.TABLE_NAME + "). ", e);
        } finally {
            mDb.endTransaction();
        }
    }
}
