package com.kiwi.auready_ver2.data.source.remote;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiwi on 8/23/16.
 */
public class FakeTaskRemoteDataSource implements TaskDataSource {

    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    private static FakeTaskRemoteDataSource INSTANCE;

    public static FakeTaskRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FakeTaskRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void deleteTask(@NonNull String id) {

    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }
}
