package com.kiwi.auready_ver2.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.data.TaskHeadDetail;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.AccessTokenStore;
import com.kiwi.auready_ver2.rest_service.HttpStatusCode;
import com.kiwi.auready_ver2.rest_service.ServiceGenerator;
import com.kiwi.auready_ver2.rest_service.task.DeletingIds_remote;
import com.kiwi.auready_ver2.rest_service.task.ITaskService;
import com.kiwi.auready_ver2.rest_service.task.Member_remote;
import com.kiwi.auready_ver2.rest_service.task.TaskHeadDetail_remote;
import com.kiwi.auready_ver2.rest_service.task.Task_remote;
import com.kiwi.auready_ver2.tasks.MemberTasks;
import com.kiwi.auready_ver2.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the remote data source
 */

public class TaskRemoteDataSource implements TaskDataSource {

    private static final String TAG = "Tag_remoteTask";

    private static TaskRemoteDataSource INSTANCE;
    private final AccessTokenStore mAccessTokenStore;
    private final Context mContext;
    private String mAccessToken;

    private TaskRemoteDataSource(Context context) {
        mContext = context.getApplicationContext();
        mAccessTokenStore = AccessTokenStore.getInstance(context);
    }

    public static TaskDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskRemoteDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void deleteAllTaskHeads(@NonNull DeleteAllCallback callback) {

    }

    @Override
    public void initializeLocalData(@NonNull InitLocalDataCallback callback) {
        /*
        * Implementation for Local only
        * */
    }

    @Override
    public void getTaskHeadDetails(@NonNull final LoadTaskHeadDetailsCallback callback) {

        if (!readyToRequestAPI()) {
            callback.onDataNotAvailable();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        String userId = mAccessTokenStore.getStringValue(AccessTokenStore.USER_ID, "");
        Call<List<TaskHeadDetail_remote>> call = taskService.getTaskHeadDetails(userId);
        call.enqueue(new Callback<List<TaskHeadDetail_remote>>() {
            @Override
            public void onResponse(Call<List<TaskHeadDetail_remote>> call, Response<List<TaskHeadDetail_remote>> response) {
                if (response.code() == HttpStatusCode.TaskHeadStatusCode.OK) {

                    List<TaskHeadDetail> taskHeadDetails = filterFromRemote(response.body());
                    callback.onTaskHeadDetailsLoaded(taskHeadDetails);
                } else if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_TASKHEADS) {

                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<TaskHeadDetail_remote>> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void deleteTaskHeads(List<String> taskheadIds, @NonNull final DeleteTaskHeadsCallback callback) {

        if (!readyToRequestAPI()) {
            callback.onDeleteFail();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);


        DeletingIds_remote ids = new DeletingIds_remote(taskheadIds);
        Call<Void> call = taskService.deleteTaskHeads(ids);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Tag_delete", "deleteTaskHeads success");
                    callback.onDeleteSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag_delete", "deleteTaskHeads fail");
                callback.onDeleteFail();
            }
        });
    }

    /*
    * Separate objects
    * TaskHead, Member, Task
    * */
    private List<TaskHeadDetail> filterFromRemote(List<TaskHeadDetail_remote> taskHeads_remote) {

        List<TaskHeadDetail> taskheadDetails = new ArrayList<>(0);
        for (TaskHeadDetail_remote taskHeadRemote : taskHeads_remote) {

            // Make TaskHead
            List<Member> members = new ArrayList<>();
            List<Task> tasks = new ArrayList<>();

            // Set default orders; 0
            TaskHead newTaskHead = new TaskHead(taskHeadRemote.getId(),
                    taskHeadRemote.getTitle(), 0, taskHeadRemote.getColor());
            // Member
            List<Member_remote> member_remotes = taskHeadRemote.getMembers();
            for (Member_remote member_remote : member_remotes) {
                Member newMember = new Member(member_remote.getId(),
                        newTaskHead.getId(), member_remote.getUserId(),
                        member_remote.getName(), member_remote.getEmail());
                members.add(newMember);
                // Task
                List<Task_remote> task_remotes = member_remote.getTasks();
                for (Task_remote task_remote : task_remotes) {
                    Task newTask = new Task(
                            task_remote.getId(),
                            member_remote.getId(),
                            task_remote.getDescription(),
                            task_remote.getCompleted(),
                            task_remote.getOrder());
                    tasks.add(newTask);
                }
            }
            TaskHeadDetail taskHeadDetail = new TaskHeadDetail(newTaskHead, members, tasks);
            taskheadDetails.add(taskHeadDetail);
        }
        return taskheadDetails;
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

        if (!readyToRequestAPI()) {
            callback.onSaveFailed();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        // Make Object for remote
        List<Member_remote> memberRemotes = new ArrayList<>();
        List<Member> members = taskHeadDetail.getMembers();
        for (Member member : members) {
            Member_remote memberRemote = new Member_remote(
                    member.getId(),
                    member.getUserId(),
                    member.getName(),
                    member.getEmail(),
                    new ArrayList<Task_remote>(0));
            memberRemotes.add(memberRemote);
        }
        TaskHeadDetail_remote taskHeadRemote = new TaskHeadDetail_remote(
                taskHeadDetail.getTaskHead().getId(),
                taskHeadDetail.getTaskHead().getTitle(),
                taskHeadDetail.getTaskHead().getColor(),
                memberRemotes);
        Call<Void> call = taskService.saveTaskHeadDetail(taskHeadRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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
    public void editTaskHeadDetail(@NonNull TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
                                   @NonNull final EditTaskHeadDetailCallback callback) {

        if (!readyToRequestAPI()) {
            callback.onEditFailed();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        // Make Object for remote
        List<Member_remote> memberRemotes = new ArrayList<>();
        for (Member member : addingMembers) {
            Member_remote memberRemote = new Member_remote(
                    member.getId(),
                    member.getUserId(),
                    member.getName(),
                    member.getEmail(),
                    new ArrayList<Task_remote>(0));
            memberRemotes.add(memberRemote);
        }
        TaskHeadDetail_remote editTaskHeadDetailRemote = new TaskHeadDetail_remote(
                editTaskHead.getId(),
                editTaskHead.getTitle(),
                editTaskHead.getColor(),
                memberRemotes);

        Log.d("Tag_editTaskhead", String.valueOf(editTaskHead.getColor()));

        Call<Void> call = taskService.editTaskHeadDetail(editTaskHead.getId(), editTaskHeadDetailRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onEditSuccess();
                } else {
                    callback.onEditFailed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onEditFailed();
            }
        });
    }

    private boolean readyToRequestAPI() {
        // Check network
        if (!NetworkUtils.isOnline(mContext)) {
            return false;
        }

        // Check accessToken
        mAccessToken = mAccessTokenStore.getStringValue(AccessTokenStore.ACCESS_TOKEN, "");
        if (TextUtils.isEmpty(mAccessToken)) {
            Log.d("Tag_TaskRemoteData", "no accessToken");
            return false;
        }
        return true;
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
        if (!readyToRequestAPI()) {
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Task_remote taskRemote = new Task_remote(
                task.getId(), task.getDescription(), task.getCompleted(), task.getOrder());
        Call<Void> call = taskService.saveTask(task.getMemberId(), taskRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        Log.d(TAG, "entered into deleteTask");
        if(!readyToRequestAPI()) {
            return;
        }
        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Call<Void> call = taskService.deleteTask(taskId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "success to delete task");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "fail to delete task");
            }
        });
    }

    @Override
    public void editTasks(@NonNull String taskHeadId, @NonNull Map<String, List<Task>> cachedTasks) {
        if (!readyToRequestAPI()) {
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        // Make the collection for all the tasks of members
        List<MemberTasks> updatingMemberTasks = new ArrayList<>(0);
        for(String memberId:cachedTasks.keySet()) {
            List<Task_remote> taskRemotes = new ArrayList<>(0);
            List<Task> tasks = cachedTasks.get(memberId);
            for(Task task:tasks) {
                Task_remote taskRemote = new Task_remote(
                        task.getId(), task.getDescription(), task.getCompleted(), task.getOrder());
                taskRemotes.add(taskRemote);
            }
            MemberTasks memberTasks = new MemberTasks(memberId, taskRemotes);
            updatingMemberTasks.add(memberTasks);
        }

        Call<Void> call = taskService.editTasks(taskHeadId, updatingMemberTasks);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Tag_remoteTask", "success to edit tasks");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to edit tasks", t);
            }
        });
    }

    @Override
    public void editTasksOfMember(String memberId, List<Task> tasks) {
        if(!readyToRequestAPI()) {
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        List<Task_remote> updatingTasks = new ArrayList<>();
        for(Task task: tasks) {
            updatingTasks.add(new Task_remote(
                    task.getId(), task.getDescription(), task.getCompleted(), task.getOrder()));
        }

        Call<Void> call = taskService.editTasksOfMember(memberId, updatingTasks);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Tag_remoteTask", "success to editTasksOfMember");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to editTasksOfMember");
            }
        });
    }
}
