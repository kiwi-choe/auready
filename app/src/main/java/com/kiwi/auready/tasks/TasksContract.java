package com.kiwi.auready.tasks;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready.BasePresenter;
import com.kiwi.auready.BaseView;
import com.kiwi.auready.data.Member;
import com.kiwi.auready.data.Task;

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
        void onEditTasksOfMemberError();
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

        void createTask(@NonNull Task task, List<Task> editingTasks);

        void result(int requestCode, int resultCode, Intent data);

        void deleteTask(@NonNull final String memberId, @NonNull String taskId, List<Task> editingTasks);

        void editTasks();

        void filterTasks(List<Task> tasks, List<Task> completed, List<Task> uncompleted);

        void updateTasksInMemory(String memberId, List<Task> tasks);

        void getTaskHeadDetailFromRemote();

        void changeComplete(String memberId, String taskId, List<Task> editingTasks);

        void reorder(String memberId, List<Task> tasks);

        void notifyAUReady(String userId);
    }
}
