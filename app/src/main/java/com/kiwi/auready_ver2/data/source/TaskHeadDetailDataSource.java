package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHeadDetail;

/**
 * Main entry point for accessing taskHeadDetail data.
 */

public interface TaskHeadDetailDataSource {

    void deleteTaskHeadDetail(@NonNull String taskHeadId);

    interface GetTaskHeadDetailCallback {

        void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail);

        void onDataNotAvailable();
    }

    void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull SaveCallback callback);

    void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback);

    interface SaveCallback {

        void onSaveSuccess();

        void onSaveFailed();
    }
}
