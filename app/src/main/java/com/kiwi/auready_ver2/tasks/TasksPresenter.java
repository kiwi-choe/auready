package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final TasksContract.View mTasksView;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedList<Task> mTaskList;
    private final GetMembers mGetMembers;
    private final GetTasks mGetTasks;
    private final SaveTask mSaveTask;
    private final DeleteTask mDeleteTask;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetMembers getMembers,
                          @NonNull GetTasks getTasks,
                          @NonNull SaveTask saveTask,
                          @NonNull DeleteTask deleteTask) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetMembers = checkNotNull(getMembers);
        mGetTasks = checkNotNull(getTasks);
        mSaveTask = checkNotNull(saveTask);
        mDeleteTask = checkNotNull(deleteTask);

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
        if (mTaskHeadId != null) {
            populateMembers();
        }
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
    public void getTasks(@NonNull String memberId) {
        checkNotNull(memberId);

        mUseCaseHandler.execute(mGetTasks, new GetTasks.RequestValues(memberId),
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasks.ResponseValue response) {
                        List<Task> tasks = response.getTasks();
                        processTasks(tasks);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void processTasks(List<Task> tasks) {
        if(tasks.isEmpty()) {
            mTasksView.showTasks(tasks);
        } else {
            mTasksView.showNoTasks();
        }
    }

    @Override
    public void createTask(@NonNull String memberId, @NonNull String description, @NonNull int order) {
        Task newTask = new Task(memberId, description, order);
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(newTask),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void editTask(@NonNull String memberId, @NonNull String taskId, @NonNull String description, @NonNull int order) {
        Task task = new Task(taskId, memberId, description, order);
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(task),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void deleteTask(@NonNull String id) {
        checkNotNull(id);
        mUseCaseHandler.execute(mDeleteTask, new DeleteTask.RequestValues(id),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (TasksFragment.REQ_EDIT_TASKHEAD == requestCode && Activity.RESULT_OK == resultCode) {
            if (data.hasExtra(TaskHeadDetailFragment.EXTRA_TITLE)) {
                String title = data.getStringExtra(TaskHeadDetailFragment.EXTRA_TITLE);
                mTasksView.setTitle(title);
            }
        }
    }
}
