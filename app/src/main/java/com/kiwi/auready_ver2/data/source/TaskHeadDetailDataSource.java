package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.List;

/**
 * Main entry point for accessing taskHeadDetail data.
 */

public interface TaskHeadDetailDataSource {

    void deleteTaskHeadDetail(@NonNull String taskHeadId);

    interface GetTaskHeadDetailCallback {

        void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail);

        void onDataNotAvailable();
    }

    interface SaveCallback {

        void onSaveSuccess();

        void onSaveFailed();
    }

    void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback);

    void saveTaskHead(@NonNull TaskHead taskHead, @NonNull SaveCallback callback);

    void saveMembers(@NonNull List<Member> members, @NonNull SaveCallback callback);
}
