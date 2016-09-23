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
    private final GetTasks mGetTasks;
    private final SaveTask mSaveTask;
    private final CompleteTask mCompleteTask;
    private final ActivateTask mActivateTask;
    private final SortTasks mSortTasks;
    private final DeleteTask mDeleteTask;
    private final EditDescription mEditDescription;

    private final FilterFactory mFilterFactory;

    private String mTaskHeadId;

    /*
    * Task List that can be controlled - add, delete, modify
    * For TasksAdapter and TaskRepository
    * */
    public LinkedList<Task> mTaskList;

    public TasksPresenter(@NonNull UseCaseHandler useCaseHandler,
                          String taskHeadId,
                          @NonNull TasksContract.View tasksView,
                          @NonNull GetTasks getTasks,
                          @NonNull SaveTask saveTask,
                          @NonNull CompleteTask completeTask, @NonNull ActivateTask activateTask,
                          @NonNull SortTasks sortTasks,
                          @NonNull DeleteTask deleteTask,
                          @NonNull EditDescription editDescription) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTaskHeadId = taskHeadId;
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");

        mGetTasks = checkNotNull(getTasks, "getTasks cannot be null!");
        mSaveTask = checkNotNull(saveTask, "saveTask cannot be null!");
        mCompleteTask = checkNotNull(completeTask, "completeTask cannot be null");
        mActivateTask = checkNotNull(activateTask, "activateTask cannot be null");
        mSortTasks = checkNotNull(sortTasks, "sortTasks cannot be null");
        mDeleteTask = checkNotNull(deleteTask, "deleteTask cannot be null");
        mEditDescription = checkNotNull(editDescription, "editDescription cannot be null");

        mFilterFactory = new FilterFactory();

        mTasksView.setPresenter(this);
        // init mTaskList
        mTaskList = new LinkedList<>();
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
        Log.d("kiwi_test", "called loadTasks()");
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
    public void addTask(@NonNull Task newTask) {
        checkNotNull(newTask);

        int newActiveTaskPosition = newTask.getOrder();

        Log.d("kiwi_test",String.valueOf(newActiveTaskPosition));
        if(newActiveTaskPosition >= mTaskList.size()) {
            mTaskList.add(newTask);
        } else {
            mTaskList.add(newActiveTaskPosition, newTask);
        }

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
        Log.d("kiwi_test", "editedTask order: " + String.valueOf(editedTask.getOrder()));
        // Modify - remove original task and add edited task
        mTaskList.remove(editedTask.getOrder());
        mTaskList.add(editedTask.getOrder(), editedTask);

        mUseCaseHandler.execute(mEditDescription, new EditDescription.RequestValues(editedTask),
                new UseCase.UseCaseCallback<EditDescription.ResponseValue>() {

                    @Override
                    public void onSuccess(EditDescription.ResponseValue response) {
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

        mTaskList.remove(task);

        mUseCaseHandler.execute(mDeleteTask, new DeleteTask.RequestValues(task),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {

                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {
                        sortTasks();
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
