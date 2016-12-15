package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

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
    * Key: taskHeadId, memberId
    * Value: tasks of member
    * */
    public Map<TaskMapKey, List<Task>> mCachedTasksByTaskMapKey;
    /*
    * Key: taskHeadId
    * Value: tasks of taskHead
    * */
    public Map<String, List<Task>> mCachedTasksByTaskHeadId;
    /*
    * Key: taskId
    * Value: task
    * */
    public Map<String, Task> mCachedTasksById;

    private boolean mCacheIsDirty;

    // Prevent direct instantiation
    private TaskRepository(@NonNull TaskDataSource taskRemoteDataSource,
                           @NonNull TaskDataSource taskLocalDataSource) {

        mTaskRemoteDataSource = checkNotNull(taskRemoteDataSource);
        mTaskLocalDataSource = checkNotNull(taskLocalDataSource);
    }


    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull String memberId) {
        mTaskLocalDataSource.deleteTasks(taskHeadId, memberId);

        mCachedTasksByTaskMapKey.remove(new TaskMapKey(taskHeadId, memberId));
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull DeleteTasksCallback callback) {
        mTaskLocalDataSource.deleteTasks(taskHeadId, callback);

        mCachedTasksByTaskHeadId.remove(taskHeadId);
    }

    @Override
    public void deleteTask(@NonNull String id) {
        mTaskRemoteDataSource.deleteTask(id);
        mTaskLocalDataSource.deleteTask(id);

        // cache
        mCachedTasksById.remove(id);
    }

    /*
    * Gets tasks from local data source by taskHeadId and memberId
    * unless the table is new or empty. In that case it uses the network data source.
    * This is done to simplify the sample.
    * */
    @Override
    public void getTasks(@NonNull final String taskHeadId, @NonNull final String memberId, @NonNull final LoadTasksCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(memberId);

        // Is the taskhead in the local? if not, query the network.
        mTaskLocalDataSource.getTasks(taskHeadId, memberId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                getTasksFromRemote(taskHeadId, memberId, callback);

            }
        });
    }

    private void getTasksFromRemote(final String taskHeadId, final String memberId, final LoadTasksCallback callback) {
        mTaskRemoteDataSource.getTasks(taskHeadId, memberId, new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshLocalDataSource(taskHeadId, memberId, tasks);
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(String taskHeadId, String memberId, List<Task> tasks) {
        mTaskLocalDataSource.deleteTasks(taskHeadId, memberId);
        for (Task task : tasks) {
            mTaskLocalDataSource.saveTask(task);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskRemoteDataSource.saveTask(task);
        mTaskLocalDataSource.saveTask(task);

        // Save to cache
        if (mCachedTasksById == null) {
            mCachedTasksById = new LinkedHashMap<>();
        }
        mCachedTasksById.put(task.getId(), task);

        if (mCachedTasksByTaskMapKey == null) {
            mCachedTasksByTaskMapKey = new LinkedHashMap<>();
        }
        List<Task> tasks = mCachedTasksByTaskMapKey.get(new TaskMapKey(task.getTaskHeadId(), task.getMemberId()));
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        mCachedTasksByTaskMapKey.put(new TaskMapKey(task.getTaskHeadId(), task.getMemberId()), tasks);

        if (mCachedTasksByTaskHeadId == null) {
            mCachedTasksByTaskHeadId = new LinkedHashMap<>();
        }
        List<Task> tasksOfTaskHead = mCachedTasksByTaskHeadId.get(task.getTaskHeadId());
        if (tasksOfTaskHead == null) {
            tasksOfTaskHead = new ArrayList<>();
        }
        tasksOfTaskHead.add(task);
        mCachedTasksByTaskHeadId.put(task.getTaskHeadId(), tasks);
    }


    public static TaskRepository getInstance(TaskDataSource taskRemoteDataSource,
                                             TaskDataSource taskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TaskRepository(taskRemoteDataSource, taskLocalDataSource);
        }
        return INSTANCE;
    }

    /*
    * Used to force {@link #getInstance(TaskDataSource, TaskDataSource)} to create a new instance
    * next time it's called.
    * */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
