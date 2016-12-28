package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kiwi.auready_ver2.data.Friend;
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
    public Map<String, TaskHead> mCachedTaskHeads = null;

    // Prevent direct instantiation
    private TaskHeadRepository(@NonNull TaskHeadDataSource taskHeadRemoteDataSource,
                               @NonNull TaskHeadDataSource taskHeadLocalDataSource) {

        mTaskHeadRemoteDataSource = checkNotNull(taskHeadRemoteDataSource);
        mTaskHeadLocalDataSource = checkNotNull(taskHeadLocalDataSource);
    }

    @Override
    public int getTaskHeadsCount() {

        if (mCachedTaskHeads != null) {
            return mCachedTaskHeads.size();
        }
        return mTaskHeadLocalDataSource.getTaskHeadsCount();
    }

    @Override
    public void updateTaskHeadsOrder(List<TaskHead> taskHeads) {
        mTaskHeadLocalDataSource.updateTaskHeadsOrder(taskHeads);

        refreshCache(taskHeads);
    }

    @Override
    public void editTaskHead(@NonNull String id, String title, List<Friend> members) {
        checkNotNull(id);
        mTaskHeadLocalDataSource.editTaskHead(id, title, members);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        TaskHead taskHead = mCachedTaskHeads.get(id);
        TaskHead editedTaskHead = new TaskHead(id, title, members, taskHead.getOrder());
        mCachedTaskHeads.put(id, editedTaskHead);
    }

    @Override
    public void deleteTaskHeads(List<String> taskHeadIds) {
        mTaskHeadLocalDataSource.deleteTaskHeads(taskHeadIds);

        for(String taskHeadId:taskHeadIds) {
            mCachedTaskHeads.remove(taskHeadId);
        }
    }

    @Override
    public void getTaskHeads(@NonNull final LoadTaskHeadsCallback callback) {

        checkNotNull(callback);
        // Respond immediately with cache if available
        if (mCachedTaskHeads != null) {
            callback.onTaskHeadsLoaded(new ArrayList<>(mCachedTaskHeads.values()));
            return;
        } else {
            // Query the local storage if available, if not, query the network.
            mTaskHeadLocalDataSource.getTaskHeads(new LoadTaskHeadsCallback() {
                @Override
                public void onTaskHeadsLoaded(List<TaskHead> taskHeads) {
                    refreshCache(taskHeads);
                    callback.onTaskHeadsLoaded(new ArrayList<>(mCachedTaskHeads.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTaskHeadsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getTaskHead(@NonNull final String taskHeadId, @NonNull final GetTaskHeadCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        TaskHead cachedTaskHead = getTaskHeadWithId(taskHeadId);
        // Respond immediately with cache if available and not dirty
//        if (cachedTaskHead != null) {
//            callback.onTaskHeadLoaded(cachedTaskHead);
//            return;
//        }

        // Is the taskhead in the local? if not, query the network.
        mTaskHeadLocalDataSource.getTaskHead(taskHeadId, new GetTaskHeadCallback() {
            @Override
            public void onTaskHeadLoaded(TaskHead taskHead) {
                callback.onTaskHeadLoaded(taskHead);
            }

            @Override
            public void onDataNotAvailable() {
                mTaskHeadRemoteDataSource.getTaskHead(taskHeadId, new GetTaskHeadCallback() {
                    @Override
                    public void onTaskHeadLoaded(TaskHead taskHead) {
                        callback.onTaskHeadLoaded(taskHead);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Nullable
    private TaskHead getTaskHeadWithId(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        if (mCachedTaskHeads == null || mCachedTaskHeads.isEmpty()) {
            return null;
        } else {
            return mCachedTaskHeads.get(taskHeadId);
        }
    }

    @Override
    public void deleteAllTaskHeads() {
//        mTaskHeadRemoteDataSource.deleteAllTaskHeads();
        mTaskHeadLocalDataSource.deleteAllTaskHeads();

        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.clear();
    }

    @Override
    public void saveTaskHead(@NonNull TaskHead taskHead) {
        checkNotNull(taskHead);

//        mTaskHeadRemoteDataSource.createTaskHead(taskHead);
        mTaskHeadLocalDataSource.saveTaskHead(taskHead);

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
        mTaskHeadLocalDataSource.deleteAllTaskHeads();
        for (TaskHead taskHead : taskHeads) {
            mTaskHeadLocalDataSource.saveTaskHead(taskHead);
        }
    }

    private void refreshCache(List<TaskHead> taskHeads) {
        if (mCachedTaskHeads == null) {
            mCachedTaskHeads = new LinkedHashMap<>();
        }
        mCachedTaskHeads.clear();
        for (TaskHead taskHead : taskHeads) {
            mCachedTaskHeads.put(taskHead.getId(), taskHead);
        }
    }

    public static TaskHeadRepository getInstance(TaskHeadDataSource taskHeadRemoteDataSource,
                                                 TaskHeadDataSource taskHeadLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskHeadRepository(taskHeadRemoteDataSource, taskHeadLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
