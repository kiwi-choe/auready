package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.OrderAscCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    * First Map: (Key: taskHeadId, memberId), (Value: tasks of member)
    * Second Map: mTasksOfMember(Key: taskId), (Value: task)
    * */
    public Map<TaskMapKey, Map<String, Task>> mCachedTasks;
    public Map<String, Task> mTasksOfMember;

    private boolean mCacheIsDirty;

    // Prevent direct instantiation
    private TaskRepository(@NonNull TaskDataSource taskRemoteDataSource,
                           @NonNull TaskDataSource taskLocalDataSource) {

        mTaskRemoteDataSource = checkNotNull(taskRemoteDataSource);
        mTaskLocalDataSource = checkNotNull(taskLocalDataSource);
    }

    @Override
    public void deleteAllTasks() {

    }

    /*
        * Gets tasks from local data source by taskHeadId unless the table is new or empty. In that case it
        * uses the network data source. This is done to simplify the sample.
        * */
    @Override
    public void getTasks(String taskHeadId, String memberId, @NonNull GetTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
//        mTaskRemoteDataSource.saveTask(task);
        mTaskLocalDataSource.saveTask(task);

        // Do in memory cache update
        if(mTasksOfMember == null) {
            mTasksOfMember = new LinkedHashMap<>();
        }
        mTasksOfMember.put(task.getId(), task);

        if(mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(new TaskMapKey(task.getTaskHeadId(), task.getMemberId()), mTasksOfMember);
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
