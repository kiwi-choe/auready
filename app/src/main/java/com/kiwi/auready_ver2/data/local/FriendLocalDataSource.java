package com.kiwi.auready_ver2.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.FriendDataSource;
import com.kiwi.auready_ver2.data.local.PersistenceContract.FriendEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 7/5/16.
 */
public class FriendLocalDataSource implements FriendDataSource {

    private static FriendLocalDataSource INSTANCE;

    private SQLiteDbHelper mDbHelper;

    private FriendLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SQLiteDbHelper(context);

    }

    public static FriendLocalDataSource getInstance(@NonNull Context context) {
        if(INSTANCE == null) {
            INSTANCE = new FriendLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getFriends(LoadFriendsCallback callback) {
        List<Friend> friends = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FriendEntry.COLUMN_ID,
                FriendEntry.COLUMN_EMAIL
        };

        Cursor c = db.query(
                FriendEntry.TABLE_NAME, projection, null, null, null, null, null);

        if(c != null && c.getCount() > 0) {
            while(c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_ID));
                String email = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_EMAIL));

                Friend friend = new Friend(email, id);
                friends.add(friend);
            }
        }

        if(c != null) {
            c.close();
        }

        db.close();
        if(friends.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onFriendsLoaded(friends);
        }
    }

    @Override
    public void getFriend(String friendId, GetFriendCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FriendEntry.COLUMN_ID,
                FriendEntry.COLUMN_EMAIL
        };

        String selection = FriendEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {friendId};

        Cursor c = db.query(
                FriendEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Friend friend = null;
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            String id = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_ID));
            String email = c.getString(c.getColumnIndexOrThrow(FriendEntry.COLUMN_EMAIL));

            friend = new Friend(email, id);
        }
        if(c != null) {
            c.close();
        }
        db.close();

        if(friend != null) {
            callback.onFriendLoaded(friend);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void deleteFriend(String id) {

    }

    @Override
    public void saveFriend(Friend friend) {
        checkNotNull(friend);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FriendEntry.COLUMN_ID, friend.getId());
        values.put(FriendEntry.COLUMN_EMAIL, friend.getEmail());

        db.insert(FriendEntry.TABLE_NAME, null, values);
        db.close();
    }

}
