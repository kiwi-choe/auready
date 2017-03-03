package com.kiwi.auready_ver2.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.task.ITaskService;
import com.kiwi.auready_ver2.rest_service.task.Member_remote;
import com.kiwi.auready_ver2.rest_service.task.TaskHead_remote;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the remote data source
 */

public class TaskRemoteDataSource implements TaskDataSource {

    private static TaskRemoteDataSource INSTANCE;
    private final String mAccessToken;

    private TaskRemoteDataSource(Context context) {
        mAccessToken =
                AccessTokenStore.getInstance(context).getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
    }

    public static TaskDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskRemoteDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void getTaskHeads(@NonNull LoadTaskHeadsCallback callback) {

    }

    @Override
    public void deleteTaskHeads(List<String> taskheadIds) {

    }

    // for Local
    @Override
    public int getTaskHeadsCount() {
        return 0;
    }

    @Override
    public void updateTaskHeadOrders(@NonNull List<TaskHead> taskHeads) {

    }

    @Override
    public void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        // Make Object for remote
        List<Member_remote> memberRemotes = new ArrayList<>();
        List<Member> members = taskHeadDetail.getMembers();
        for(Member member:members) {
            Member_remote memberRemote = new Member_remote(
                    member.getId(),
                    member.getName(),
                    member.getEmail());
            memberRemotes.add(memberRemote);
        }
        TaskHead_remote taskHeadRemote = new TaskHead_remote(
                taskHeadDetail.getTaskHead().getId(),
                taskHeadDetail.getTaskHead().getTitle(),
                memberRemotes);
        Call<Void> call = taskService.saveTaskHeadDetail(taskHeadRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("test_SaveTaskHead", "response.isSuccessful()");
                    callback.onSaveSuccess();
                } else {
                    callback.onSaveFailed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead, @NonNull List<Member> addingMembers, @NonNull List<String> deletingMemberIds, @NonNull EditTaskHeadDetailCallback callback) {

    }

    @Override
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull GetTaskHeadDetailCallback callback) {

    }

    @Override
    public void getMembers(@NonNull String taskHeadId, @NonNull LoadMembersCallback callback) {

    }

    @Override
    public void getTasksOfMember(@NonNull String memberId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @Override
    public void deleteTasks(@NonNull List<String> taskIds) {

    }

    @Override
    public void editTasks(@NonNull List<Task> tasks) {

    }
}
