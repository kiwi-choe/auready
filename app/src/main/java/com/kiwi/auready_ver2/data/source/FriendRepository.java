package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 7/1/16.
 */
public class FriendRepository implements FriendDataSource {

    private static FriendRepository INSTANCE = null;

    private final FriendDataSource mFriendsLocalDataSource;

    public Map<String, Friend> mCacheFriends;

    private FriendRepository(@NonNull FriendDataSource friendLocalDataSource) {
        mFriendsLocalDataSource = checkNotNull(friendLocalDataSource);
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


    @Override
    public void deleteAllFriends() {
        mFriendsLocalDataSource.deleteAllFriends();

        if(mCacheFriends != null) {
            mCacheFriends.clear();
        }
    }

    @Override
    public void deleteFriend(@NonNull String id) {
        checkNotNull(id);
        mFriendsLocalDataSource.deleteFriend(id);
    }

    @Override
    public void getFriends(@NonNull final LoadFriendsCallback callback) {
        checkNotNull(callback);

        // Get from Local
        mFriendsLocalDataSource.getFriends(new LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                refreshCaches(friends);
                callback.onFriendsLoaded(friends);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCaches(List<Friend> friends) {
        if(mCacheFriends == null) {
            mCacheFriends = new LinkedHashMap<>();
        }
        mCacheFriends.clear();
        for(Friend friend:friends) {

            mCacheFriends.put(friend.getId(), friend);
        }
    }

    @Override
    public void saveFriend(@NonNull final Friend friend, @NonNull SaveCallback callback) {

        checkNotNull(friend);
        mFriendsLocalDataSource.saveFriend(friend, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                // Do in memory cache update to keep the app UI up to date
                addToCache(friend);
            }

            @Override
            public void onSaveFailed() {

            }
        });
    }

    private void addToCache(Friend friend) {
        if(mCacheFriends == null) {
            mCacheFriends = new LinkedHashMap<>();
        }
        mCacheFriends.put(friend.getId(), friend);
    }
}
