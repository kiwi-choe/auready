package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Main entry point for accessing taskHeads data.
 */
public interface TaskHeadDataSource {

    interface LoadTaskHeadsCallback {
        void onTaskHeadsLoaded(List<TaskHead> taskHeads);

        void onDataNotAvailable();
    }

    void getTaskHeads(@NonNull LoadTaskHeadsCallback callback);

    void deleteTaskHead(@NonNull String taskHeadId);
}
