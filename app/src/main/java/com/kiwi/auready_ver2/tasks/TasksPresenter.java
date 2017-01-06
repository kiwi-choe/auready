package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;

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

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
        if (mTaskHeadId != null) {
            populateTaskHead();
            getTasks();
        }
    }

    @Override
    public void populateTaskHead() {

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
}
