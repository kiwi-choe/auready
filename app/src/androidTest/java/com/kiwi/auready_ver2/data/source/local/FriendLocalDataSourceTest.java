package com.kiwi.auready_ver2.data.source.local;

import android.support.test.InstrumentationRegistry;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Integration test for the {@link FriendDataSource}, which uses the {@link SQLiteDbHelper}.
 */
public class FriendLocalDataSourceTest {

    private static final String EMAIL = "aa@a.com";
    private static final String NAME = "name1";

    private FriendLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        mLocalDataSource = FriendLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
        // deleteAllFriends
    }

    @Test
    public void saveFriend_retrieveFriend() {
        // Given a new Friend
        final Friend newFriend = new Friend(EMAIL, NAME);
        mLocalDataSource.saveFriend(newFriend);

        // Then the Friend can be retrieved from the persistent repository
        mLocalDataSource.getFriend(newFriend.getId(), new FriendDataSource.GetFriendCallback() {
            @Override
            public void onFriendLoaded(Friend friend) {
                assertThat(friend, is(newFriend));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }
}