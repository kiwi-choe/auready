package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.kiwi.auready_ver2.data.Task;

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
    * Tasks of Member(by taskHeadId, memberId)
    * */
    private static final Map<TaskMapKey, List<Task>> TASKS_OF_MEMBER_SERVICE_DATA = new LinkedHashMap<>();

    private static FakeTaskRemoteDataSource INSTANCE;

    public static FakeTaskRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTaskRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull String memberId) {

    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull DeleteTasksCallback callback) {

    }

    @Override
    public void deleteTask(@NonNull String id) {

    }

    @Override
    public void getTasks(String taskHeadId, String memberId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @VisibleForTesting
    public void addTasks(String taskHeadId, String memberId, List<Task> tasks) {
        TASKS_OF_MEMBER_SERVICE_DATA.put(new TaskMapKey(taskHeadId, memberId), tasks);
    }
}
