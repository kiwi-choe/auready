package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;

import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Created by kiwi on 6/26/16.
 */
public interface TaskHeadContract {

    interface Presenter {

        void result(int requestCode, int resultCode, Intent data);

        void addNewTask();

        void openTask(TaskHead clickedTaskHead);

        void loadTaskHeads();
    }

    interface View {

        void setPresenter(TaskHeadContract.Presenter tasksPresenter);

        void setLoginSuccessUI();

        void openAddEditTask();

        void showTaskHeads(List<TaskHead> taskHeads);

        void showNoTaskHeads();
    }
}
