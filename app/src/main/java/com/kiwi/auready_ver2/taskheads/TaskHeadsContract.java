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

        void showTaskHeads(List<TaskHead> taskHeads);

        void showNoTaskHeads();

        void showTaskHeadDetail(int cntOfTaskHeads);

        void showTasksView(String taskHeadId, String title, int color);

        void setRefreshing(boolean refreshing);

        void showAccountView();
    }

    interface Presenter extends BasePresenter {

        /*
        * forceToUpdate; true - getting from Remote first and refresh Local
        *               false - getting from Local first
        * */
        void loadTaskHeads(boolean forceToUpdate);

        void addNewTaskHead();

        void result(int requestCode, int resultCode, Intent data);

        void deleteTaskHeads(List<TaskHead> taskheads);

        void updateOrders(List<TaskHead> taskheads);
    }
}
