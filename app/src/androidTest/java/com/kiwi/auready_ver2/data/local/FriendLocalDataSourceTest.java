package com.kiwi.auready_ver2.data.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.FriendDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by kiwi on 7/6/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class FriendLocalDataSourceTest {

    private FriendLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
        mLocalDataSource = FriendLocalDataSource.getInstance(
                InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalDataSource);
    }

    @Test
    public void saveFriend_retrievesFriend() {
        final Friend newFriend = new Friend("email1");
        // When saved into the persistent repository
        mLocalDataSource.saveFriend(newFriend);

        // Then the friend can be retrieved from the persistent repository
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

    @Test
    public void getFriends_retrieveSavedFriends() {
        // Given 2 new friends in the persistent repository
        final Friend newFriend1 = new Friend("email1");
        mLocalDataSource.saveFriend(newFriend1);
        final Friend newFriend2 = new Friend("email2");
        mLocalDataSource.saveFriend(newFriend2);

        // Then the friends can be retrieved from the persistent repository
        mLocalDataSource.getFriends(new FriendDataSource.LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                assertNotNull(friends);
                assertTrue(friends.size() >= 2);

                boolean newFriend1EmailFound = false;
                boolean newFriend2EmailFound = false;
                for (Friend friend : friends) {
                    if (friend.getEmail().equals(newFriend1.getEmail())) {
                        newFriend1EmailFound = true;
                    }
                    if (friend.getEmail().equals(newFriend2.getEmail())) {
                        newFriend2EmailFound = true;
                    }
                }
                assertTrue(newFriend1EmailFound);
                assertTrue(newFriend2EmailFound);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }


}