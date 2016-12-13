package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
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

    private final GetTaskHead mGetTaskHead;
    private GetTasks mGetTasks;
    private SaveTask mSaveTask;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedList<Task> mTaskList;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetTaskHead getTaskHead,
                          @NonNull GetTasks getTasks,
                          @NonNull SaveTask saveTask) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTaskHead = checkNotNull(getTaskHead, "getTaskHead cannot be null");
        mGetTasks = checkNotNull(getTasks, "getTasks cannot be null");
        mSaveTask = checkNotNull(saveTask, "saveTask cannot be null");

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
//        if(mTaskHeadId != null) {
//            populateTaskHead();
//            populateTasks();
//        }
    }

    @Override
    public void populateTaskHead() {
        mUseCaseHandler.execute(mGetTaskHead, new GetTaskHead.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTaskHead.ResponseValue response) {
                        showTaskHead(response.getTaskHead());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void getTasks(@NonNull String memberId) {
        checkNotNull(memberId);

        mUseCaseHandler.execute(mGetTasks, new GetTasks.RequestValues(mTaskHeadId, memberId),
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

    @Override
    public void saveTask(@NonNull String memberId, @NonNull String description, @NonNull int order) {
        if (isNewTask()) {
            createTask(memberId, description, order);
        } else {
            // update
        }
    }

    private void createTask(String memberId, String description, int order) {
        Task newTask = new Task(mTaskHeadId, memberId, description, order);

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

    private boolean isNewTask() {
        return true;
    }

    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            mTasksView.showNoTasks();
        } else {
            mTasksView.showTasks(tasks);
        }
    }

    private void showTaskHead(TaskHead taskHead) {
        mTasksView.setTitle(taskHead.getTitle());
        mTasksView.setMembers(taskHead.getMembers());
    }

}
