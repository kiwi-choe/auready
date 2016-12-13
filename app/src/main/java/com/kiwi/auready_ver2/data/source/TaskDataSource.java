package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Task;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface TaskDataSource {

    void deleteAllTasks();

    interface GetTasksCallback {
        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    // Get tasks by taskHeadId and memberId
    void getTasks(String taskHeadId, String memberId, @NonNull GetTasksCallback callback);

    void saveTask(@NonNull Task task);
}
