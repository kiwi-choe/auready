package com.kiwi.auready_ver2.tasks;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class TasksContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();

        void showTasks(List<Task> tasks);

        void showNoTasks();

        void showEmptyTasksError();
    }

    interface Presenter extends BasePresenter {

        void loadTasks();

        boolean isEmptyTaskHead(String taskHeadTitle, List<Task> tasks);

        void saveTasks(String title, List<Task> tasks);
    }
}
