package com.kiwi.auready_ver2.tasks;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Member;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
class TasksContract {

    interface View extends BaseView<Presenter> {

        void setTitle(String titleOfTaskHead);

        /*
        * Member
        * */
        void showMembers(List<Member> members);

        /*
        * Tasks
        * */
        void showLoadProgressBar();

        void showTasks(String memberId, List<Task> completed, List<Task> uncompleted);

        void showNoTask(String memberId);

        void setColor(int color);
    }

    interface Presenter extends BasePresenter {

        void populateMembers();

        // Get tasks by memberId
        void getTasksOfMember(@NonNull String memberId);

        void createTask(@NonNull String memberId,
                        @NonNull String description, @NonNull int order);

        void result(int requestCode, int resultCode, Intent data);

        void deleteTask(@NonNull final String memberId, @NonNull String taskId);

        void editTasks();

        void filterTasks(List<Task> tasks, List<Task> completed, List<Task> uncompleted);

        void updateTasksInMemory(String memberId, List<Task> tasks);

        void editTasksOfMember(String memberId, List<Task> tasks);
    }
}
