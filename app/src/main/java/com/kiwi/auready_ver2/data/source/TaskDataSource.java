package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * Main entry point for accessing tasks data.
 */
public interface TaskDataSource {

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    void getTasks(@NonNull LoadTasksCallback callback);

    void deleteTask(@NonNull String taskHeadId);

    void saveTasks(List<Task> tasks);
}
