package com.kiwi.auready_ver2.tasks;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.Friend;
import com.kiwi.auready_ver2.data.Task;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class TasksContract {

    interface View extends BaseView<Presenter> {

        /*
        * TaskHead
        * */
        void setTitle(String title);

        void setMembers(List<Friend> members);

        /*
        * Tasks
        * */
        void showTasks(List<Task> tasks);

        void showNoTasks();
    }

    interface Presenter extends BasePresenter {

        void populateTaskHead();

        // Get tasks by mTaskHeadId and memberId
        void getTasks(@NonNull String memberId);

        void saveTask(@NonNull String memberId, @NonNull String description, @NonNull int order);
    }
}
