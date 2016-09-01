package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/23/16.
 */
public class FakeTaskRemoteDataSource implements TaskDataSource {

    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    /*
    * Tasks of TaskHead(by taskHeadId)
    * */
    private static final Map<String, List<Task>> TASKS_OF_TASKHEAD_SERVICE_DATA = new LinkedHashMap<>();

    private static FakeTaskRemoteDataSource INSTANCE;

    public static FakeTaskRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTaskRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(String taskHeadId, @NonNull GetTasksCallback callback) {

        if (TASKS_OF_TASKHEAD_SERVICE_DATA.get(taskHeadId) == null) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(TASKS_OF_TASKHEAD_SERVICE_DATA.get(taskHeadId));
        }
    }

    @Override
    public void getAllTasks(@NonNull GetTasksCallback callback) {
    }

    @Override
    public void deleteTask(@NonNull String id) {

    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }

    @Override
    public void saveTask(Task task) {

    }

    @VisibleForTesting
    public void addTasks(String taskHeadId, List<Task> tasks) {
        TASKS_OF_TASKHEAD_SERVICE_DATA.put(taskHeadId, tasks);
    }
}
