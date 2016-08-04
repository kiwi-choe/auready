package com.kiwi.auready_ver2.data.source.local;

import android.support.test.InstrumentationRegistry;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.source.FriendDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
    public void saveFriends_retrievesFriends() {
        // Given a stub friend list
        final List<Friend> stubFriends = Lists.newArrayList(
                new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));
        // When saved into the persistent repository
        mLocalDataSource.saveFriends(stubFriends);
        // then the friends can be retrieved from the persistent repository
        mLocalDataSource.getFriends(new FriendDataSource.LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                assertNotNull(friends);
                assertTrue(friends.size() >= 2);

                boolean newFriend1NameFound = false;
                boolean newFriend2NameFound = false;
                boolean newFriend3NameFound = false;
                for(Friend friend:friends) {
                    if(friend.getName().equals("aa")) {
                        newFriend1NameFound = true;
                    }
                    if(friend.getName().equals("bb")) {
                        newFriend2NameFound = true;
                    }
                    if(friend.getName().equals("cc")) {
                        newFriend3NameFound = true;
                    }
                }
                assertTrue(newFriend1NameFound);
                assertTrue(newFriend2NameFound);
                assertTrue(newFriend3NameFound);
            }

            @Override
            public void onDataNotAvailable() {
                fail("fail to saveFriends_retrievesFriends test");
            }
        });

    }
//
//    @Test
//    public void saveFriend_retrieveFriend() {
//        // Given a new Friend
//        final Friend newFriend = new Friend(EMAIL, NAME);
//        mLocalDataSource.saveFriend(newFriend);
//
//        // Then the Friend can be retrieved from the persistent repository
//        mLocalDataSource.getFriend(newFriend.getId(), new FriendDataSource.GetFriendCallback() {
//            @Override
//            public void onFriendLoaded(Friend friend) {
//                assertThat(friend, is(newFriend));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                fail("Callback error");
//            }
//        });
//    }
}