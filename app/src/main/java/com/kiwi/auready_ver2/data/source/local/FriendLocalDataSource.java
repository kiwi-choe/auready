package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry;

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
        if (INSTANCE == null) {
            INSTANCE = new FriendLocalDataSource(context);
        }
        return INSTANCE;
    }


    @Override
    public void saveFriend(@NonNull Friend friend) {
        checkNotNull(friend);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FriendEntry.COLUMN_ID, friend.getId());
        values.put(FriendEntry.COLUMN_EMAIL, friend.getEmail());
        values.put(FriendEntry.COLUMN_NAME, friend.getName());

        db.insert(FriendEntry.TABLE_NAME, null, values);
        db.close();
    }

    /*
    * Note: {@link GetFriendCallback#onDataNotAvailable()} is fired
    * if the {@link Friend} isn't found.
    * */
    @Override
    public void getFriend(@NonNull String friendColumnId, @NonNull GetFriendCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                FriendEntry.COLUMN_ID,
                FriendEntry.COLUMN_EMAIL,
                FriendEntry.COLUMN_NAME
        };

        String selection = FriendEntry.COLUMN_ID + " LIKE?";
        String[] selectionArgs = {friendColumnId};

        Cursor c = db.query(
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

        db.close();

        if(friend != null) {
            callback.onFriendLoaded(friend);
        } else {
            callback.onDataNotAvailable();
        }
    }

}
