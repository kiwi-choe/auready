package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Task;

import java.util.LinkedHashMap;
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

    interface GetTasksCallback {
        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    void getTasks(String taskHeadId, @NonNull GetTasksCallback callback);   // Get tasks of the taskHead with taskHeadId

    void getAllTasks(@NonNull GetTasksCallback callback);                   // Get all tasks

    void deleteTask(@NonNull String taskHeadId);

    void saveTasks(List<Task> tasks);

    interface SaveTaskCallback {
        void onTaskSaved();

        void onTaskNotSaved();
    }

    void saveTask(@NonNull Task task, @NonNull SaveTaskCallback callback);

    void completeTask(@NonNull Task task);

    void activateTask(@NonNull Task task);


    void sortTasks(LinkedHashMap<String, Task> taskList);
}
