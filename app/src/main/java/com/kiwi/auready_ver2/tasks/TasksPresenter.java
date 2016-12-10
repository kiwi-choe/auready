package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.taskheaddetail.domain.usecase.GetTaskHead;
import com.kiwi.auready_ver2.tasks.domain.filter.FilterFactory;
import com.kiwi.auready_ver2.tasks.domain.usecase.ActivateTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.CompleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.EditDescription;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.SortTasks;

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

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedList<Task> mTaskList;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetTaskHead getTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTaskHead = checkNotNull(getTaskHead, "getTaskHead cannot be null");

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

    private void showTaskHead(TaskHead taskHead) {
        mTasksView.setTitle(taskHead.getTitle());
        mTasksView.setMembers(taskHead.getMembers());
    }

}
