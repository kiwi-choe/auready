package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.R;
import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHeadDetail;
import com.kiwi.auready_ver2.tasks.domain.usecase.ChangeComplete;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private static final int DEFAULT_COLOR = R.color.color_picker_default_color;

    private final UseCaseHandler mUseCaseHandler;
    private final TasksContract.View mTasksView;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    Map<String, List<Task>> mCachedTasks = new LinkedHashMap<>();

    private final GetMembers mGetMembers;
    private final GetTasksOfMember mGetTasksOfMember;
    private final SaveTask mSaveTask;
    private final DeleteTask mDeleteTask;
    private final EditTasks mEditTasks;
    private final EditTasksOfMember mEditTasksOfMember;
    private final GetTaskHeadDetail mGetTaskHeadDetail;
    private final ChangeComplete mChangeCompleted;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          @NonNull String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetMembers getMembers,
                          @NonNull GetTasksOfMember getTasksOfMember,
                          @NonNull SaveTask saveTask,
                          @NonNull DeleteTask deleteTask,
                          @NonNull EditTasks editTasks,
                          @NonNull EditTasksOfMember editTasksOfMember,
                          @NonNull GetTaskHeadDetail getTaskHeadDetail,
                          @NonNull ChangeComplete changeCompleted) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = checkNotNull(taskHeadId, "taskHeadId cannot be null!");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetMembers = checkNotNull(getMembers);
        mGetTasksOfMember = checkNotNull(getTasksOfMember);
        mSaveTask = checkNotNull(saveTask);
        mDeleteTask = checkNotNull(deleteTask);
        mEditTasks = checkNotNull(editTasks);
        mEditTasksOfMember = editTasksOfMember;

        mGetTaskHeadDetail = getTaskHeadDetail;
        mChangeCompleted = changeCompleted;

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mTasksView != null) {
            mTasksView.showLoadProgressBar();
        }
        populateMembers();
    }

    @Override
    public void populateMembers() {
        mUseCaseHandler.execute(mGetMembers, new GetMembers.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetMembers.ResponseValue>() {

                    @Override
                    public void onSuccess(GetMembers.ResponseValue response) {
                        mTasksView.showMembers(response.getMembers());
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public void getTasksOfMember(@NonNull final String memberId) {
        checkNotNull(memberId);

        // we need copy of GetTasksOfMember instance to support asynchronous getTasksOfMember method call
        mUseCaseHandler.execute(new GetTasksOfMember(mGetTasksOfMember), new GetTasksOfMember.RequestValues(memberId),
                new UseCase.UseCaseCallback<GetTasksOfMember.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasksOfMember.ResponseValue response) {
                        if (response.getTasks().size() != 0) {
                            Log.d("Tag_getTasksOfMember", String.valueOf(response.getTasks().size()));
                            filterTasks(response.getTasks(), new ArrayList<Task>(), new ArrayList<Task>());
                        } else {
                            mTasksView.showNoTask(memberId);
                        }
                    }

                    @Override
                    public void onError() {
                        mTasksView.showNoTask(memberId);
                    }
                });
    }

    @Override
    public void createTask(@NonNull final String memberId, @NonNull String description, @NonNull int order) {
        Task newTask = new Task(memberId, description, order);
        mUseCaseHandler.execute(new SaveTask(mSaveTask), new SaveTask.RequestValues(newTask),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        getTasksOfMember(memberId);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (TasksActivity.REQ_EDIT_TASKHEAD == requestCode && Activity.RESULT_OK == resultCode) {
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_TITLE)) {
                String title = data.getStringExtra(TaskHeadDetailFragment.EXTRA_TITLE);
                mTasksView.setTitle(title);
            }
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_COLOR)) {
                int color = data.getIntExtra(TaskHeadDetailFragment.EXTRA_COLOR, DEFAULT_COLOR);
                mTasksView.setColor(color);
            }
        }
    }

    @Override
    public void deleteTask(@NonNull final String memberId, @NonNull String taskId) {

        mUseCaseHandler.execute(new DeleteTask(mDeleteTask), new DeleteTask.RequestValues(taskId),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {
                        getTasksOfMember(memberId);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /*
    * Edit tasks of a taskhead*/
    @Override
    public void editTasks() {
        mUseCaseHandler.execute(mEditTasks, new EditTasks.RequestValues(mTaskHeadId, mCachedTasks),
                new UseCase.UseCaseCallback<EditTasks.ResponseValue>() {

                    @Override
                    public void onSuccess(EditTasks.ResponseValue response) {
                        refreshCachedTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    // Remove all tasks in memory
    private void refreshCachedTasks() {
        mCachedTasks.clear();
    }

    @Override
    public void filterTasks(List<Task> tasks, List<Task> completed, List<Task> uncompleted) {
        if (tasks.size() <= 0) {
            return;
        }

        for (Task task : tasks) {
            if (task.isCompleted()) {
                if (task.getOrder() >= completed.size()) {
                    completed.add(task);
                } else {
                    completed.add(task.getOrder(), task);
                }
            } else {
                if (task.getOrder() >= uncompleted.size()) {
                    uncompleted.add(task);
                } else {
                    uncompleted.add(task.getOrder(), task);
                }
            }
        }

        mTasksView.showTasks(tasks.get(0).getMemberId(), completed, uncompleted);
    }

    @Override
    public void updateTasksInMemory(String memberId, List<Task> tasks) {

        mCachedTasks.put(memberId, tasks);
    }

    @Override
    public void editTasksOfMember(String memberId, List<Task> tasks) {

        mUseCaseHandler.execute(mEditTasksOfMember, new EditTasksOfMember.RequestValues(memberId, tasks),
                new UseCase.UseCaseCallback<EditTasksOfMember.ResponseValue>() {

                    @Override
                    public void onSuccess(EditTasksOfMember.ResponseValue response) {

                    }

                    @Override
                    public void onError() {
                        // Request getting latest updated taskHeads
                        // Refresh TasksActivity
                        mTasksView.onEditTasksOfMemberError();
                    }
                });
    }

    @Override
    public void getTaskHeadDetailFromRemote() {
        boolean forceToUpdate = true;
        mUseCaseHandler.execute(mGetTaskHeadDetail, new GetTaskHeadDetail.RequestValues(mTaskHeadId, forceToUpdate),
                new UseCase.UseCaseCallback<GetTaskHeadDetail.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHeadDetail.ResponseValue response) {
                        setTaskHead(response.getTaskHeadDetail().getTaskHead());
                        mTasksView.showMembers(response.getTaskHeadDetail().getMembers());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void changeComplete(Task editedTask) {
        mUseCaseHandler.execute(mChangeCompleted, new ChangeComplete.RequestValues(editedTask),
                new UseCase.UseCaseCallback<ChangeComplete.ResponseValue>() {

                    @Override
                    public void onSuccess(ChangeComplete.ResponseValue response) {
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void setTaskHead(TaskHead taskHead) {
        mTasksView.setTitle(taskHead.getTitle());
        mTasksView.setColor(taskHead.getColor());
    }
}
