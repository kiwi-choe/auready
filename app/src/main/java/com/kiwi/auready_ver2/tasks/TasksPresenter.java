package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.UseCase;
import com.kiwi.auready_ver2.UseCaseHandler;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.tasks.domain.filter.FilterFactory;
import com.kiwi.auready_ver2.tasks.domain.usecase.ActivateTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.CompleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.DeleteTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.GetTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTask;
import com.kiwi.auready_ver2.tasks.domain.usecase.SaveTasks;
import com.kiwi.auready_ver2.tasks.domain.usecase.SortTasks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kiwi on 8/26/16.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final UseCaseHandler mUseCaseHandler;
    private final TasksContract.View mTasksView;
    private final GetTasks mGetTasks;
    private final SaveTasks mSaveTasks;
    private final SaveTask mSaveTask;
    private final CompleteTask mCompleteTask;
    private final ActivateTask mActivateTask;
    private final SortTasks mSortTasks;
    private final DeleteTask mDeleteTask;

    private final FilterFactory mFilterFactory;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedHashMap<String, Task> mTaskList;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetTasks getTasks,
                          @NonNull SaveTasks saveTasks, @NonNull SaveTask saveTask,
                          @NonNull CompleteTask completeTask, @NonNull ActivateTask activateTask,
                          @NonNull SortTasks sortTasks,
                          @NonNull DeleteTask deleteTask) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTasks = checkNotNull(getTasks, "getTasks cannot be null!");
        mSaveTasks = checkNotNull(saveTasks, "saveTasks cannot be null!");
        mSaveTask = checkNotNull(saveTask, "saveTask cannot be null!");
        mCompleteTask = checkNotNull(completeTask, "completeTask cannot be null");
        mActivateTask = checkNotNull(activateTask, "activateTask cannot be null");
        mSortTasks = checkNotNull(sortTasks, "sortTasks cannot be null");
        mDeleteTask = checkNotNull(deleteTask, "deleteTask cannot be null");

        mFilterFactory = new FilterFactory();

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks();
    }

    @Override
    public void loadTasks() {

        if (mTaskHeadId == null || mTaskHeadId.isEmpty()) {
            Log.d("test", "entered mTaskHeadId is null? or empty");
            mTasksView.showInvalidTaskHeadError();
            return;
        }

        mUseCaseHandler.execute(mGetTasks, new GetTasks.RequestValues(mTaskHeadId),
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasks.ResponseValue response) {
                        List<Task> tasks = response.getTasks();
                        for (Task task : tasks) {
                            Log.d("kiwi_test", "tasks values : " + task.getDescription() +
                                    " iscompleted: " + String.valueOf(task.isCompleted()) +
                                    " order: " + String.valueOf(task.getOrder()));
                        }
                        mTasksView.showTasks(tasks);
                    }

                    @Override
                    public void onError() {

                        Log.d("test", "entered GetTask onError()");
                        mTasksView.showInvalidTaskHeadError();
                    }
                });

    }

    @Override
    public boolean validateEmptyTaskHead(String taskHeadTitle, int numOfTasks) {

        return taskHeadTitle == null ||
                taskHeadTitle.isEmpty() && numOfTasks == 0;
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task, "activeTask cannot be null");
        mUseCaseHandler.execute(mCompleteTask, new CompleteTask.RequestValues(task),
                new UseCase.UseCaseCallback<CompleteTask.ResponseValue>() {

                    @Override
                    public void onSuccess(CompleteTask.ResponseValue response) {
                        loadTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task, "completeTask cannot be null");
        mUseCaseHandler.execute(mActivateTask, new ActivateTask.RequestValues(task),
                new UseCase.UseCaseCallback<ActivateTask.ResponseValue>() {

                    @Override
                    public void onSuccess(ActivateTask.ResponseValue response) {
                        loadTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void saveTasks(List<Task> tasks) {

    }

    @Override
    public void addTask(Task newTask) {
        checkNotNull(newTask);

        if(mTaskList == null) {
            mTaskList = new LinkedHashMap<>();
        }
        mTaskList.put(newTask.getId(), newTask);

        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(newTask),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        sortTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void editTask(@NonNull Task editedTask) {
        checkNotNull(editedTask);

        // Save the existing task
        if(mTaskList.containsKey(editedTask.getId())) {
            mTaskList.put(editedTask.getId(), editedTask);
        }

        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(editedTask),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {

                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        loadTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void deleteTask(@NonNull Task task) {
        checkNotNull(task);
        if(mTaskList != null && mTaskList.containsKey(task.getId())) {
            mTaskList.remove(task.getId());
        }

        mUseCaseHandler.execute(mDeleteTask, new DeleteTask.RequestValues(task),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void sortTasks() {

        mUseCaseHandler.execute(mSortTasks, new SortTasks.RequestValues(mTaskList),
                new UseCase.UseCaseCallback<SortTasks.ResponseValue>() {

                    @Override
                    public void onSuccess(SortTasks.ResponseValue response) {
                        loadTasks();
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
