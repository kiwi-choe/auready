package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

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


    /*
                * TaskHeadsView
                * */
    interface LoadTaskHeadsCallback {

        void onTaskHeadsLoaded(List<TaskHead> taskHeads);

        void onDataNotAvailable();
    }

    void getTaskHeads(@NonNull LoadTaskHeadsCallback callback);

    void deleteTaskHeads(List<String> taskheadIds);

    int getTaskHeadsCount();

    /*
    * TaskHeadDetailView
    * */
    interface SaveCallback {

        void onSaveSuccess();

        void onSaveFailed();
    }

    void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull SaveCallback callback);

    interface EditTaskHeadDetailCallback {

        void onEditSuccess();

        void onEditFailed();
    }

    void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                            @NonNull List<Member> addingMembers,
                            @NonNull List<String> deletingMemberIds,
                            @NonNull EditTaskHeadDetailCallback callback);

    interface GetTaskHeadDetailCallback {

        void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail);

        void onDataNotAvailable();
    }

    void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback);

    /*
    * TasksView
    * */

//    interface LoadTasksCallback {
//        void onTasksLoaded(List<Task> tasks);
//
//        void onDataNotAvailable();
//    }
//
//    interface DeleteTasksCallback {
//        void onDeleteSuccess();
//
//        void onDeleteFail();
//    }

    // Get tasks by taskHeadId and memberId
//    void getTasks(@NonNull String taskHeadId, @NonNull String memberId, @NonNull LoadTasksCallback callback);
    // Get tasks by taskHeadId
//    void getTasks(@NonNull String taskHeadId, @NonNull LoadTasksCallback callback);

//    void saveTask(@NonNull Task task);

    // When delete a taskhead
//    void deleteTasks(@NonNull List<String> taskHeadIds);
    // When delete a member of taskHeadDetail(update taskhead)
//    void deleteTasks(@NonNull String taskHeadId, @NonNull String memberId);

//    void deleteTask(@NonNull String id);
}
