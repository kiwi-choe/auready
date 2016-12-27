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
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration test for the {@link FriendDataSource}, which uses the {@link BaseDBAdapter}.
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
        mLocalDataSource.deleteAllFriends();
    }

    @Test
    public void saveFriends_retrievesFriends() {
        // Given a stub friend list
        final List<Friend> stubFriends = Lists.newArrayList(
                new Friend("aa@aa.com", "aa"), new Friend("bb@bb.com", "bb"), new Friend("cc@cc.com", "cc"));
        // When saved into the persistent repository
        mLocalDataSource.initFriend(stubFriends);
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

    @Test
    public void getFriends_retrieveSavedFriends() {
        // Given 2 new friends in the local repository
        final Friend newFriend1 = new Friend("aa@aa.com", "aa");
        mLocalDataSource.saveFriend(newFriend1);
        final Friend newFriend2 = new Friend("bb@bb.com", "bb");
        mLocalDataSource.saveFriend(newFriend2);

        mLocalDataSource.getFriends(new FriendDataSource.LoadFriendsCallback() {

            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                assertNotNull(friends);
                assertTrue(friends.size() >= 2);

                boolean newFriend1IdFound = false;
                boolean newFriend2IdFound = false;
                for(Friend friend:friends) {
                    if(friend.getId().equals(newFriend1.getId())) {
                        newFriend1IdFound = true;
                    }
                    if(friend.getId().equals(newFriend2.getId())) {
                        newFriend2IdFound = true;
                    }
                }
                assertTrue(newFriend1IdFound);
                assertTrue(newFriend2IdFound);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });

    }

    @Test
    public void deleteAllFriends_emptyListOfRetrievedFriend() {
        // Given a new friend in the persistent repository and a mocked callback
        Friend newFriend = new Friend(EMAIL, NAME);
        mLocalDataSource.saveFriend(newFriend);
        FriendDataSource.LoadFriendsCallback callback = mock(FriendDataSource.LoadFriendsCallback.class);

        // When all tasks are deleted
        mLocalDataSource.deleteAllFriends();

        // Then the retrieved friends is an empty list
        mLocalDataSource.getFriends(callback);

        verify(callback).onDataNotAvailable();
        verify(callback, never()).onFriendsLoaded(anyList());
    }

//

}