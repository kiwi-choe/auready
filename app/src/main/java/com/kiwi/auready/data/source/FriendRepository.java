package com.kiwi.auready.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready.data.Friend;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Friend Repository
 */
public class FriendRepository implements FriendDataSource {

    private static FriendRepository INSTANCE = null;

    private final FriendDataSource mLocalDataSource;
    private final FriendDataSource mRemoteDataSource;

    public Map<String, Friend> mCacheFriends;

    private FriendRepository(@NonNull FriendDataSource localDataSource, FriendDataSource remoteDataSource) {
        mLocalDataSource = checkNotNull(localDataSource);
        mRemoteDataSource = remoteDataSource;
    }

    public static FriendRepository getInstance(FriendDataSource localDataSource, FriendDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FriendRepository(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void deleteAllFriends() {
        mLocalDataSource.deleteAllFriends();

        if (mCacheFriends != null) {
            mCacheFriends.clear();
        }
    }

    @Override
    public void deleteFriend(@NonNull String id) {
        checkNotNull(id);
        mLocalDataSource.deleteFriend(id);

        if (mCacheFriends != null) {
            mCacheFriends.remove(id);
        }
    }

    @Override
    public void getFriends(@NonNull final LoadFriendsCallback callback) {
        checkNotNull(callback);

        mRemoteDataSource.getFriends(new LoadFriendsCallback() {
            @Override
            public void onFriendsLoaded(List<Friend> friends) {
                refreshCaches(friends);
                refreshLocalDataSource(friends);
                callback.onFriendsLoaded(friends);
            }

            @Override
            public void onDataNotAvailable() {
                // Get from Local when Network couldn't connect
                mLocalDataSource.getFriends(new LoadFriendsCallback() {
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
        });
    }

    private void refreshLocalDataSource(List<Friend> friends) {
        mLocalDataSource.deleteAllFriends();
        for (Friend friend : friends) {
            mLocalDataSource.saveFriend(friend, new SaveCallback() {
                @Override
                public void onSaveSuccess() {

                }

                @Override
                public void onSaveFailed() {

                }
            });
        }
    }

    private void refreshCaches(List<Friend> friends) {
        if (mCacheFriends == null) {
            mCacheFriends = new LinkedHashMap<>();
        }
        mCacheFriends.clear();
        for (Friend friend : friends) {

            mCacheFriends.put(friend.getUserId(), friend);
        }
    }

    @Override
    public void saveFriend(@NonNull final Friend friend, @NonNull SaveCallback callback) {

        checkNotNull(friend);
        mLocalDataSource.saveFriend(friend, new SaveCallback() {
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
        if (mCacheFriends == null) {
            mCacheFriends = new LinkedHashMap<>();
        }
        mCacheFriends.put(friend.getUserId(), friend);
    }
}