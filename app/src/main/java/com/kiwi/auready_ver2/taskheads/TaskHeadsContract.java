package com.kiwi.auready_ver2.taskheads;

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
    }

    interface Presenter extends BasePresenter {

        void loadTaskHeads();

        void deleteTaskHead(String taskHeadId);

        void addNewTask();
    }
}
