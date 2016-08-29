package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.TaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTaskHead;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final TasksContract.View mTasksView;
    private final GetTasks mGetTasks;
    private final SaveTasks mSaveTasks;
    private final SaveTaskHead mSaveTaskHead;

    private final String mTaskHeadId;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetTasks getTasks, @NonNull SaveTasks saveTasks,
                          @NonNull SaveTaskHead saveTaskHead) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTasks = checkNotNull(getTasks, "getTasks cannot be null!");
        mSaveTasks = checkNotNull(saveTasks, "saveTasks cannot be null!");
        mSaveTaskHead = checkNotNull(saveTaskHead, "saveTaskHead cannot be null");

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mTaskHeadId != null) {
            loadTasks();
        } else {
            createTaskHead();
        }
    }

    @Override
    public void loadTasks() {

        mUseCaseHandler.execute(mGetTasks, new GetTasks.RequestValues(),
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
    public boolean isEmptyTaskHead(String taskHeadTitle, List<Task> tasks) {

        if (taskHeadTitle.isEmpty() &&
                (tasks.size() == 0)) {
            return true;
        }
        return false;
    }

    @Override
    public void saveTaskHead(String title, List<Task> tasks) {

        if (isEmptyTaskHead(title, tasks)) {
            mTasksView.showEmptyTasksError();
        }
    }


    private void createTaskHead() {

        TaskHead newTaskHead = new TaskHead();
        mUseCaseHandler.execute(mSaveTaskHead, new SaveTaskHead.RequestValues(newTaskHead),
                new UseCase.UseCaseCallback<SaveTaskHead.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTaskHead.ResponseValue response) {

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
}
