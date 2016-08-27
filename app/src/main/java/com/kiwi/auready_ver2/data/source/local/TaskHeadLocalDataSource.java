package com.kiwi.auready_ver2.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.source.TaskHeadDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskHeadLocalDataSource implements TaskHeadDataSource {

    private static TaskHeadLocalDataSource INSTANCE;

    private SQLiteDbHelper mDbHelper;

    private TaskHeadLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SQLiteDbHelper(context);
    }

    public static TaskHeadLocalDataSource getInstance(@NonNull Context context) {
        if(INSTANCE == null) {
            INSTANCE = new TaskHeadLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {

    }

    @Override
    public void deleteTaskHead(@NonNull String taskHeadId) {

    }
}
