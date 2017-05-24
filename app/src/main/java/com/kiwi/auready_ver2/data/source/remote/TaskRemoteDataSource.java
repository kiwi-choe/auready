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
                if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {

                    List<TaskHeadDetail> taskHeadDetails = filterTaskHeadDetailRemoteList(response.body());
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
    private List<TaskHeadDetail> filterTaskHeadDetailRemoteList(List<TaskHeadDetail_remote> taskHeads_remote) {

        List<TaskHeadDetail> taskheadDetails = new ArrayList<>(0);
        for (TaskHeadDetail_remote taskHeadDetailRemote : taskHeads_remote) {
            TaskHeadDetail taskHeadDetail = filterTaskHeadDetailRemote(taskHeadDetailRemote);
            taskheadDetails.add(taskHeadDetail);
        }
        return taskheadDetails;
    }

    private TaskHeadDetail filterTaskHeadDetailRemote(TaskHeadDetail_remote taskHeadDetailRemote) {

        // Make TaskHead
        List<Member> members = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        // Set default orders; 0
        TaskHead newTaskHead = new TaskHead(taskHeadDetailRemote.getId(),
                taskHeadDetailRemote.getTitle(), 0, taskHeadDetailRemote.getColor());
        // Member
        List<Member_remote> member_remotes = taskHeadDetailRemote.getMembers();
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
        return new TaskHeadDetail(newTaskHead, members, tasks);
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
    public void getTaskHeadDetail(@NonNull String taskHeadId, @NonNull final GetTaskHeadDetailCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onDataNotAvailable();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Call<TaskHeadDetail_remote> call = taskService.getTaskHeadDetail(taskHeadId);
        call.enqueue(new Callback<TaskHeadDetail_remote>() {
            @Override
            public void onResponse(Call<TaskHeadDetail_remote> call, Response<TaskHeadDetail_remote> response) {
                if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    TaskHeadDetail taskHeadDetail = filterTaskHeadDetailRemote(response.body());
                    callback.onTaskHeadDetailLoaded(taskHeadDetail);
                } else if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_TASKHEADS) {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<TaskHeadDetail_remote> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getMembers(@NonNull final String taskHeadId, @NonNull final LoadMembersCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onDataNotAvailable();
        }
        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);
        Call<List<Member_remote>> call = taskService.getMembers(taskHeadId);
        call.enqueue(new Callback<List<Member_remote>>() {
            @Override
            public void onResponse(Call<List<Member_remote>> call, Response<List<Member_remote>> response) {
                if (response.isSuccessful()) {
                    List<Member> members = convertMemberRemoteToMember(taskHeadId, response.body());
                    callback.onMembersLoaded(members);
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<Member_remote>> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }

    // Convert to Local model Member
    private List<Member> convertMemberRemoteToMember(String taskHeadId, List<Member_remote> memberRemotes) {
        List<Member> members = new ArrayList<>();
        for (Member_remote memberRemote : memberRemotes) {
            members.add(new Member(
                    memberRemote.getId(),
                    taskHeadId,
                    memberRemote.getUserId(),
                    memberRemote.getName(),
                    memberRemote.getEmail()));
        }
        return members;
    }

    @Override
    public void getTasksOfMember(@NonNull final String memberId, @NonNull final LoadTasksCallback callback) {
        if (!readyToRequestAPI()) {
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Call<List<Task_remote>> call = taskService.getTasksOfMember(memberId);
        call.enqueue(new Callback<List<Task_remote>>() {
            @Override
            public void onResponse(Call<List<Task_remote>> call, Response<List<Task_remote>> response) {
                if(response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    List<Task> tasks = convertTasksRemoteToTasks(memberId, response.body());
                    callback.onTasksLoaded(tasks);
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onFailure(Call<List<Task_remote>> call, Throwable t) {
                callback.onDataNotAvailable();
            }
        });
    }

    private List<Task> convertTasksRemoteToTasks(String memberId, List<Task_remote> taskRemotes) {
        List<Task> tasks = new ArrayList<>();
        for(Task_remote remote: taskRemotes) {
            Task task = new Task(
                    remote.getId(), memberId, remote.getDescription(), remote.getCompleted(), remote.getOrder());

            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public void getTasksOfTaskHead(@NonNull String taskheadId, @NonNull LoadTasksCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task, @NonNull final SaveTaskCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onSaveFailed();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Task_remote taskRemote = new Task_remote(
                task.getId(), task.getDescription(), task.getCompleted(), task.getOrder());
        Call<Void> call = taskService.saveTask(task.getMemberId(), taskRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    callback.onSaveSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void deleteTask(@NonNull String taskId, @NonNull final DeleteTaskCallback callback) {
        Log.d(TAG, "entered into deleteTask");
        if (!readyToRequestAPI()) {
            return;
        }
        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Call<Void> call = taskService.deleteTask(taskId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onDeleteSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onDeleteFailed();
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
        for (String memberId : cachedTasks.keySet()) {
            List<Task_remote> taskRemotes = new ArrayList<>(0);
            List<Task> tasks = cachedTasks.get(memberId);
            for (Task task : tasks) {
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
    public void saveMembers(List<Member> members) {

    }

    @Override
    public void changeComplete(Task editedTask) {
        if(!readyToRequestAPI()) {
            return;
        }
        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Task_remote taskRemote = new Task_remote(
                editedTask.getId(), editedTask.getDescription(), editedTask.getCompleted(), editedTask.getOrder());
        Call<Void> call = taskService.changeCompleted(editedTask.getId(), taskRemote);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("Tag_changeComplete", "success to edit a task");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag_changeComplete", "fail to edit a task");
            }
        });
    }

    @Override
    public void deleteMembers(String taskHeadId, DeleteMembersCallback callback) {
        // Implement to Local
    }

    @Override
    public void editTasksOfMember(String memberId, List<Task> tasks,
                                  @NonNull final EditTasksOfMemberCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onEditFail();
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        List<Task_remote> updatingTasks = new ArrayList<>();
        for (Task task : tasks) {
            updatingTasks.add(new Task_remote(
                    task.getId(), task.getDescription(), task.getCompleted(), task.getOrder()));
        }

        Call<Void> call = taskService.editTasksOfMember(memberId, updatingTasks);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_MEMBER) {
                    // Request getting latest updated taskHeads
                    Log.d("Tag_remoteTask", "editTasksOfMember; no member");
                    callback.onEditFail();
                } else if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    Log.d("Tag_remoteTask", "success to editTasksOfMember");
                    callback.onEditSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onEditFail();
                Log.d("Tag_remoteTask", "fail to editTasksOfMember");
            }
        });
    }

    @Override
    public void refreshLocalTaskHead() {
        // Not required because the {@link TaskRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }
}
