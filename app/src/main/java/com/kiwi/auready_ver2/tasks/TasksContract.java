package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.tasks.domain.filter.TasksFilterType;

import java.util.ArrayList;
import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class TasksContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showTasks(List<Task> tasks);

        void showInvalidTaskHeadError();

        void showLoadingErrorTasksError();

        void showTaskHeadList(boolean isEmptyTaskHead);
    }

    interface Presenter extends BasePresenter {

        void loadTasks();

        boolean validateEmptyTaskHead(String taskHeadTitle, int numOfTasks);

        void completeTask(@NonNull Task task);

        void activateTask(@NonNull Task task);

        void saveTasks(List<Task> tasks);

        void addTask(@NonNull Task newTask);

        void editTask(@NonNull Task editedTask);

        void deleteTask(String taskId);
    }
}
