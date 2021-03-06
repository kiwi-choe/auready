package com.kiwi.auready.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.Task;
import com.kiwi.auready.data.TaskHead;
import com.kiwi.auready.data.TaskHeadDetail;
import com.kiwi.auready.data.source.TaskDataSource;
import com.kiwi.auready.data.source.local.AccessTokenStore;
import com.kiwi.auready.rest_service.HttpStatusCode;
import com.kiwi.auready.rest_service.ServiceGenerator;
import com.kiwi.auready.rest_service.task.AddTaskData;
import com.kiwi.auready.rest_service.task.DeletingIds_remote;
import com.kiwi.auready.rest_service.task.ITaskService;
import com.kiwi.auready.rest_service.task.Member_remote;
import com.kiwi.auready.rest_service.task.Order_remote;
import com.kiwi.auready.rest_service.task.TaskHeadDetail_remote;
import com.kiwi.auready.rest_service.task.Task_remote;
import com.kiwi.auready.rest_service.task.UpdatingOrder_remote;
import com.kiwi.auready.tasks.MemberTasks;
import com.kiwi.auready.util.NetworkUtils;

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
            return;
        }

        Log.d("Tag_network", "if no network connection, should not enter to here");

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
            return;
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

        int order = 0;
        String currentUserId = AccessTokenStore.getInstance(mContext).getStringValue(AccessTokenStore.USER_ID, "");
        for (Order_remote order_remote : taskHeadDetailRemote.getOrders()) {
            if (order_remote.getUserId().equals(currentUserId)) {
                order = order_remote.getOrderNum();
                break;
            }
        }
        // Set default orders; 0
        TaskHead newTaskHead = new TaskHead(
                taskHeadDetailRemote.getId(),
                taskHeadDetailRemote.getTitle(),
                order,
                taskHeadDetailRemote.getColor());
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
    public void updateTaskHeadOrders(@NonNull List<TaskHead> taskHeads, @NonNull final UpdateTaskHeadOrdersCallback callback) {

        if (!readyToRequestAPI()) {
            return;
        }

        // Make jsonData
        List<UpdatingOrder_remote> updatingTaskHeadOrders = new ArrayList<>();
        for (TaskHead taskHead : taskHeads) {
            UpdatingOrder_remote updatingOrder = new UpdatingOrder_remote(taskHead.getId(), taskHead.getOrder());
            updatingTaskHeadOrders.add(updatingOrder);
        }
        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);
        Call<Void> call = taskService.updateTaskHeadOrders(updatingTaskHeadOrders);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "updateTaskHeadOrders succeeded");
                    callback.onUpdateSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "updateTaskHeadOrders failed");
                callback.onUpdateFailed();
            }
        });
    }

    @Override
    public void saveTaskHeadDetails(@NonNull List<TaskHeadDetail> taskHeadDetails, @NonNull SaveTaskHeadDetailsCallback callback) {

    }

    @Override
    public void saveTaskHeadDetail(@NonNull TaskHeadDetail taskHeadDetail, @NonNull final SaveCallback callback) {

        if (!readyToRequestAPI()) {
            callback.onSaveFailed();
            return;
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
        // Make orders
        List<Order_remote> orders = new ArrayList<>();
        for (Member member : members) {
            String currentUserId = AccessTokenStore.getInstance(mContext).getStringValue(AccessTokenStore.USER_ID, "");
            Order_remote orderRemote;
            if (member.getUserId().equals(currentUserId)) {
                orderRemote = new Order_remote(member.getUserId(), taskHeadDetail.getTaskHead().getOrder());
            } else {
                orderRemote = new Order_remote(member.getUserId(), 0);
            }
            orders.add(orderRemote);
        }
        TaskHeadDetail_remote taskHeadRemote = new TaskHeadDetail_remote(
                taskHeadDetail.getTaskHead().getId(),
                taskHeadDetail.getTaskHead().getTitle(),
                taskHeadDetail.getTaskHead().getColor(),
                orders,
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
    public void editTaskHeadDetail(@NonNull final TaskHead editTaskHead,
                                   @NonNull List<Member> addingMembers,
                                   @NonNull final EditTaskHeadDetailCallback callback) {

        if (!readyToRequestAPI()) {
            callback.onEditFailed();
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        // Make Object for remote
        List<Member_remote> memberRemotes = new ArrayList<>();
        List<Order_remote> orders = new ArrayList<>();
        for (Member member : addingMembers) {
            Member_remote memberRemote = new Member_remote(
                    member.getId(),
                    member.getUserId(),
                    member.getName(),
                    member.getEmail(),
                    new ArrayList<Task_remote>(0));
            memberRemotes.add(memberRemote);

            // Make orders
            Order_remote orderRemote = new Order_remote(member.getUserId(), 0);
            orders.add(orderRemote);
        }

        TaskHeadDetail_remote editTaskHeadDetailRemote = new TaskHeadDetail_remote(
                editTaskHead.getId(),
                editTaskHead.getTitle(),
                editTaskHead.getColor(),
                orders,
                memberRemotes);

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
            return;
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
            return;
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
                if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
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
        for (Task_remote remote : taskRemotes) {
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
    public void deleteTask(final String memberId, @NonNull String taskId, @NonNull List<Task> editingTasks, @NonNull final DeleteTaskCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onDeleteFailed();
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        List<Task_remote> editingTasks_remote = new ArrayList<>();
        for (Task editingTask : editingTasks) {
            editingTasks_remote.add(new Task_remote(
                    editingTask.getId(),
                    editingTask.getDescription(),
                    editingTask.getCompleted(),
                    editingTask.getOrder()));
        }
        Call<List<Task_remote>> call = taskService.deleteTask(memberId, taskId, editingTasks_remote);
        Log.d("Tag_remote", "entered into DeleteTask in Remote");
        call.enqueue(new Callback<List<Task_remote>>() {
            @Override
            public void onResponse(Call<List<Task_remote>> call, Response<List<Task_remote>> response) {
                if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_MEMBER) {
                    // Request getting latest updated taskHeads
                    Log.d("Tag_remoteTask", "deleteTask; no member");
                    callback.onDeleteFailed();
                } else if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    Log.d("Tag_remoteTask", "success to deleteTask");
                    List<Task> tasks = convertTasksRemoteToTasks(memberId, response.body());
                    callback.onDeleteSuccess(tasks);
                }
            }

            @Override
            public void onFailure(Call<List<Task_remote>> call, Throwable t) {
                Log.d("Tag_remoteTask", "deleteTask is failed");
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
                if (response.isSuccessful()) {
                    Log.d("Tag_remoteTask", "success to edit tasks");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to edit tasks", t);
            }
        });
    }

    @Override
    public void saveTask(@NonNull final Task task, @NonNull List<Task> editingTasks, @NonNull final SaveTaskCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onSaveFailed();
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        Task_remote newTask = new Task_remote(
                task.getId(), task.getDescription(), task.getCompleted(), task.getOrder());

        List<Task_remote> editingTasks_remote = new ArrayList<>();
        for (Task editingTask : editingTasks) {
            editingTasks_remote.add(new Task_remote(
                    editingTask.getId(),
                    editingTask.getDescription(),
                    editingTask.getCompleted(),
                    editingTask.getOrder()));
        }
        AddTaskData addTaskData = new AddTaskData(newTask, editingTasks_remote);

        Call<List<Task_remote>> call = taskService.addTask(task.getMemberId(), addTaskData);
        call.enqueue(new Callback<List<Task_remote>>() {
            @Override
            public void onResponse(Call<List<Task_remote>> call, Response<List<Task_remote>> response) {
                if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_MEMBER) {
                    // Request getting latest updated taskHeads
                    Log.d("Tag_remoteTask", "saveTask; no member");
                    callback.onSaveFailed();
                } else if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    Log.d("Tag_remoteTask", "success to saveTask");
                    List<Task> tasks = convertTasksRemoteToTasks(task.getMemberId(), response.body());
                    callback.onSaveSuccess(tasks);
                }
            }

            @Override
            public void onFailure(Call<List<Task_remote>> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to saveTask");
                callback.onSaveFailed();
            }
        });
    }

    @Override
    public void changeComplete(final String memberId, String taskId, List<Task> editingTasks, final ChangeCompleteTaskCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onChangeCompleteFail();
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        List<Task_remote> updatingTasks = new ArrayList<>();
        for (Task task : editingTasks) {
            updatingTasks.add(new Task_remote(
                    task.getId(), task.getDescription(), task.getCompleted(), task.getOrder()));
        }

        Call<List<Task_remote>> call = taskService.changeComplete(memberId, taskId, updatingTasks);
        call.enqueue(new Callback<List<Task_remote>>() {
            @Override
            public void onResponse(Call<List<Task_remote>> call, Response<List<Task_remote>> response) {

                if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_MEMBER) {
                    // Request getting latest updated taskHeads
                    Log.d("Tag_remoteTask", "changeCompleted; no member");
                    callback.onChangeCompleteFail();
                } else if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    Log.d("Tag_remoteTask", "success to changeComplete");
                    List<Task> tasks = convertTasksRemoteToTasks(memberId, response.body());
                    callback.onChangeCompleteSuccess(tasks);
                }
            }

            @Override
            public void onFailure(Call<List<Task_remote>> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to changeComplete");
                callback.onChangeCompleteFail();
            }
        });
    }

    @Override
    public void changeOrders(final String memberId, List<Task> editingTasks, final ChangeOrdersCallback callback) {
        if (!readyToRequestAPI()) {
            callback.onChangeOrdersFail();
            return;
        }

        ITaskService taskService =
                ServiceGenerator.createService(ITaskService.class, mAccessToken);

        List<Task_remote> updatingTasks = new ArrayList<>();
        for (Task task : editingTasks) {
            updatingTasks.add(new Task_remote(
                    task.getId(), task.getDescription(), task.getCompleted(), task.getOrder()));
        }

        Call<List<Task_remote>> call = taskService.changeOrders(memberId, updatingTasks);
        call.enqueue(new Callback<List<Task_remote>>() {
            @Override
            public void onResponse(Call<List<Task_remote>> call, Response<List<Task_remote>> response) {
                if (response.code() == HttpStatusCode.TaskHeadStatusCode.NO_MEMBER) {
                    // Request getting latest updated taskHeads
                    Log.d("Tag_remoteTask", "changeOrders; no member");
                    callback.onChangeOrdersFail();
                } else if (response.code() == HttpStatusCode.BasicStatusCode.OK_GET) {
                    Log.d("Tag_remoteTask", "success to changeOrders");
                    List<Task> tasks = convertTasksRemoteToTasks(memberId, response.body());
                    callback.onChangeOrdersSuccess(tasks);
                }
            }

            @Override
            public void onFailure(Call<List<Task_remote>> call, Throwable t) {
                Log.d("Tag_remoteTask", "fail to changeOrders");
                callback.onChangeOrdersFail();
            }
        });
    }

    /*
    * unimplemented in Remote
    * */
    @Override
    public void saveMembers(List<Member> members) {

    }

    @Override
    public void deleteTasksOfMember(String memberId) {

    }

    @Override
    public void deleteMembers(String taskHeadId, DeleteMembersCallback callback) {
        // Implement to Local
    }

    @Override
    public void forceUpdateLocalATaskHeadDetail() {
        // Not required because the {@link TaskRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }
}
