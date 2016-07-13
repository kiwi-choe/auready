package com.kiwi.auready_ver2.data;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 7/1/16.
 */
public class FriendRepository implements FriendDataSource {

    private static List<Friend> FRIENDS = Lists.newArrayList(new Friend("email1"), new Friend("email2"));

    private static FriendRepository INSTANCE = null;

    private final FriendDataSource mFriendLocalDataSource;

    /*
    * This variable has package local visibility so it can be accessed from tests.
    * */
    Map<String, Friend> mCachedFriend;
    /*
    * Marks the cache as invalid, to force an update the next time data is requested.
    * This variable has package local visibility so it can be accessed from tests.
    * */
    boolean mCacheIsDirty = false;

    private FriendRepository(@NonNull FriendDataSource friendLocalData) {
        mFriendLocalDataSource = checkNotNull(friendLocalData);
    }

    @Override
    public void getFriends(@NonNull final LoadFriendsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedFriend != null && !mCacheIsDirty) {
            callback.onFriendsLoaded(new ArrayList<Friend>(mCachedFriend.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty, we need to fetch new data from the network.
            // getFriendsFromRemoteDataSource(callback);
        } else {
            // Query the local storage of available. If not, query the network.
            mFriendLocalDataSource.getFriends(new LoadFriendsCallback() {
                @Override
                public void onFriendsLoaded(List<Friend> friends) {
                    refreshCache(friends);
                    callback.onFriendsLoaded(new ArrayList<Friend>(mCachedFriend.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    // getFriendsFromRemoteDataSource(callback);
                }
            });
        }

    }

    private void refreshCache(List<Friend> friends) {
        if(mCachedFriend == null) {
            mCachedFriend = new LinkedHashMap<>();
        }
        mCachedFriend.clear();
        for(Friend friend : friends) {
            mCachedFriend.put(friend.getId(), friend);
        }
        mCacheIsDirty = false;
    }

    @Override
    public void saveFriend(Friend friend) {
        checkNotNull(friend);

        mFriendLocalDataSource.saveFriend(friend);

        // Do in memory cache update to keep the app UI up to date
        if(mCachedFriend == null) {
            mCachedFriend = new LinkedHashMap<>();
        }
        mCachedFriend.put(friend.getId(), friend);
    }

    @Override
    public void getFriend(String email, GetFriendCallback getFriendCallback) {

    }

    @Override
    public void deleteFriend(@NonNull String id) {
        mFriendLocalDataSource.deleteFriend(checkNotNull(id));

        mCachedFriend.remove(id);
    }

    public static FriendRepository getInstance(FriendDataSource friendLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FriendRepository(friendLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


}
