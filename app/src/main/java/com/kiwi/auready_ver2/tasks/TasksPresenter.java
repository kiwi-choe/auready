package com.kiwi.auready_ver2.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.taskheaddetail.TaskHeadDetailFragment;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetMembers;

import java.util.LinkedList;

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

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetMembers getMembers) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetMembers = checkNotNull(getMembers);

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
        if (mTaskHeadId != null) {
//            populateMembers();
//            getTasks();
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

    }

    @Override
    public void getTasks() {

    }

    @Override
    public void createTask(@NonNull String memberId, @NonNull String description, @NonNull int order) {

    }

    @Override
    public void updateTask(@NonNull String memberId, @NonNull String taskId, @NonNull String description, @NonNull int order) {

    }

    @Override
    public void deleteTask(@NonNull String id) {

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
