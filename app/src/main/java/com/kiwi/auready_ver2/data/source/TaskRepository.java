package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.util.OrderAscCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    * First Map: KEY taskHeadId, VALUE tasks
    * Second Map: KEY taskId, VALUE task
    * */
    public Map<String, Map<String, Task>> mCachedTasks;
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
    public void getTasks(@NonNull final String taskHeadId, @NonNull final GetTasksCallback callback) {
        checkNotNull(taskHeadId);
        checkNotNull(callback);

        Map<String, Task> cachedTasks = getTasksWithTaskHeadId(taskHeadId);

        // Respond immediately with cache if available
        if (cachedTasks != null) {
            List<Task> taskList = new ArrayList<>(cachedTasks.values());
            // Sorting by order
            Collections.sort(taskList, new OrderAscCompare());

            callback.onTasksLoaded(taskList);
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
    private Map<String, Task> getTasksWithTaskHeadId(@NonNull String taskHeadId) {
        checkNotNull(taskHeadId);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskHeadId);
        }
    }

    @Override
    public void deleteTask(@NonNull Task task) {
        checkNotNull(task);
        if(mCachedTasks.get(task.getTaskHeadId()).containsKey(task.getId())) {
            mCachedTasks.get(task.getTaskHeadId()).remove(task.getId());
        }
    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }

    @Override
    public void saveTask(@NonNull final Task task, @NonNull final SaveTaskCallback callback) {
        checkNotNull(task);
        mTaskRemoteDataSource.saveTask(task, new SaveTaskCallback() {
            @Override
            public void onTaskSaved() {

//                callback.onTaskSaved();
                mTaskLocalDataSource.saveTask(task, new SaveTaskCallback() {
                    @Override
                    public void onTaskSaved() {
                        callback.onTaskSaved();
                    }

                    @Override
                    public void onTaskNotSaved() {
                        callback.onTaskNotSaved();
                    }
                });
            }

            @Override
            public void onTaskNotSaved() {
                callback.onTaskNotSaved();
            }
        });


        // Do in memory cache update to keep the app UI up to date
        putToCachedTasks(task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
//        mTaskRemoteDataSource.completeTask(task);

        Task completedTask = new Task(task.getTaskHeadId(), task.getId(), task.getDescription(), true);
        // Do in memory cache update to keep the app UI up to date
        putToCachedTasks(completedTask);

        // Increase the order of complete tasks
        updateTasksOrderNextOf(task);
    }

    private void updateTasksOrderNextOf(Task task) {
        Map<String, Task> cachedTasks = getTasksWithTaskHeadId(task.getTaskHeadId());

        if (cachedTasks != null) {
            int sizeOfCachedTasks = cachedTasks.size();

            Task changedTask = cachedTasks.get(task.getId());
            int orderOfChangedTask = changedTask.getOrder();

            for (String key : cachedTasks.keySet()) {
                Task tmpTask = cachedTasks.get(key);

                // Active -> Complete
                if (changedTask.isCompleted()) {
                    // order of task +1 ~ size of tasks
                    if (orderOfChangedTask + 1 < tmpTask.getOrder() &&
                            tmpTask.getOrder() < sizeOfCachedTasks) {
                        tmpTask.decreaseOrder();
                    }
                    // Set the order of changedTask
                    changedTask.setOrder(sizeOfCachedTasks - 1);
                }
                // Complete -> Active
                else {
                    // size of active tasks ~ order of task
                    int numOfActiveTasks = getNumOfActiveTasks(cachedTasks);
                    if (numOfActiveTasks <= tmpTask.getOrder() &&
                            tmpTask.getOrder() < orderOfChangedTask) {
                        tmpTask.increaseOrder();
                    }
                    // Set the order of changedTask
                    changedTask.setOrder(numOfActiveTasks);
                }
            }
        }

    }

    private int getNumOfActiveTasks(Map<String, Task> cachedTasks) {
        Map<String, Task> tasks = cachedTasks;
        int numOfActiveTasks = 0;

        for (String key : tasks.keySet()) {
            if (tasks.get(key).isActive()) {
                numOfActiveTasks++;
            }
        }
        return numOfActiveTasks;
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
//        mTaskRemoteDataSource.activateTask(task);

        Task activeTask = new Task(task.getTaskHeadId(), task.getId(), task.getDescription());
        putToCachedTasks(activeTask);

        // Decrease the order of active tasks
        updateTasksOrderNextOf(task);
    }

    @Override
    public void sortTasks(LinkedHashMap<String, Task> taskList) {

        int order = 0;
        for (Map.Entry<String, Task> entry : taskList.entrySet()) {

            Task task = entry.getValue();
            task.setOrder(order);
            putToCachedTasks(task);

            order++;
        }
    }

    private void putToCachedTasks(Task task) {

        Map<String, Task> tasks;
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
            tasks = new LinkedHashMap<>();

        } else {
            tasks = mCachedTasks.get(task.getTaskHeadId());
            if (tasks == null) {
                tasks = new LinkedHashMap<>();
            }
        }
        tasks.put(task.getId(), task);
        mCachedTasks.put(task.getTaskHeadId(), tasks);
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
