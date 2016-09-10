package com.kiwi.auready_ver2.taskheads;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.BasePresenter;
import com.kiwi.auready_ver2.BaseView;
import com.kiwi.auready_ver2.data.TaskHead;

import java.util.List;

/**
 * Created by kiwi on 6/26/16.
 */
public interface TaskHeadsContract {

    interface View extends BaseView<Presenter> {

        void setPresenter(TaskHeadsContract.Presenter tasksPresenter);

        void setLoginSuccessUI();

        void showTaskHeads(List<TaskHead> taskHeads);

        void showNoTaskHeads();

        void openTasks(TaskHead requestedTaskHead);

        void showTaskHeadDeleted();

        void showEmptyTaskHeadError();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode, Intent data);

        void loadTaskHeads();

        void editTaskHead(@NonNull TaskHead requestedTaskHead);

        void deleteTaskHead(@NonNull String taskHeadId);

        void deleteTaskHeadByIsEmptyTaskHead(String taskHeadId);

        void saveTaskHead(String taskHeadId, String title);
    }
}
