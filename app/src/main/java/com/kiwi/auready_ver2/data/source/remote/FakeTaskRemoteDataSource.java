package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.ArrayList;
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
    public void deleteAllTasks() {

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
    public void deleteTask(@NonNull Task task) {

    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }

    @Override
    public void saveTask(Task task, @NonNull SaveTaskCallback callback) {
        String taskHeadId = task.getTaskHeadId();
        List<Task> tasksOfTaskHeadId = TASKS_OF_TASKHEAD_SERVICE_DATA.get(taskHeadId);
        if(tasksOfTaskHeadId == null) {
            tasksOfTaskHeadId = new ArrayList<>(0);
        }
        tasksOfTaskHeadId.add(task);
        TASKS_OF_TASKHEAD_SERVICE_DATA.put(task.getTaskHeadId(), tasksOfTaskHeadId);
    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void sortTasks(LinkedHashMap<String, Task> taskList) {

    }

    @VisibleForTesting
    public void addTasks(String taskHeadId, List<Task> tasks) {
        TASKS_OF_TASKHEAD_SERVICE_DATA.put(taskHeadId, tasks);
    }
}
