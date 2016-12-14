package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskLocalDataSource implements TaskDataSource {

    private static TaskLocalDataSource INSTANCE;

    private SQLiteDbHelper mDbHelper;

    private TaskLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SQLiteDbHelper(context);
    }

    public static TaskLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TaskLocalDataSource(context);
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
    public void getTasks(@NonNull String taskHeadId, @NonNull String memberId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {
    }



}
