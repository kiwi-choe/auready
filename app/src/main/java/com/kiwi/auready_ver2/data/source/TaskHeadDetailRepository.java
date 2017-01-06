package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Process TaskHead and Member data repository
 */

public class TaskHeadDetailRepository implements TaskHeadDetailDataSource {

    private static TaskHeadDetailRepository INSTANCE = null;

    private final TaskHeadDetailDataSource mRemoteDataSource;
    private final TaskHeadDetailDataSource mLocalDataSource;

    /*
    * Cache for testing
    * */
    TaskHeadDetail mCachedTaskHeadDetail = null;
    /*
    * Key: memberId, Value: members => of one taskHead
    * */
    Map<String, Member> mCachedMembers = null;

    private TaskHeadDetailRepository(@NonNull TaskHeadDetailDataSource remoteDataSource,
                                     @NonNull TaskHeadDetailDataSource localDataSource) {
        mRemoteDataSource = checkNotNull(remoteDataSource);
        mLocalDataSource = checkNotNull(localDataSource);
    }

    @Override
    public void deleteTaskHeadDetail(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        mLocalDataSource.deleteTaskHeadDetail(taskHeadId);

        // Clear cache of this taskHeadId
        if (mCachedTaskHeadDetail != null) {
            mCachedTaskHeadDetail = null;
        }
        if (mCachedMembers != null) {
            mCachedMembers.clear();
        }
    }

    public void saveTaskHeadDetail(@NonNull final TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {
        checkNotNull(taskHeadDetail);

        saveTaskHead(taskHeadDetail.getTaskHead(), new SaveCallback() {
            @Override
            public void onSaveSuccess() {

                saveMembers(taskHeadDetail.getMembers(), new SaveCallback() {
                    @Override
                    public void onSaveSuccess() {
                        refreshCache(taskHeadDetail);
                        callback.onSaveSuccess();
                    }

                    @Override
                    public void onSaveFailed() {
                        callback.onSaveFailed();
                    }
                });
            }

            @Override
            public void onSaveFailed() {

            }
        });
    }

    @Override
    public void saveTaskHead(@NonNull final TaskHead taskHead, @NonNull final SaveCallback callback) {
        checkNotNull(taskHead);
        checkNotNull(callback);
        mLocalDataSource.saveTaskHead(taskHead, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                callback.onSaveSuccess();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void saveMembers(@NonNull List<Member> members, @NonNull final SaveCallback callback) {
        checkNotNull(members);
        checkNotNull(callback);

        mLocalDataSource.saveMembers(members, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                callback.onSaveSuccess();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void getTaskHeadDetail(@NonNull final String taskHeadId, @NonNull final GetTaskHeadDetailCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedTaskHeadDetail != null) {
            callback.onTaskHeadDetailLoaded(mCachedTaskHeadDetail);
            return;
        }

        // Is the taskhead in the local? if not, query the network.
        mLocalDataSource.getTaskHeadDetail(taskHeadId, new GetTaskHeadDetailCallback() {

            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                refreshCache(taskHeadDetail);
                callback.onTaskHeadDetailLoaded(taskHeadDetail);
            }

            @Override
            public void onDataNotAvailable() {
                getTaskHeadDetailFromRemote(taskHeadId, callback);
            }
        });
    }

    private void getTaskHeadDetailFromRemote(@NonNull String taskHeadId, @NonNull final GetTaskHeadDetailCallback callback) {
        mRemoteDataSource.getTaskHeadDetail(taskHeadId, new GetTaskHeadDetailCallback() {

            @Override
            public void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail) {
                refreshCache(taskHeadDetail);
                refreshLocalDataSource(taskHeadDetail);
                callback.onTaskHeadDetailLoaded(taskHeadDetail);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(final TaskHeadDetail taskHeadDetail) {
        mLocalDataSource.deleteTaskHeadDetail(taskHeadDetail.getTaskHead().getId());
        mLocalDataSource.saveTaskHead(taskHeadDetail.getTaskHead(), new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                mLocalDataSource.saveMembers(taskHeadDetail.getMembers(), new SaveCallback() {
                    @Override
                    public void onSaveSuccess() {

                    }

                    @Override
                    public void onSaveFailed() {

                    }
                });
            }

            @Override
            public void onSaveFailed() {

            }
        });
    }

    private void refreshCache(TaskHeadDetail taskHeadDetail) {
        // Do in memory cache update
        mCachedTaskHeadDetail = taskHeadDetail;
        refreshCacheMembers(taskHeadDetail.getMembers());
    }

    private void refreshCacheMembers(List<Member> members) {
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        mCachedMembers.clear();
        for (Member member : members) {
            mCachedMembers.put(member.getId(), member);
        }
    }

    public static TaskHeadDetailRepository getInstance(TaskHeadDetailDataSource remoteDataSource,
                                                       TaskHeadDetailDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadDetailRepository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
