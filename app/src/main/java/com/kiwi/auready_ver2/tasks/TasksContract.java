package com.kiwi.auready_ver2.tasks;

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

        void showActiveTasks(List<Task> tasks);

        void showCompletedTasks(List<Task> tasks);

        void showEmptyTasksError();

        void showLoadingErrorTasksError();
    }

    interface Presenter extends BasePresenter {

        void loadTasks();

        boolean isEmptyTaskHead(String taskHeadTitle, List<Task> tasks);

        void saveTasks(String title, List<Task> tasks);

        void saveTask(Task task);
    }
}
