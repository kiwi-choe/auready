package com.kiwi.auready.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.kiwi.auready.data.Friend;
import com.kiwi.auready.data.source.FriendDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready.data.source.local.PersistenceContract.FriendEntry.COLUMN_EMAIL;
import static com.kiwi.auready.data.source.local.PersistenceContract.FriendEntry.COLUMN_ID;
import static com.kiwi.auready.data.source.local.PersistenceContract.FriendEntry.COLUMN_NAME;
import static com.kiwi.auready.data.source.local.PersistenceContract.FriendEntry.TABLE_NAME;

/**
 * Created by kiwi on 7/5/16.
 */
public class FriendLocalDataSource implements FriendDataSource {

    private static FriendLocalDataSource INSTANCE;
    private SQLiteDBHelper mDbHelper;

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
        mDbHelper.delete(TABLE_NAME, null, null);
    }

    @Override
    public void deleteFriend(@NonNull String id) {

        String selection = COLUMN_ID + " LIKE?";
        String[] selectionArgs = {id};

        mDbHelper.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void getFriends(@NonNull LoadFriendsCallback callback) {

        List<Friend> friends = new ArrayList<>();

        String[] projection = {
                COLUMN_ID,
                COLUMN_EMAIL,
                COLUMN_NAME
        };

        Cursor c = mDbHelper.query(
                TABLE_NAME, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                String email = c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL));
                String name = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME));

                Friend friend = new Friend(itemId, email, name);
                friends.add(friend);
            }
        }
        if (c != null) {
            c.close();
        }

        if (friends.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onFriendsLoaded(friends);
        }
    }

    @Override
    public void saveFriend(@NonNull Friend friend, @NonNull SaveCallback callback) {
        checkNotNull(friend);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, friend.getUserId());
        values.put(COLUMN_EMAIL, friend.getEmail());
        values.put(COLUMN_NAME, friend.getName());

        long isSuccess = mDbHelper.insert(TABLE_NAME, null, values);

        if (isSuccess != PersistenceContract.DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }
}
