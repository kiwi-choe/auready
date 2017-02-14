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
        void showTasks(List<Task> tasks);

        void showTasks(String memberId, List<Task> tasks);

        void showNoTasks();

        void scrollToAddButton();
    }

    interface Presenter extends BasePresenter {

        void populateMembers();

        // Get tasks by memberId
        void getTasksOfMember(@NonNull String memberId);
        // Get tasks of all members
        void getTasksOfTaskHead(@NonNull String taskHeadId);


        void createTask(@NonNull String memberId,
                        @NonNull String description, @NonNull int order);

        void result(int requestCode, int resultCode, Intent data);

        void deleteTasks(@NonNull List<String> taskIds);

        void editTasks(@NonNull List<Task> tasks);
    }
}
