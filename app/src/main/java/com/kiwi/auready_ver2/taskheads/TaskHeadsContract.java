package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Created by kiwi on 6/26/16.
 */
public interface TaskHeadsContract {

    interface View extends BaseView<Presenter> {

        void setLoginSuccessUI();

        void showTaskHeads(List<TaskHead> taskHeads);

        void showNoTaskHeads();

        void showAddTaskHead();

        void showTasksView(String taskHeadId);
    }

    interface Presenter extends BasePresenter {

        void loadTaskHeads();

        void deleteTaskHead(String taskHeadId);

        void addNewTask();

        void result(int requestCode, int resultCode, Intent data);
    }
}
