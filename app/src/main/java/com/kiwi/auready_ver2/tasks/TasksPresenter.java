package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfMember;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasksOfTaskHead;
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
    private final GetTasksOfMember mGetTasksOfMember;
    private final GetTasksOfTaskHead mGetTasksOfTaskHead;
    private final SaveTask mSaveTask;
    private final DeleteTask mDeleteTask;

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
                          @NonNull GetTasksOfMember getTasksOfMember,
                          @NonNull SaveTask saveTask,
                          @NonNull DeleteTask deleteTask,
                          @NonNull GetTasksOfTaskHead getTasksOfTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTaskHead = checkNotNull(getTaskHead, "getTaskHead cannot be null");
        mGetTasksOfMember = checkNotNull(getTasksOfMember, "getTasksOfMember cannot be null");
        mSaveTask = checkNotNull(saveTask, "createTask cannot be null");
        mDeleteTask = checkNotNull(deleteTask, "deleteTask cannot be null");
        mGetTasksOfTaskHead = checkNotNull(getTasksOfTaskHead);

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
    }

    @Override
    public void start() {
        if(mTaskHeadId != null) {
            populateTaskHead();
        }
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

        mUseCaseHandler.execute(mGetTasksOfMember, new GetTasksOfMember.RequestValues(mTaskHeadId, memberId),
                new UseCase.UseCaseCallback<GetTasksOfMember.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTasksOfMember.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void getTasks() {
        mUseCaseHandler.execute(mGetTasksOfTaskHead, new GetTasksOfTaskHead.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetTasksOfTaskHead.ResponseValue>() {

                    @Override
                    public void onSuccess(GetTasksOfTaskHead.ResponseValue response) {
                        List<Task> tasks = response.getTasks();
                        processTasks(tasks);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void createTask(@NonNull String memberId, @NonNull String description, @NonNull int order) {
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

    @Override
    public void updateTask(@NonNull String memberId, @NonNull String taskId, @NonNull String description, @NonNull int order) {
        checkNotNull(taskId);
        Task newTask = new Task(mTaskHeadId, memberId, taskId, description, order);
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
    public void deleteTask(@NonNull String id) {
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

    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            mTasksView.showNoTasks();
        } else {
            mTasksView.showTasks(tasks);
        }
    }

    private void showTaskHead(TaskHead taskHead) {

        if(taskHead.getMembers().size() == 0) {

            Log.d("TEST_TAG", "there is no member");
        }
        mTasksView.setTitle(taskHead.getTitle());
        mTasksView.setMembers(taskHead.getMembers());
    }
}
