package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
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
    public TaskHeadDetail mCachedTaskHeadDetail = null;
    /*
    * Key: memberId, Value: members => of one taskHead
    * */
    public Map<String, Member> mCachedMembers = null;

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

    @Override
    public void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {
        checkNotNull(taskHeadDetail);

        mLocalDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
            @Override
            public void onSaveSuccess() {
                callback.onSaveSuccess();
            }

            @Override
            public void onSaveFailed() {
                callback.onSaveFailed();
            }
        });

        // Do in memory cache update
        mCachedTaskHeadDetail = taskHeadDetail;
        if (mCachedMembers == null) {
            mCachedMembers = new LinkedHashMap<>();
        }
        refreshCacheMembers(taskHeadDetail.getMembers());
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

    private void refreshLocalDataSource(TaskHeadDetail taskHeadDetail) {
        mLocalDataSource.deleteTaskHeadDetail(taskHeadDetail.getTaskHead().getId());
        mLocalDataSource.saveTaskHeadDetail(taskHeadDetail, new SaveCallback() {
            @Override
            public void onSaveSuccess() {

            }

            @Override
            public void onSaveFailed() {
                // requery?
            }
        });
    }

    private void refreshCache(TaskHeadDetail taskHeadDetail) {
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
