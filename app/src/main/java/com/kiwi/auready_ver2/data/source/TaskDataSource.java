package com.kiwi.auready_ver2.data.source;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;

import java.util.List;
import java.util.Map;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getTasksByMember() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface TaskDataSource {

    void saveMembers(List<Member> members);

    interface DeleteMembersCallback {
        void onDeleteSuccess();
        void onDeleteFail();
    }
    void deleteMembers(String taskHeadId, DeleteMembersCallback callback);

    void forceUpdateLocalATaskHeadDetail();

    interface  DeleteAllCallback {
        void onDeleteAllSuccess();
        void onDeleteAllFail();
    }

    void deleteAllTaskHeads(@NonNull DeleteAllCallback callback);

    interface InitLocalDataCallback {
        void onInitSuccess();
        void onInitFail();
    }
    void initializeLocalData(@NonNull InitLocalDataCallback callback); // only in Local

    /*
    * TaskHeadsView
    * */
    interface LoadTaskHeadDetailsCallback {

        void onTaskHeadDetailsLoaded(List<TaskHeadDetail> taskHeadDetails);

        void onDataNotAvailable();
    }

    void getTaskHeadDetails(@NonNull LoadTaskHeadDetailsCallback callback);

    interface DeleteTaskHeadsCallback {
        void onDeleteSuccess();
        void onDeleteFail();
    }
    void deleteTaskHeads(List<String> taskheadIds, @NonNull DeleteTaskHeadsCallback callback);

    int getTaskHeadsCount();

    interface UpdateTaskHeadOrdersCallback {
        void onUpdateSuccess();
        void onUpdateFailed();
    }
    void updateTaskHeadOrders(@NonNull List<TaskHead> taskHeads, @NonNull UpdateTaskHeadOrdersCallback callback);

    interface SaveTaskHeadDetailsCallback {
        void onSaveSuccess();
        void onSaveFailed();
    }
    void saveTaskHeadDetails(@NonNull List<TaskHeadDetail> taskHeadDetails, @NonNull SaveTaskHeadDetailsCallback callback);

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
                            @NonNull EditTaskHeadDetailCallback callback);

    interface GetTaskHeadDetailCallback {

        void onTaskHeadDetailLoaded(TaskHeadDetail taskHeadDetail);

        void onDataNotAvailable();
    }

    void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback);


    /*
    * TasksView
    * */
    interface LoadMembersCallback {

        void onMembersLoaded(List<Member> members);

        void onDataNotAvailable();
    }

    void getMembers(@NonNull String taskHeadId, @NonNull LoadMembersCallback callback);

    interface LoadTasksCallback {
        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }

    void getTasksOfMember(@NonNull String memberId, @NonNull LoadTasksCallback callback);

    void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull LoadTasksCallback callback);

    interface DeleteTaskCallback {
        void onDeleteSuccess(List<Task> tasksOfMember);
        void onDeleteFailed();
    }
    void deleteTask(String memberId, @NonNull String taskId, @NonNull List<Task> editingTasks, @NonNull DeleteTaskCallback callback);

    void editTasks(@NonNull String taskHeadId, @NonNull Map<String, List<Task>> tasks);

    interface SaveTaskCallback {
        void onSaveSuccess(List<Task> tasksOfMember);
        void onSaveFailed();
    }
    void saveTask(@NonNull Task task, @NonNull List<Task> editingTasks, @NonNull SaveTaskCallback callback);

    interface ChangeCompleteTaskCallback {
        void onChangeCompleteSuccess(List<Task> tasksOfMember);
        void onChangeCompleteFail();
    }
    void changeComplete(String memberId, String taskId, List<Task> editingTasks, ChangeCompleteTaskCallback callback);

    void deleteTasksOfMember(String memberId);

    interface ChangeOrdersCallback {
        void onChangeOrdersSuccess(List<Task> tasksOfMember);
        void onChangeOrdersFail();
    }
    void changeOrders(String memberId, List<Task> editingTasks, ChangeOrdersCallback callback);
}
