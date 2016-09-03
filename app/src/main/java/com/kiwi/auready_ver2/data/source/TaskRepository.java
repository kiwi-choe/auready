package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/23/16.
 */
public class TaskRepository implements TaskDataSource {

    private static TaskRepository INSTANCE = null;

    private final TaskDataSource mTaskRemoteDataSource;
    private final TaskDataSource mTaskLocalDataSource;

    /*
    * This variable has package local visibility so it can be accessed from tests.
    * */
    private Map<String, List<Task>> mCachedTasks;
    private boolean mCacheIsDirty;

    // Prevent direct instantiation
    private TaskRepository(@NonNull TaskDataSource taskRemoteDataSource,
                           @NonNull TaskDataSource taskLocalDataSource) {

        mTaskRemoteDataSource = checkNotNull(taskRemoteDataSource);
        mTaskLocalDataSource = checkNotNull(taskLocalDataSource);
    }

    /*
    * Gets tasks from local data source by taskHeadId unless the table is new or empty. In that case it
    * uses the network data source. This is done to simplify the sample.
    * */
    public void getTasks(@NonNull final String taskHeadId, @NonNull final GetTasksCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        List<Task> cachedTasks = getTasksWithTaskHeadId(taskHeadId);

        // Respond immediately with cache if available
        if (cachedTasks != null) {
            callback.onTasksLoaded(cachedTasks);
            return;
        }

        // Load from server if needed.

        // Is the task in the local? If not, query the network.
        mTaskLocalDataSource.getTasks(taskHeadId, new GetTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                mTaskRemoteDataSource.getTasks(taskHeadId, new GetTasksCallback() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks) {
                        callback.onTasksLoaded(tasks);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void getAllTasks(@NonNull GetTasksCallback callback) {

    }

    @Nullable
    private List<Task> getTasksWithTaskHeadId(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskHeadId);
        }
    }

    @Override
    public void deleteTask(@NonNull String taskId) {


    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }

    @Override
    public void saveTask(@NonNull Task task, @NonNull final SaveTaskCallback callback) {
        checkNotNull(task);
//        mTaskRemoteDataSource.saveTask(task);
//        mTaskLocalDataSource.saveTask(task, new SaveTaskCallback() {
//            @Override
//            public void onTaskSaved() {
//                callback.onTaskSaved();
//            }
//
//            @Override
//            public void onTaskNotSaved() {
//                callback.onTaskNotSaved();
//            }
//        });

        // Do in memory cache update to keep the app UI up to date
        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        // Check that exists tasks of taskHeadId
        List<Task> tasksOfTaskHeadId = mCachedTasks.get(task.getTaskHeadId());
        if(tasksOfTaskHeadId == null) {
            tasksOfTaskHeadId = new ArrayList<>();
        }
        tasksOfTaskHeadId.add(task);
        mCachedTasks.put(task.getTaskHeadId(), tasksOfTaskHeadId);

        // when testing, used only cache.
        callback.onTaskSaved();
    }


    public static TaskRepository getInstance(TaskDataSource taskRemoteDataSource,
                                             TaskDataSource taskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskRepository(taskRemoteDataSource, taskLocalDataSource);
        }
        return INSTANCE;
    }

    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    /*
    * Used to force {@link #getInstance(TaskDataSource, TaskDataSource)} to create a new instance
    * next time it's called.
    * */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
