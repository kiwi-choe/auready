package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.TaskHead;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskHeadRepository implements TaskHeadDataSource {

    private static TaskHeadRepository INSTANCE = null;

    private final TaskHeadDataSource mTaskHeadRemoteDataSource;
    private final TaskHeadDataSource mTaskHeadLocalDataSource;

    /*
    * This variable has package local visibility so it can be accessed from tests.
    * */
    Map<String, TaskHead> mCachedTaskHeads;

    private boolean mCacheIsDirty;

    // Prevent direct instantiation
    private TaskHeadRepository(@NonNull TaskHeadDataSource taskHeadRemoteDataSource,
                               @NonNull TaskHeadDataSource taskHeadLocalDataSource) {

        mTaskHeadRemoteDataSource = checkNotNull(taskHeadRemoteDataSource);
        mTaskHeadLocalDataSource = checkNotNull(taskHeadLocalDataSource);
    }

    public void getTaskHeads(@NonNull final LoadTaskHeadsCallback callback) {
        Log.d("kiwi_test", "----------------------------------getTaskHeads");

        checkNotNull(callback);
        // Respond immediately with cache if available and not dirty
        if (mCachedTaskHeads != null && !mCacheIsDirty) {

            Log.d("kiwi_test", "entered into repository getTaskHeads 1, " + String.valueOf(mCachedTaskHeads.size()));
            callback.onTaskHeadsLoaded(new ArrayList<>(mCachedTaskHeads.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTaskHeadsFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available, if not, query the network.
//            mTaskHeadLocalDataSource.getTaskHeads(new LoadTaskHeadsCallback() {
//                @Override
//                public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
//                    refreshCache(taskHeads);
//                    callback.onTaskHeadsLoaded(new ArrayList<>(mCachedTaskHeads.values()));
//                }
//
//                @Override
//                public void onDataNotAvailable() {
//                    getTaskHeadsFromRemoteDataSource(callback);
//                }
//            });

            getTaskHeadsFromRemoteDataSource(callback);

            Log.d("kiwi_test", "entered into repository getTaskHeads 2, " + String.valueOf(mCachedTaskHeads.size()));
        }
    }

    @Override
    public void deleteTaskHead(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        mTaskHeadRemoteDataSource.deleteTaskHead(taskHeadId);

        mCachedTaskHeads.remove(taskHeadId);
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {
        checkNotNull(taskHead);
//        mTaskHeadRemoteDataSource.saveTaskHead(taskHead);
//        mTaskHeadLocalDataSource.saveTaskHead(taskHead);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.put(taskHead.getId(), taskHead);
    }

    private void getTaskHeadsFromRemoteDataSource(@NonNull final LoadTaskHeadsCallback callback) {
        mTaskHeadRemoteDataSource.getTaskHeads(new LoadTaskHeadsCallback() {
            @Override
            public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                refreshCache(taskHeads);
                refreshLocalDataSource(taskHeads);
                callback.onTaskHeadsLoaded(new ArrayList<>(mCachedTaskHeads.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<TaskHead> taskHeads) {
    }

    private void refreshCache(List<TaskHead> taskHeads) {
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.clear();
        for (TaskHead taskHead : taskHeads) {
            mCachedTaskHeads.put(taskHead.getId(), taskHead);
        }
        mCacheIsDirty = false;
    }

    public static TaskHeadRepository getInstance(TaskHeadDataSource taskHeadRemoteDataSource,
                                                 TaskHeadDataSource taskHeadLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadRepository(taskHeadRemoteDataSource, taskHeadLocalDataSource);
        }
        return INSTANCE;
    }

    public void refreshTaskHeads() {
        mCacheIsDirty = true;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
