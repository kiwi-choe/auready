package com.kiwi.auready_ver2.data.local;

import android.database.Cursor;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;
import com.kiwi.auready_ver2.data.source.local.FriendLocalDataSource;
import com.kiwi.auready_ver2.data.source.local.SQLiteDBHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static com.kiwi.auready_ver2.StubbedData.FriendStub.FRIENDS;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry.COLUMN_EMAIL;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry.COLUMN_NAME;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.FriendEntry.TABLE_NAME;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

/**
 * Local db test : table 'friend'
 */

@RunWith(RobolectricTestRunner.class)
public class FriendLocalDataSourceTest {

    private static SQLiteDBHelper mDbHelper;
    private FriendLocalDataSource mLocalDataSource = FriendLocalDataSource.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        mDbHelper = SQLiteDBHelper.getInstance(RuntimeEnvironment.application);
    }

    @Test
    public void saveFriend_returnCallback() {
        Friend newFriend = FRIENDS.get(0);
        FriendDataSource.SaveCallback saveCallback = Mockito.mock(FriendDataSource.SaveCallback.class);
        mLocalDataSource.saveFriend(newFriend, saveCallback);
        verify(saveCallback).onSaveSuccess();
    }

    @Test
    public void saveFriend_retrieveFriends() {

        // Save 3 friends
        FriendDataSource.SaveCallback saveCallback = Mockito.mock(FriendDataSource.SaveCallback.class);
        for (Friend friend : FRIENDS) {
            mLocalDataSource.saveFriend(friend, saveCallback);
            saveCallback.onSaveSuccess();
        }

        List<Friend> friends = retrieveSavedFriends();

        assertThat(friends.size(), is(FRIENDS.size()));
        assertThat(friends.containsAll(FRIENDS), is(true));
    }

    @Test
    public void getFriends() {
        saveStubbedFriends(FRIENDS);

        FriendDataSource.LoadFriendsCallback loadFriendsCallback = new FriendDataSource.LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                assertThat(friends.size(), is(FRIENDS.size()));
                assertThat(friends.containsAll(FRIENDS), is(true));
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        };
        mLocalDataSource.getFriends(loadFriendsCallback);
    }

    @Test
    public void deleteAllFriends() {

        saveStubbedFriends(FRIENDS);

        mLocalDataSource.deleteAllFriends();

        // Verify that there is no friend
        List<Friend> friends = retrieveSavedFriends();
        assertThat(friends.size(), is(0));
    }

    @Test
    public void deleteFriend() {

        saveStubbedFriends(FRIENDS);

        Friend deletingFriend = FRIENDS.get(1);
        mLocalDataSource.deleteFriend(deletingFriend.getId());

        // Verify that the deletingFriend is not exist
        List<Friend> friends = retrieveSavedFriends();
        assertThat(friends.contains(deletingFriend), is(false));
    }

    // Retrieve the saved members using query directly
    private List<Friend> retrieveSavedFriends() {
        List<Friend> friends = new ArrayList<>(0);

        String[] projection = {
                COLUMN_ID,
                COLUMN_EMAIL,
                COLUMN_NAME
        };
        Cursor c = mDbHelper.query(TABLE_NAME, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(COLUMN_ID));
                String email = c.getString(c.getColumnIndexOrThrow(COLUMN_EMAIL));
                String name = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME));

                friends.add(new Friend(id, email, name));
            }
        }

        return friends;
    }

    private void deleteAll() {
        mDbHelper.delete(TABLE_NAME, null, null);
    }

    private void saveStubbedFriends(List<Friend> friends) {
        // Save 3 friends
        FriendDataSource.SaveCallback saveCallback = Mockito.mock(FriendDataSource.SaveCallback.class);
        for (Friend friend : friends) {
            mLocalDataSource.saveFriend(friend, saveCallback);
            saveCallback.onSaveSuccess();
        }
    }

    @After
    public void tearDown() {
        deleteAll();
        mDbHelper.close();
    }
}
