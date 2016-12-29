package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Main entry point for accessing taskHeads data.
 */
public interface TaskHeadDataSource {

    int getTaskHeadsCount();   // for Local data source

    void updateTaskHeadsOrder(List<TaskHead> taskHeads);

    void editTaskHead(@NonNull String id, String title, List<Friend> members);

    void addMembers(@NonNull String id, List<Friend> members);

    interface LoadTaskHeadsCallback {

        void onTaskHeadsLoaded(List<TaskHead> taskHeads);

        void onDataNotAvailable();
    }
    interface GetTaskHeadCallback {

        void onTaskHeadLoaded(TaskHead taskHead);

        void onDataNotAvailable();
    }

    void getTaskHeads(@NonNull LoadTaskHeadsCallback callback);

    void getTaskHead(@NonNull String taskHeadId, @NonNull GetTaskHeadCallback callback);

    void saveTaskHead(@NonNull TaskHead taskHead);

    void deleteTaskHeads(List<String> taskHeadIds);

    void deleteAllTaskHeads();
}
